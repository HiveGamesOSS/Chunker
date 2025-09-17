package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.StructureBlockMode;

/**
 * Represents a Structure Block Entity.
 */
public class StructureBlockEntity extends BlockEntity {
    /**
     * How the structure should be mirrored
     */
    public enum Mirror {
        NONE,
        LEFT_RIGHT,
        FRONT_BACK,
        BOTH // Bedrock only
    }

    /**
     * How the structure should be rotated
     */
    public enum Rotation {
        NONE,
        CLOCKWISE_90,
        CLOCKWISE_180,
        COUNTER_CLOCKWISE_90
    }

    private String name = "";
    private String author = "";
    private String metadata = "";
    private int posX;
    private int posY;
    private int posZ;
    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private Rotation rotation = Rotation.NONE;
    private Mirror mirror = Mirror.NONE;
    private StructureBlockMode mode = StructureBlockMode.DATA;
    private boolean ignoreEntities = false;
    private boolean includePlayers = false;
    private boolean removeBlocks = false;
    private boolean strict;
    private boolean powered;
    private boolean showAir;
    private boolean showBoundingBox = true;
    private float integrity = 1f;
    private long seed;
    private long lastTouchedPlayerId;
    private byte animationMode;
    private float animationSeconds;
    private int redstoneSaveMode;

    /**
     * Get the name of the structure.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the structure.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the author used for the structure.
     *
     * @return the author (can be empty).
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the author used for the structure.
     *
     * @param author the author (can be empty).
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Get the metadata associated with the structure.
     *
     * @return the metadata.
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * Set the metadata associated with the structure.
     *
     * @param metadata the metadata.
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * Get the position X for the structure.
     *
     * @return the x offset.
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Set the position X for the structure.
     *
     * @param posX the x offset.
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Get the position Y for the structure.
     *
     * @return the y offset.
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Set the position Y for the structure.
     *
     * @param posY the y offset.
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Get the position Z for the structure.
     *
     * @return the z offset.
     */
    public int getPosZ() {
        return posZ;
    }

    /**
     * Set the position Z for the structure.
     *
     * @param posZ the z offset.
     */
    public void setPosZ(int posZ) {
        this.posZ = posZ;
    }

    /**
     * Get the size of the structure in X.
     *
     * @return the x size in blocks.
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Set the size of the structure in X.
     *
     * @param sizeX the x size in blocks.
     */
    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    /**
     * Get the size of the structure in Y.
     *
     * @return the y size in blocks.
     */
    public int getSizeY() {
        return sizeY;
    }

    /**
     * Set the size of the structure in Y.
     *
     * @param sizeY the y size in blocks.
     */
    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    /**
     * Get the size of the structure in Z.
     *
     * @return the z size in blocks.
     */
    public int getSizeZ() {
        return sizeZ;
    }

    /**
     * Set the size of the structure in Z.
     *
     * @param sizeZ the z size in blocks.
     */
    public void setSizeZ(int sizeZ) {
        this.sizeZ = sizeZ;
    }

    /**
     * Get the rotation of the structure.
     *
     * @return the rotation.
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Set the rotation of the structure.
     *
     * @param rotation the rotation.
     */
    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    /**
     * Get the mirror mode of the structure.
     *
     * @return the mirror mode to reflect the structure.
     */
    public Mirror getMirror() {
        return mirror;
    }

    /**
     * Set the mirror mode of the structure.
     *
     * @param mirror the mirror mode to reflect the structure.
     */
    public void setMirror(Mirror mirror) {
        this.mirror = mirror;
    }

    /**
     * Get the mode of the structure block.
     *
     * @return the mode.
     */
    public StructureBlockMode getMode() {
        return mode;
    }

    /**
     * Set the mode of the structure block.
     *
     * @param mode the mode.
     */
    public void setMode(StructureBlockMode mode) {
        this.mode = mode;
    }

    /**
     * Get whether the structure block ignores entities.
     *
     * @return true if it ignores entities.
     */
    public boolean isIgnoreEntities() {
        return ignoreEntities;
    }

    /**
     * Set whether the structure block ignores entities.
     *
     * @param ignoreEntities true if it ignores entities.
     */
    public void setIgnoreEntities(boolean ignoreEntities) {
        this.ignoreEntities = ignoreEntities;
    }

    /**
     * Get whether the structure should include players (Bedrock only).
     *
     * @return true if players should be included.
     */
    public boolean isIncludePlayers() {
        return includePlayers;
    }

