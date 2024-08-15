package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a Skull Block Entity.
 */
public class SkullBlockEntity extends BlockEntity {
    @Nullable
    private String ownerId;
    @Nullable
    private String ownerName;
    @Nullable
    private String texture;
    @Nullable
    private String textureSignature;

    /**
     * The UUID of the head owner (Java only).
     *
     * @return the owner UUID or null if not present.
     */
    @Nullable
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Set the UUID of the head owner (Java only).
     *
     * @param ownerId the owner UUID or null if not present.
     */
    public void setOwnerId(@Nullable String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * The username of the head owner (Java only).
     *
     * @return the owner username or null if not present.
     */
    @Nullable
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Set the username of the head owner (Java only).
     *
     * @param ownerName the owner username or null if not present.
     */
    public void setOwnerName(@Nullable String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * Get the encoded texture for the head (Java only).
     *
     * @return the base64 encoded texture or null if not present.
     */
    @Nullable
    public String getTexture() {
        return texture;
    }

    /**
     * Set the encoded texture for the head (Java only).
     *
     * @param texture the base64 encoded texture or null if not present.
     */
    public void setTexture(@Nullable String texture) {
        this.texture = texture;
    }

    /**
     * Get the signature for the head texture (Java only).
     *
     * @return the signature for the head texture or null if not present.
     */
    @Nullable
    public String getTextureSignature() {
        return textureSignature;
    }

    /**
     * Set the signature for the head texture (Java only).
     *
     * @param textureSignature the signature for the head texture.
     */
    public void setTextureSignature(@Nullable String textureSignature) {
        this.textureSignature = textureSignature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkullBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getOwnerId(), that.getOwnerId()) && Objects.equals(getOwnerName(), that.getOwnerName()) && Objects.equals(getTexture(), that.getTexture()) && Objects.equals(getTextureSignature(), that.getTextureSignature());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getOwnerId(), getOwnerName(), getTexture(), getTextureSignature());
    }
}
