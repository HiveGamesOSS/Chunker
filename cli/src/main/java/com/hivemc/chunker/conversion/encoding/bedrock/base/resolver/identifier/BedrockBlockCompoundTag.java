package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier;

import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

/**
 * Wrapper to indicate if a compound tag should be waterlogged.
 *
 * @param compoundTag the block NBT.
 * @param waterlogged whether a water block should be on the liquid layer.
 */
public record BedrockBlockCompoundTag(CompoundTag compoundTag, boolean waterlogged) {
}
