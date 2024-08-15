package com.hivemc.chunker.conversion.bedrock.resolver.blockentity;

import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers.BedrockFlowerPotBlockEntityHandler;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic test to ensure potted plants have been updated for Bedrock / Legacy Java.
 */
public class BedrockFlowerPotMappedTests {
    public static ChunkerBlockType[] flowerPots() {
        return Arrays.stream(ChunkerVanillaBlockType.values())
                .filter(a -> a.name().startsWith("POTTED_") || a.name().equals("FLOWER_POT"))
                .toArray(ChunkerBlockType[]::new);
    }

    @ParameterizedTest
    @MethodSource("flowerPots")
    public void testFlowerPot(ChunkerBlockType flowerPot) {
        assertTrue(BedrockFlowerPotBlockEntityHandler.POTTED_TO_PLANT.containsKey(flowerPot));
    }
}
