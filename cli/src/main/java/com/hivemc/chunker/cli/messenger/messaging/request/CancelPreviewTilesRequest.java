package com.hivemc.chunker.cli.messenger.messaging.request;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Request to cancel preview tile generation for a world (optionally filtered by LOD).
 */
public class CancelPreviewTilesRequest extends BasicMessage {
    private final UUID anonymousId;
    private final String world;
    @Nullable
    private final Integer lod;

    /**
     * Create a new request to cancel preview tiles.
     *
     * @param anonymousId the session ID for the user.
     * @param world       the name of the world.
     * @param lod         the level of detail, or null to cancel all LODs.
     */
    public CancelPreviewTilesRequest(UUID anonymousId, String world, @Nullable Integer lod) {
        this.anonymousId = anonymousId;
        this.world = world;
        this.lod = lod;
    }

    /**
     * Create a new request to cancel preview tiles.
     *
     * @param requestId   the request ID to use for this request.
     * @param anonymousId the session ID for the user.
     * @param world       the name of the world.
     * @param lod         the level of detail, or null to cancel all LODs.
     */
    public CancelPreviewTilesRequest(UUID requestId, UUID anonymousId, String world, @Nullable Integer lod) {
        super(requestId);
        this.anonymousId = anonymousId;
        this.world = world;
        this.lod = lod;
    }

    /**
     * The session ID used by the user.
     *
     * @return a random UUID used by the user.
     */
    public UUID getAnonymousId() {
        return anonymousId;
    }

    /**
     * The name of the world.
     *
     * @return the world name.
     */
    public String getWorld() {
        return world;
    }

    /**
     * The level of detail.
     *
     * @return the LOD, or null to cancel all LODs.
     */
    @Nullable
    public Integer getLod() {
        return lod;
    }
}
