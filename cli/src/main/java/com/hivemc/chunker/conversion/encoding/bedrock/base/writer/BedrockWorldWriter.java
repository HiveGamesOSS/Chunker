package com.hivemc.chunker.conversion.encoding.bedrock.base.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A writer for Bedrock worlds.
 */
public class BedrockWorldWriter implements WorldWriter {
    protected final File outputFolder;
    protected final Converter converter;
    protected final BedrockResolvers resolvers;
    protected final DB database;
    protected final AtomicInteger entityID = new AtomicInteger(0);

    /**
     * Create a new Bedrock world writer.
     *
     * @param outputFolder the output folder where the world is written.
     * @param converter    the converter instance.
     * @param resolvers    the resolvers to use.
     * @param database     the LevelDB database.
     */
    public BedrockWorldWriter(File outputFolder, Converter converter, BedrockResolvers resolvers, DB database) {
        this.outputFolder = outputFolder;
        this.converter = converter;
        this.resolvers = resolvers;
        this.database = database;
    }

    @Override
    public ColumnWriter writeWorld(ChunkerWorld chunkerWorld) {
        // Return a new column writer
        return createColumnWriter(chunkerWorld.getDimension());
    }

    /**
     * Create a new column writer.
     *
     * @param dimension the dimension being written.
     * @return a newly created column writer.
     */
    public BedrockColumnWriter createColumnWriter(Dimension dimension) {
        return new BedrockColumnWriter(this, converter, resolvers, database, dimension);
    }

    /**
     * Generate a unique entity ID to use for an entity.
     *
     * @return a unique entity ID.
     */
    public long generateUniqueEntityID() {
        // Prefix the entity ID with the worldStartCount of 0 (it's flipped).
        return (0xFFFFFFFFL << 32) | entityID.getAndIncrement();
    }
}
