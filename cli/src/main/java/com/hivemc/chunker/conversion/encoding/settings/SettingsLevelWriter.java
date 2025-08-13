package com.hivemc.chunker.conversion.encoding.settings;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.*;

/**
 * LevelWriter which writes the maps as images, the settings and present columns as JSON.
 */
public class SettingsLevelWriter implements LevelWriter {
    private final File outputFolder;
    private final List<SettingsWorldWriter.WorldData> worlds = Collections.synchronizedList(new ArrayList<>(4));
    private List<ChunkerMap> maps;
    private JsonObject settings;

    /**
     * Create a new settings level writer.
     *
     * @param outputFolder the output folder to write the data to.
     */
    public SettingsLevelWriter(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    @Override
    public WorldWriter writeLevel(ChunkerLevel chunkerLevel) throws Exception {
        outputFolder.mkdirs(); // Ensure folder is made

        // Serialize maps
        Task.asyncConsumeForEach("Saving Map", TaskWeight.LOW, this::writeMap, chunkerLevel.getMaps());

        // Record data
        settings = chunkerLevel.getSettings().toDescriptiveJSON();
        maps = chunkerLevel.getMaps();

        // Create a new world writer with ourselves
        return new SettingsWorldWriter(this);
    }

    @Override
    public void flushLevel() throws Exception {
        // Turn the regions into a dimensions array
        JsonObject dimensions = new JsonObject();

        // Sort the dimensions by index so they're in order
        worlds.sort(Comparator.comparing(a -> a.dimension.ordinal()));
        for (SettingsWorldWriter.WorldData worldData : worlds) {
            JsonArray regions = new JsonArray(worldData.regions.size());
            for (RegionCoordPair regionCoordPair : worldData.regions) {
                JsonArray coords = new JsonArray(2);
                coords.add(regionCoordPair.regionX());
                coords.add(regionCoordPair.regionZ());
                regions.add(coords);
            }
            dimensions.add(worldData.dimension.name(), regions);
        }

        // Create the data object which holds all the data
        Map<String, Object> data = new Object2ObjectOpenHashMap<>();
        data.put("maps", maps);
        data.put("dimensions", dimensions);
        data.put("settings", settings);

        // Write it to disk
        Files.writeString(new File(outputFolder, "data.json").toPath(), new Gson().toJson(data));
    }

    @Override
    public EncodingType getEncodingType() {
        return EncodingType.SETTINGS;
    }

    @Override
    public Version getVersion() {
        return new Version(1, 0, 0);
    }

    /**
     * Add worldData to the world list.
     *
     * @param worldData the world data.
     */
    public void add(SettingsWorldWriter.WorldData worldData) {
        worlds.add(worldData);
    }

    /**
     * Write a map file as a PNG.
     *
     * @param map the map file to write.
     * @throws Exception if an error happened during writing.
     */
    protected void writeMap(ChunkerMap map) throws Exception {
        BufferedImage image = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_ARGB);
        byte[] bytes = map.getBytes();
        if (bytes == null) return; // Don't write empty maps

        // Loop through each pixel
        int index = 0;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                // Convert from RGBA to ARGB
                image.setRGB(x, y,
                        ((bytes[index + 3] & 0xFF) << 24) |
                                ((bytes[index] & 0xFF) << 16) |
                                ((bytes[index + 1] & 0xFF) << 8) |
                                ((bytes[index + 2] & 0xFF))
                );
                index += 4;
            }
        }
        // Write file
        ImageIO.write(image, "png", new File(outputFolder, map.getId() + ".png"));
    }
}
