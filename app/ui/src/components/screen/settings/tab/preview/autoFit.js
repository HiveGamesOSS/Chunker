export const MIN_ZOOM_FLOOR = -12;
const TILE_SIZE_PX = 512;

export function computeMinZoom(bounds) {
    if (!bounds) return MIN_ZOOM_FLOOR;
    const chunksX = bounds.maxX - bounds.minX + 1;
    const chunksZ = bounds.maxZ - bounds.minZ + 1;
    const worldPx = Math.max(chunksX, chunksZ) * 16;
    if (worldPx <= 0) return MIN_ZOOM_FLOOR;
    const neededLod = Math.max(0, Math.ceil(Math.log2(worldPx / TILE_SIZE_PX)));
    return -Math.min(neededLod, -MIN_ZOOM_FLOOR);
}
