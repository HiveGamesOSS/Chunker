package com.hivemc.chunker.conversion.intermediate.level.map;

import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;

/**
 * In-game item map with bytes of the rendered image.
 */
public class ChunkerMap {
    /**
     * Comparator for sorting maps by their ID in ascending order.
     */
    public static final Comparator<ChunkerMap> BY_ID_COMPARATOR = Comparator.comparing(ChunkerMap::getId);

    private final Long originalId;
    private long id;
    private int width;
    private int height;
    private byte scale;
    private Dimension dimension;
    private int xCenter;
    private int zCenter;
    private boolean unlimitedTracking;
    private boolean locked;
    private transient byte @Nullable [] bytes;

    @Nullable
    private transient CompoundTag originalNBT;

    /**
     * Create a new map.
     *
     * @param originalId        the original ID of the map before conversion.
     * @param id                the ID of the map used in-game.
     * @param width             the width of the map.
     * @param height            the height of the map.
     * @param scale             the scale to use for the map.
     * @param dimension         the dimension which the bytes are from.
     * @param xCenter           the X co-ordinate center for the map.
     * @param zCenter           the Z co-ordinate center for the map.
     * @param unlimitedTracking whether player tracking on the edges of the map is enabled.
     * @param locked            whether the map is locked and can't be changed.
     * @param bytes             the image bytes of the map in RGBA.
     * @param originalNBT       the original map data used for copying (null if not present).
     */
    public ChunkerMap(long originalId, long id, int width, int height, byte scale, Dimension dimension, int xCenter, int zCenter, boolean unlimitedTracking, boolean locked, byte @Nullable [] bytes, @Nullable CompoundTag originalNBT) {
        this.originalId = originalId;
        this.id = id;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.dimension = dimension;
        this.xCenter = xCenter;
        this.zCenter = zCenter;
        this.unlimitedTracking = unlimitedTracking;
        this.locked = locked;
        this.bytes = bytes;
        this.originalNBT = originalNBT;
    }

    /**
     * Create a new map with no original ID (it was never read from a world).
     *
     * @param id                the ID of the map used in-game.
     * @param width             the width of the map.
     * @param height            the height of the map.
     * @param scale             the scale to use for the map.
     * @param dimension         the dimension which the bytes are from.
     * @param xCenter           the X co-ordinate center for the map.
     * @param zCenter           the Z co-ordinate center for the map.
     * @param unlimitedTracking whether player tracking on the edges of the map is enabled.
     * @param locked            whether the map is locked and can't be changed.
     * @param bytes             the image bytes of the map in RGBA.
     */
    public ChunkerMap(long id, int width, int height, byte scale, Dimension dimension, int xCenter, int zCenter, boolean unlimitedTracking, boolean locked, byte @Nullable [] bytes) {
        originalId = null;
        this.id = id;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.dimension = dimension;
        this.xCenter = xCenter;
        this.zCenter = zCenter;
        this.unlimitedTracking = unlimitedTracking;
        this.locked = locked;
        this.bytes = bytes;
    }

    /**
     * Get the ID used for the map.
     *
     * @return the ID of the map.
     */
    public long getId() {
        return id;
    }

    /**
     * Set the ID used for the map.
     *
     * @param id the ID of the map.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the original ID used for the map.
     *
     * @return the ID of the map for reading, can be null if the map was never read.
     */
    @Nullable
    public Long getOriginalId() {
        return originalId;
    }

    /**
     * Get the pixel width of the map.
     *
     * @return the width of the map.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the width of the map (the pixels will need to correspond to this value).
     *
     * @param width the width of the map.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get the pixel height of the map.
     *
     * @return the height of the map.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the height of the map (the pixels will need to correspond to this value).
     *
     * @param height the width of the map.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get the scale of the map.
     *
     * @return the scale of the map.
     */
    public byte getScale() {
        return scale;
    }

    /**
     * Set the scale of the map.
     *
     * @param scale the scale of the map.
     */
    public void setScale(byte scale) {
        this.scale = scale;
    }

    /**
     * Get the dimension the map is inside.
     *
     * @return the dimension.
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Set the dimension the map is inside.
     *
     * @param dimension the dimension for the map.
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    /**
     * Get the X center of the map.
     *
     * @return the x co-ordinate.
     */
    public int getXCenter() {
        return xCenter;
    }

    /**
     * Set the X co-ordinate center of the map.
     *
     * @param xCenter the X co-ordinate for the center of the map.
     */
    public void setXCenter(int xCenter) {
        this.xCenter = xCenter;
    }

    /**
     * Get the Z center of the map.
     *
     * @return the z co-ordinate.
     */
    public int getZCenter() {
        return zCenter;
    }

    /**
     * Set the Z co-ordinate center of the map.
     *
     * @param zCenter the Z co-ordinate for the center of the map.
     */
    public void setZCenter(int zCenter) {
        this.zCenter = zCenter;
    }

    /**
     * Get whether players can be tracked outside the map.
     *
     * @return true if they can be tracked.
     */
    public boolean isUnlimitedTracking() {
        return unlimitedTracking;
    }

    /**
     * Set whether players can be tracked outside the map.
     *
     * @param unlimitedTracking true if they can be tracked.
     */
    public void setUnlimitedTracking(boolean unlimitedTracking) {
        this.unlimitedTracking = unlimitedTracking;
    }

    /**
     * Whether the map is locked from re-rendering in-game.
     *
     * @return true if it is locked.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Set whether the map is locked for modification.
     *
     * @param locked true if it is locked and shouldn't re-render.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Get the RGBA bytes for the map, with 4 bytes for each pixel.
     *
     * @return the array of bytes.
     */
    public byte @Nullable [] getBytes() {
        return bytes;
    }

    /**
     * Set the RGBA bytes for the map.
     *
     * @param bytes the RGBA maps for the map, which should be getWidth() * getHeight() * 4 in size.
     */
    public void setBytes(byte @Nullable [] bytes) {
        this.bytes = bytes;
    }

    /**
     * Load an image from a file for this map, automatically resized to the width/height declared by this map.
     *
     * @param file the file to load.
     * @throws IOException if it fails to read the map.
     */
    public void loadImage(File file) throws IOException {
        if (!file.exists()) throw new IllegalArgumentException("Could not find map file " + file.getName());

        // Read the image
        BufferedImage inputImage = ImageIO.read(file);

        // Check if it needs resizing
        if (inputImage.getWidth() != width || inputImage.getHeight() != height) {
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Draw the image to resize
            Graphics2D g2d = outputImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(inputImage, 0, 0, width, height, null);
            g2d.dispose();

            // Replace the input with the resized one
            inputImage = outputImage;
        }

        // Convert the image to RGBA
        byte[] rgba = new byte[width * height * 4];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = inputImage.getRGB(x, y);
                rgba[index] = (byte) ((color >> 16) & 0xFF);
                rgba[index + 1] = (byte) ((color >> 8) & 0xFF);
                rgba[index + 2] = (byte) (color & 0xFF);
                rgba[index + 3] = ((color >> 24) & 0xFF) > 128 ? (byte) 255 : (byte) 0;
                index += 4;
            }
        }

        bytes = rgba;
    }

    /**
     * Get the original map data (used for NBT copying).
     *
     * @return the original data, null if not present.
     */
    @Nullable
    public CompoundTag getOriginalNBT() {
        return originalNBT;
    }

    /**
     * Set the original map data NBT, used for if NBT copying is enabled.
     *
     * @param originalNBT the original data.
     */
    public void setOriginalNBT(@Nullable CompoundTag originalNBT) {
        this.originalNBT = originalNBT;
    }
}