    /**
     * Set whether the structure should include players (Bedrock only).
     *
     * @param includePlayers true if players should be included.
     */
    public void setIncludePlayers(boolean includePlayers) {
        this.includePlayers = includePlayers;
    }

    /**
     * Set whether blocks should be removed (Bedrock only).
     *
     * @return true if they should be removed.
     */
    public boolean isRemoveBlocks() {
        return removeBlocks;
    }

    /**
     * Get whether blocks should be removed (Bedrock only).
     *
     * @param removeBlocks true if they should be removed.
     */
    public void setRemoveBlocks(boolean removeBlocks) {
        this.removeBlocks = removeBlocks;
    }

    /**
     * Get whether the structure block is strict with placement of states (Java only).
     *
     * @return true if it is strict.
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Set whether the structure block is strict with placement of states (Java only).
     *
     * @param strict true if it is strict.
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * Get whether the structure block is powered.
     *
     * @return true if powered.
     */
    public boolean isPowered() {
        return powered;
    }

    /**
     * Set whether the structure block is powered.
     *
     * @param powered true if powered.
     */
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    /**
     * Get whether air should be shown (Java only).
     *
     * @return true if air should be shown.
     */
    public boolean isShowAir() {
        return showAir;
    }

    /**
     * Set whether air should be shown (Java only).
     *
     * @param showAir true if air should be shown.
     */
    public void setShowAir(boolean showAir) {
        this.showAir = showAir;
    }

    /**
     * Get whether the bounding box is shown.
     *
     * @return true if it's visible.
     */
    public boolean isShowBoundingBox() {
        return showBoundingBox;
    }

    /**
     * Set whether the bounding box is shown.
     *
     * @param showBoundingBox true if it's visible.
     */
    public void setShowBoundingBox(boolean showBoundingBox) {
        this.showBoundingBox = showBoundingBox;
    }

    /**
     * Get the percentage of blocks which should be kept randomly (between 0-1).
     *
     * @return the value between 0 and 1.
     */
    public float getIntegrity() {
        return integrity;
    }

    /**
     * Set the percentage of blocks which should be kept randomly (between 0-1).
     *
     * @param integrity the value between 0 and 1.
     */
    public void setIntegrity(float integrity) {
        this.integrity = integrity;
    }

    /**
     * Get the seed used for removing random blocks.
     *
     * @return the seed.
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Set the seed used for removing random blocks.
     *
     * @param seed the seed.
     */
    public void setSeed(long seed) {
        this.seed = seed;
    }

    /**
     * Get the ID of the last player that touched the block (Bedrock only).
     *
     * @return the player ID.
     */
    public long getLastTouchedPlayerId() {
        return lastTouchedPlayerId;
    }

    /**
     * Set the ID of the last player that touched the block (Bedrock only).
     *
     * @param lastTouchedPlayerId the player ID.
     */
    public void setLastTouchedPlayerId(long lastTouchedPlayerId) {
        this.lastTouchedPlayerId = lastTouchedPlayerId;
    }

    /**
     * Get the animation mode for the placement (Bedrock only).
     *
     * @return the animation mode ID.
     */
    public byte getAnimationMode() {
        return animationMode;
    }

    /**
     * Set the animation mode for the placement (Bedrock only).
     *
     * @param animationMode the animation mode ID.
     */
    public void setAnimationMode(byte animationMode) {
        this.animationMode = animationMode;
    }

    /**
     * Get the length of the animation for placement in seconds (Bedrock only).
     *
     * @return the time in seconds.
     */
    public float getAnimationSeconds() {
        return animationSeconds;
    }

    /**
     * Set the length of the animation for placement in seconds (Bedrock only).
     *
     * @param animationSeconds the time in seconds.
     */
    public void setAnimationSeconds(float animationSeconds) {
        this.animationSeconds = animationSeconds;
    }

    /**
     * Get the redstone save mode ID (Bedrock only).
     *
     * @return the ID.
     */
    public int getRedstoneSaveMode() {
        return redstoneSaveMode;
    }

    /**
     * Set the redstone save mode ID (Bedrock only).
     *
     * @param redstoneSaveMode the ID.
     */
    public void setRedstoneSaveMode(int redstoneSaveMode) {
        this.redstoneSaveMode = redstoneSaveMode;
    }
}
