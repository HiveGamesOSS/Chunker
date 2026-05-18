const BEDROCK_ID_TO_IDENTIFIER = {
    0: "minecraft:overworld",
    1: "minecraft:the_nether",
    2: "minecraft:the_end"
};

export function parseMapBin(buffer) {
    const view = new DataView(buffer);
    let offset = 0;
    const worldCount = view.getInt32(offset, true); offset += 4;
    const worlds = new Map();
    for (let w = 0; w < worldCount; w++) {
        const bedrockId = view.getInt32(offset, true); offset += 4;
        const minX = view.getInt32(offset, true); offset += 4;
        const minZ = view.getInt32(offset, true); offset += 4;
        const maxX = view.getInt32(offset, true); offset += 4;
        const maxZ = view.getInt32(offset, true); offset += 4;
        const regionCount = view.getInt32(offset, true); offset += 4;
        const regionPresence = new Map();
        for (let r = 0; r < regionCount; r++) {
            const rx = view.getInt32(offset, true); offset += 4;
            const rz = view.getInt32(offset, true); offset += 4;
            // Slice once into a view backed by the same buffer — avoid the heavy buffer.slice + new ArrayBuffer
            // allocation per region. The view stays valid because we hold the buffer reference via DataView.
            const bits = new Uint8Array(buffer, offset, 128);
            offset += 128;
            regionPresence.set(`${rx},${rz}`, bits);
        }
        const identifier = BEDROCK_ID_TO_IDENTIFIER[bedrockId] || `custom:${bedrockId}`;
        worlds.set(identifier, {bedrockId, minX, minZ, maxX, maxZ, regionPresence});
    }
    return {worlds};
}

function regionHasAnyChunk(entry, rx, rz) {
    const bits = entry.regionPresence.get(`${rx},${rz}`);
    if (!bits) return false;
    for (let i = 0; i < bits.length; i++) if (bits[i] !== 0) return true;
    return false;
}

export function isTileEmpty(parsed, world, lod, tx, tz) {
    const entry = parsed.worlds.get(world);
    if (!entry) return true;
    const scale = 1 << (-lod);
    const minRx = tx * scale;
    const minRz = tz * scale;
    const maxRx = minRx + scale;
    const maxRz = minRz + scale;
    for (let rx = minRx; rx < maxRx; rx++) {
        for (let rz = minRz; rz < maxRz; rz++) {
            if (regionHasAnyChunk(entry, rx, rz)) return false;
        }
    }
    return true;
}

export function worldBoundsForAutoFit(parsed, world) {
    const entry = parsed.worlds.get(world);
    if (!entry) return null;
    return {minX: entry.minX, minZ: entry.minZ, maxX: entry.maxX, maxZ: entry.maxZ};
}
