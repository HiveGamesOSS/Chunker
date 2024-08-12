import fs from "fs/promises";
import path from "path";
import fsSync from "fs";
import archiver from "archiver";

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