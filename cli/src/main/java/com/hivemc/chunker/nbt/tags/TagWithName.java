package com.hivemc.chunker.nbt.tags;

/**
 * Represents a NBT tag with a name attached.
 *
 * @param name the tag name which was read.
 * @param tag  the tag which was read.
 * @param <T>  the type of the tag which was read.
 */
public record TagWithName<T extends Tag<?>>(String name, T tag) {

}
