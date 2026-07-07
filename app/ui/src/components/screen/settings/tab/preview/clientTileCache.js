const MIN_BYTES = 64 * 1024 * 1024;
const MAX_BYTES = 384 * 1024 * 1024;

export function computeBudgetBytes() {
    const deviceMemoryGb = (typeof navigator !== "undefined" && navigator.deviceMemory) || 4;
    const tenPercent = deviceMemoryGb * 1024 * 1024 * 1024 * 0.10;
    return Math.max(MIN_BYTES, Math.min(MAX_BYTES, tenPercent));
}

export class ClientTileCache {
    constructor(budgetBytes = computeBudgetBytes()) {
        this.budgetBytes = budgetBytes;
        this.bytesInUse = 0;
        this.map = new Map(); // insertion order doubles as LRU; re-insert on access
    }

    keyFor(world, lod, tx, tz) {
        return `${world}|${lod}|${tx}|${tz}`;
    }

    get(key) {
        const entry = this.map.get(key);
        if (!entry) return null;
        // Touch for LRU.
        this.map.delete(key);
        this.map.set(key, entry);
        return entry;
    }

    put(key, entry) {
        const existing = this.map.get(key);
        if (existing) {
            this.bytesInUse -= existing.sizeBytes;
            this.map.delete(key);
        }
        this.map.set(key, entry);
        this.bytesInUse += entry.sizeBytes;
        while (this.bytesInUse > this.budgetBytes && this.map.size > 1) {
            const firstKey = this.map.keys().next().value;
            this.evict(firstKey);
        }
    }

    evict(key) {
        const entry = this.map.get(key);
        if (!entry) return;
        this.map.delete(key);
        this.bytesInUse -= entry.sizeBytes;
        if (entry.blobUrl) {
            // Only revoke if it's actually a blob: URL we created.
            if (typeof entry.blobUrl === "string" && entry.blobUrl.startsWith("blob:")) {
                URL.revokeObjectURL(entry.blobUrl);
            }
        }
        if (entry.img) {
            entry.img.src = "";
            if (entry.img.parentNode) entry.img.parentNode.removeChild(entry.img);
        }
    }

    evictAllExcept(keepKeys) {
        const keep = new Set(keepKeys);
        for (const key of Array.from(this.map.keys())) {
            if (!keep.has(key)) this.evict(key);
        }
    }
}
