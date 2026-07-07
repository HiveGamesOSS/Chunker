package com.hivemc.chunker.cli.messenger.messaging.response;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;

import java.util.UUID;

/**
 * Push event indicating that a preview tile failed to generate; the client should treat
 * the tile as gray-placeholder and not retry.
 */
public final class TileErrorResponse extends BasicMessage {
    private final String world;
    private final int lod;
    private final int tx;
    private final int tz;
    private final String reason;

    public TileErrorResponse(UUID requestId, String world, int lod, int tx, int tz, String reason) {
        super(requestId);
        this.world = world;
        this.lod = lod;
        this.tx = tx;
        this.tz = tz;
        this.reason = reason;
    }

    public String world() { return world; }
    public int lod() { return lod; }
    public int tx() { return tx; }
    public int tz() { return tz; }
    public String reason() { return reason; }
}
