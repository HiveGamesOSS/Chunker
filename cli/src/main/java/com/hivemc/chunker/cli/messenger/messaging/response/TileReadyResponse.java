package com.hivemc.chunker.cli.messenger.messaging.response;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;

import java.util.UUID;

/**
 * Push event indicating that a preview tile PNG is on disk and ready to be loaded by the client.
 */
public final class TileReadyResponse extends BasicMessage {
    private final String world;
    private final int lod;
    private final int tx;
    private final int tz;
    private final String path;

    public TileReadyResponse(UUID requestId, String world, int lod, int tx, int tz, String path) {
        super(requestId);
        this.world = world;
        this.lod = lod;
        this.tx = tx;
        this.tz = tz;
        this.path = path;
    }

    public String world() { return world; }
    public int lod() { return lod; }
    public int tx() { return tx; }
    public int tz() { return tz; }
    public String path() { return path; }
}
