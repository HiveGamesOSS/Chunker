package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.map;

/**
 * The decoration on a filled map which points at the structure it was made for.
 *
 * @param structure the structure the decoration points at.
 * @param x         the X co-ordinate of the structure.
 * @param z         the Z co-ordinate of the structure.
 * @param rotation  the rotation of the decoration in degrees.
 */
public record ChunkerMapDecoration(ChunkerExplorerMap structure, double x, double z, float rotation) {
}
