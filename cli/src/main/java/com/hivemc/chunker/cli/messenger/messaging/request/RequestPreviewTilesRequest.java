package com.hivemc.chunker.cli.messenger.messaging.request;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;

import java.util.UUID;

/**
 * Request indicating that the client wants a range of preview tiles to be generated
 * for the given world at the given LOD.
 */
public class RequestPreviewTilesRequest extends BasicMessage {
    private final UUID anonymousId;
    private final String world;
    private final int lod;
    private final int minTx;
    private final int minTz;
    private final int maxTx;
    private final int maxTz;

    /**
     * Create a new request for preview tiles.
     *
     * @param anonymousId the session ID for the user.
     * @param world       the name of the world.
     * @param lod         the level of detail.
     * @param minTx       the minimum tile X coordinate.
     * @param minTz       the minimum tile Z coordinate.
     * @param maxTx       the maximum tile X coordinate.
     * @param maxTz       the maximum tile Z coordinate.
     */
    public RequestPreviewTilesRequest(UUID anonymousId, String world, int lod, int minTx, int minTz, int maxTx, int maxTz) {
        this.anonymousId = anonymousId;
        this.world = world;
        this.lod = lod;
        this.minTx = minTx;
        this.minTz = minTz;
        this.maxTx = maxTx;
        this.maxTz = maxTz;
    }

    /**
     * Create a new request for preview tiles.
     *
     * @param requestId   the request ID to use for this request.
     * @param anonymousId the session ID for the user.
     * @param world       the name of the world.
     * @param lod         the level of detail.
     * @param minTx       the minimum tile X coordinate.
     * @param minTz       the minimum tile Z coordinate.
     * @param maxTx       the maximum tile X coordinate.
     * @param maxTz       the maximum tile Z coordinate.
     */
    public RequestPreviewTilesRequest(UUID requestId, UUID anonymousId, String world, int lod, int minTx, int minTz, int maxTx, int maxTz) {
        super(requestId);
        this.anonymousId = anonymousId;
        this.world = world;
        this.lod = lod;
        this.minTx = minTx;
        this.minTz = minTz;
        this.maxTx = maxTx;
        this.maxTz = maxTz;
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
     * @return the LOD.
     */
    public int getLod() {
        return lod;
    }

    /**
     * The minimum tile X coordinate.
     *
     * @return the minimum tile X.
     */
    public int getMinTx() {
        return minTx;
    }

    /**
     * The minimum tile Z coordinate.
     *
     * @return the minimum tile Z.
     */
    public int getMinTz() {
        return minTz;
    }

    /**
     * The maximum tile X coordinate.
     *
     * @return the maximum tile X.
     */
    public int getMaxTx() {
        return maxTx;
    }

    /**
     * The maximum tile Z coordinate.
     *
     * @return the maximum tile Z.
     */
    public int getMaxTz() {
        return maxTz;
    }
}
