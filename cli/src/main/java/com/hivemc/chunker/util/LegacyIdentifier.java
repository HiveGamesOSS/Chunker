package com.hivemc.chunker.util;

/**
 * LegacyIdentifier is a class holding an ID and a data value, used for older versions.
 *
 * @param id   the block/item ID.
 * @param data the data version for the item.
 */
public record LegacyIdentifier(int id, byte data) {
}
