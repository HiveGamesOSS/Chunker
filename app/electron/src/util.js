import fs from "fs/promises";
import path from "path";
import fsSync from "fs";
import zlib from "zlib";
import {once} from "events";
import {pipeline} from "stream/promises";
import archiver from "archiver";
import tarStream from "tar-stream";

export async function countFiles(inputPath) {
    const files = await fs.readdir(inputPath);

    // Loop through and count all the nested file sizes
    return (await Promise.all(files.map(async (fileName) => {
        const filePath = path.join(inputPath, fileName);
        const stats = await fs.stat(filePath);

        if (stats.isFile()) {
            return 1;
        } else if (stats.isDirectory()) {
            return await countFiles(filePath); // Recursively count files in subdirectory
        } else {
            return 0;
        }
    }))).reduce((a, b) => a + b, 0);
}

export async function copyRecursive(inputPath, outputPath, singleFileCallback) {
    const files = await fs.readdir(inputPath);
    // Create directory
    await fs.mkdir(outputPath, {recursive: true});

    // Loop through contents
    await Promise.all(files.map(async (fileName) => {
        const filePath = path.join(inputPath, fileName);
        const outputFilePath = path.join(outputPath, fileName);

        // If it's a file copy it otherwise copy the directory
        const stats = await fs.stat(filePath);
        if (stats.isFile()) {
            await fs.copyFile(filePath, outputFilePath);
            singleFileCallback(filePath);
        } else if (stats.isDirectory()) {
            await copyRecursive(filePath, outputFilePath, singleFileCallback);
        }
    }));
}

export async function zipRecursive(inputPath, outputPath) {
    let output = fsSync.createWriteStream(outputPath);
    let archive = archiver("zip", {zlib: {level: 9}});
    // Pipe to our output
    archive.pipe(output);

    // Add all the files
    await addToZipRecursive(archive, inputPath, "");

    // Finalize zip
    await archive.finalize();
}

export async function addToZipRecursive(archive, inputPath, outputPath) {
    const files = await fs.readdir(inputPath);
    if (outputPath.length > 0) {
        // Create directory
        archive.directory(outputPath);
    }

    // Loop through contents
    await Promise.all(files.map(async (fileName) => {
        const filePath = path.join(inputPath, fileName);
        const outputFilePath = path.join(outputPath, fileName);

        // If it's a file copy it otherwise copy the directory
        const stats = await fs.stat(filePath);
        if (stats.isFile()) {
            archive.file(filePath, {name: outputFilePath})
        } else if (stats.isDirectory()) {
            await addToZipRecursive(archive, filePath, outputFilePath);
        }
    }));
}

// Read the first bytes of a file so we can identify the archive type from its header.
async function readArchiveHeader(inputPath, length) {
    const handle = await fs.open(inputPath, "r");
    try {
        const header = Buffer.alloc(length);
        const {bytesRead} = await handle.read(header, 0, length, 0);
        return header.subarray(0, bytesRead);
    } finally {
        await handle.close();
    }
}

// Detect whether the provided file is a zip or tar archive (or neither) using its magic bytes.
export async function detectArchiveType(inputPath) {
    const header = await readArchiveHeader(inputPath, 512);

    // Zip archives (also .mcworld and .mctemplate) start with "PK"
    if (header[0] === 0x50 && header[1] === 0x4b) {
        return "zip";
    }

    // Gzip compressed tarballs (.tar.gz / .tgz)
    if (header[0] === 0x1f && header[1] === 0x8b) {
        return "tar";
    }

    // Uncompressed tar archives contain "ustar" at offset 257
    if (header.length >= 262 && header.toString("ascii", 257, 262) === "ustar") {
        return "tar";
    }

    // Brotli has no reliable magic bytes, so fall back to the file extension
    const lower = inputPath.toLowerCase();
    if (lower.endsWith(".tar") || lower.endsWith(".tar.gz") || lower.endsWith(".tgz") ||
        lower.endsWith(".tar.br") || lower.endsWith(".tbr")) {
        return "tar";
    }

    return "unknown";
}

// Pipe the tar archive through any decompression and stream each entry to the given handler.
async function streamTar(inputPath, onEntry) {
    // Gzip is detected from the header so a compressed tarball is handled regardless of its name
    const header = await readArchiveHeader(inputPath, 2);
    const isGzip = header[0] === 0x1f && header[1] === 0x8b;
    const lower = inputPath.toLowerCase();
    const isBrotli = !isGzip && (lower.endsWith(".br") || lower.endsWith(".tbr"));

    return new Promise((resolve, reject) => {
        const extract = tarStream.extract();
        extract.on("entry", (entryHeader, stream, next) => {
            Promise.resolve(onEntry(entryHeader, stream))
                .then(() => next())
                .catch((err) => {
                    stream.resume();
                    reject(err);
                });
        });
        extract.on("finish", resolve);
        extract.on("error", reject);

        const raw = fsSync.createReadStream(inputPath);
        raw.on("error", reject);

        let source = raw;
        if (isGzip) {
            source = raw.pipe(zlib.createGunzip());
        } else if (isBrotli) {
            source = raw.pipe(zlib.createBrotliDecompress());
        }
        source.on("error", reject);
        source.pipe(extract);
    });
}

// Extract a tar archive (optionally gzip / brotli compressed) into the output directory.
// The contents below the level.dat are extracted, mirroring the behaviour for zip archives.
export async function extractTar(inputPath, outputPath, progressCallback) {
    // First pass: find the level.dat and record the entries so we can determine the world root
    let entries = [];
    let levelDataNames = [];
    await streamTar(inputPath, async (header, stream) => {
        entries.push({name: header.name, type: header.type});
        if (/level\.dat$/.test(header.name)) {
            levelDataNames.push(header.name);
        }

        // Drain the entry as we only need the headers on this pass
        stream.resume();
        await once(stream, "end");
    });

    if (levelDataNames.length === 0) {
        const error = new Error("Provided file does not contain a Minecraft world.");
        error.reason = "NO_WORLD";
        throw error;
    }

    // Use the level.dat closest to the root of the archive
    levelDataNames.sort((a, b) => a.length - b.length);
    const selectedDat = levelDataNames[0];
    const pathPrefix = selectedDat.substring(0, selectedDat.lastIndexOf("/") + 1);

    // Count the files below the world root so we can report progress
    let totalFiles = entries.filter((entry) => entry.type === "file" && entry.name.startsWith(pathPrefix)).length;
    let filesExtracted = 0;

    // Second pass: extract everything below the level.dat
    await streamTar(inputPath, async (header, stream) => {
        if (header.name.startsWith(pathPrefix) && (header.type === "file" || header.type === "directory")) {
            // Join the paths to get the output path
            const entryOutputPath = path.join(outputPath, header.name.substring(pathPrefix.length));

            // Ignore paths which aren't safe
            if (path.normalize(entryOutputPath).startsWith(outputPath)) {
                if (header.type === "directory") {
                    await fs.mkdir(entryOutputPath, {recursive: true});
                } else {
                    // Ensure the directory is present then write the file contents
                    await fs.mkdir(path.dirname(entryOutputPath), {recursive: true});
                    await pipeline(stream, fsSync.createWriteStream(entryOutputPath));

                    // Update progress
                    filesExtracted++;
                    if (progressCallback) {
                        progressCallback(totalFiles === 0 ? 1 : filesExtracted / totalFiles);
                    }
                    return;
                }
            }
        }

        // Drain anything we didn't write so the stream advances
        stream.resume();
        await once(stream, "end");
    });
}