import {describe, it, beforeEach, afterEach} from "node:test";
import assert from "node:assert/strict";
import os from "os";
import path from "path";
import fs from "fs/promises";
import zlib from "zlib";
import tarStream from "tar-stream";
import jszip from "jszip";
import {detectArchiveType, extractTar, extractZip} from "../src/util.js";

// A small fake world used as the contents of every archive we build below.
const WORLD_ENTRIES = [
    {name: "MyWorld/level.dat", data: Buffer.from("LEVELDATA")},
    {name: "MyWorld/region/r.0.0.mca", data: Buffer.from("REGIONDATA")},
    {name: "MyWorld/data/map_0.dat", data: Buffer.from("MAPDATA")}
];

// Build an uncompressed tar buffer from the given entries.
function makeTar(entries) {
    return new Promise((resolve, reject) => {
        const pack = tarStream.pack();
        const chunks = [];
        pack.on("data", (chunk) => chunks.push(chunk));
        pack.on("end", () => resolve(Buffer.concat(chunks)));
        pack.on("error", reject);

        // Add the entries one at a time, finalizing once they're all written
        let index = 0;
        const addNext = () => {
            if (index >= entries.length) {
                pack.finalize();
                return;
            }
            const entry = entries[index++];
            if (entry.dir) {
                pack.entry({name: entry.name, type: "directory"}, addNext);
            } else {
                pack.entry({name: entry.name}, entry.data, addNext);
            }
        };
        addNext();
    });
}

// Build a zip buffer from the given entries.
async function makeZip(entries) {
    const zip = new jszip();
    for (const entry of entries) {
        if (!entry.dir) {
            zip.file(entry.name, entry.data);
        }
    }
    return await zip.generateAsync({type: "nodebuffer"});
}

async function writeFixture(dir, name, buffer) {
    const filePath = path.join(dir, name);
    await fs.writeFile(filePath, buffer);
    return filePath;
}

async function exists(filePath) {
    try {
        await fs.access(filePath);
        return true;
    } catch {
        return false;
    }
}

// Assert that the world was extracted below the level.dat (the MyWorld/ prefix removed).
async function assertExtractedWorld(outputPath) {
    assert.equal(await fs.readFile(path.join(outputPath, "level.dat"), "utf8"), "LEVELDATA");
    assert.equal(await fs.readFile(path.join(outputPath, "region", "r.0.0.mca"), "utf8"), "REGIONDATA");
    assert.equal(await fs.readFile(path.join(outputPath, "data", "map_0.dat"), "utf8"), "MAPDATA");
}

describe("detectArchiveType", () => {
    let dir;
    beforeEach(async () => {
        dir = await fs.mkdtemp(path.join(os.tmpdir(), "chunker-detect-"));
    });
    afterEach(async () => {
        await fs.rm(dir, {recursive: true, force: true});
    });

    it("detects zip archives", async () => {
        const filePath = await writeFixture(dir, "world.zip", await makeZip(WORLD_ENTRIES));
        assert.equal(await detectArchiveType(filePath), "zip");
    });

    it("detects uncompressed tar archives", async () => {
        const filePath = await writeFixture(dir, "world.tar", await makeTar(WORLD_ENTRIES));
        assert.equal(await detectArchiveType(filePath), "tar");
    });

    it("detects gzip-compressed tar archives", async () => {
        const filePath = await writeFixture(dir, "world.tar.gz", zlib.gzipSync(await makeTar(WORLD_ENTRIES)));
        assert.equal(await detectArchiveType(filePath), "tar");
    });

    it("detects brotli-compressed tar archives by extension", async () => {
        const filePath = await writeFixture(dir, "world.tar.br", zlib.brotliCompressSync(await makeTar(WORLD_ENTRIES)));
        assert.equal(await detectArchiveType(filePath), "tar");
    });

    it("returns unknown for files that aren't archives", async () => {
        const filePath = await writeFixture(dir, "notes.bin", Buffer.from("just some random text"));
        assert.equal(await detectArchiveType(filePath), "unknown");
    });
});

describe("archive extraction", () => {
    let dir;
    let outputPath;
    beforeEach(async () => {
        dir = await fs.mkdtemp(path.join(os.tmpdir(), "chunker-extract-"));
        outputPath = path.join(dir, "output");
        await fs.mkdir(outputPath);
    });
    afterEach(async () => {
        await fs.rm(dir, {recursive: true, force: true});
    });

    it("ingests a zip world below the level.dat", async () => {
        const filePath = await writeFixture(dir, "world.zip", await makeZip(WORLD_ENTRIES));
        const progress = [];
        await extractZip(filePath, outputPath, (value) => progress.push(value));

        await assertExtractedWorld(outputPath);
        assert.ok(progress.length > 0, "expected progress to be reported");
        assert.equal(Math.max(...progress), 1);
    });

    it("ingests an uncompressed tar world", async () => {
        const filePath = await writeFixture(dir, "world.tar", await makeTar(WORLD_ENTRIES));
        const progress = [];
        await extractTar(filePath, outputPath, (value) => progress.push(value));

        await assertExtractedWorld(outputPath);
        assert.equal(Math.max(...progress), 1);
    });

    it("ingests a gzip-compressed tar world", async () => {
        const filePath = await writeFixture(dir, "world.tar.gz", zlib.gzipSync(await makeTar(WORLD_ENTRIES)));
        await extractTar(filePath, outputPath, () => {});

        await assertExtractedWorld(outputPath);
    });

    it("ingests a brotli-compressed tar world", async () => {
        const filePath = await writeFixture(dir, "world.tar.br", zlib.brotliCompressSync(await makeTar(WORLD_ENTRIES)));
        await extractTar(filePath, outputPath, () => {});

        await assertExtractedWorld(outputPath);
    });

    it("rejects a zip without a level.dat", async () => {
        const filePath = await writeFixture(dir, "noworld.zip", await makeZip([
            {name: "stuff/readme.txt", data: Buffer.from("hi")}
        ]));
        await assert.rejects(() => extractZip(filePath, outputPath, () => {}), (e) => e.reason === "NO_WORLD");
    });

    it("rejects a tar without a level.dat", async () => {
        const filePath = await writeFixture(dir, "noworld.tar", await makeTar([
            {name: "stuff/readme.txt", data: Buffer.from("hi")}
        ]));
        await assert.rejects(() => extractTar(filePath, outputPath, () => {}), (e) => e.reason === "NO_WORLD");
    });

    it("ignores zip entries that escape the output directory", async () => {
        const filePath = await writeFixture(dir, "evil.zip", await makeZip([
            {name: "MyWorld/level.dat", data: Buffer.from("LEVELDATA")},
            {name: "MyWorld/../../escape.txt", data: Buffer.from("PWNED")}
        ]));
        await extractZip(filePath, outputPath, () => {});

        assert.equal(await fs.readFile(path.join(outputPath, "level.dat"), "utf8"), "LEVELDATA");
        assert.equal(await exists(path.join(dir, "escape.txt")), false);
    });

    it("ignores tar entries that escape the output directory", async () => {
        const filePath = await writeFixture(dir, "evil.tar", await makeTar([
            {name: "MyWorld/level.dat", data: Buffer.from("LEVELDATA")},
            {name: "MyWorld/../../escape.txt", data: Buffer.from("PWNED")}
        ]));
        await extractTar(filePath, outputPath, () => {});

        assert.equal(await fs.readFile(path.join(outputPath, "level.dat"), "utf8"), "LEVELDATA");
        assert.equal(await exists(path.join(dir, "escape.txt")), false);
    });
});
