package com.hivemc.chunker.mapping;

import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValueBoolean;
import com.hivemc.chunker.mapping.identifier.states.StateValueInt;
import com.hivemc.chunker.mapping.identifier.states.StateValueString;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests to ensure Identifier behaves as expected.
 */
public class IdentifierStateTests {
    @Test
    public void testIdentifierEquals() {
        assertEquals(
                new Identifier("minecraft:stone"),
                new Identifier("minecraft:stone")
        );
    }

    @Test
    public void testIdentifier2Equals() {
        assertNotEquals(
                new Identifier("minecraft:stone"),
                new Identifier("minecraft:iron")
        );
    }

    @Test
    public void testIdentifierStatesEquals() {
        assertEquals(
                new Identifier("minecraft:stone", Map.of("hello", new StateValueInt(1))),
                new Identifier("minecraft:stone", Map.of("hello", new StateValueInt(1)))
        );
    }

    @Test
    public void testIdentifierStatesEquals2() {
        assertEquals(
                new Identifier("minecraft:stone", Map.of("hello", StateValueBoolean.TRUE)),
                new Identifier("minecraft:stone", Map.of("hello", StateValueBoolean.TRUE))
        );
    }

    @Test
    public void testIdentifierStatesEquals2Illegal() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<StateValueBoolean> privateConstructor = StateValueBoolean.class.getDeclaredConstructor(boolean.class);
        privateConstructor.setAccessible(true);

        assertEquals(
                new Identifier("minecraft:stone", Map.of("hello", StateValueBoolean.TRUE)),
                new Identifier("minecraft:stone", Map.of("hello", privateConstructor.newInstance(true)))
        );
    }

    @Test
    public void testIdentifierStatesEquals3() {
        assertEquals(
                new Identifier("minecraft:stone", Map.of("hello", new StateValueString("hi!"))),
                new Identifier("minecraft:stone", Map.of("hello", new StateValueString("hi!")))
        );
    }

    @Test
    public void testIdentifierHashcode() {
        assertEquals(
                new Identifier("minecraft:stone").hashCode(),
                new Identifier("minecraft:stone").hashCode()
        );
    }

    @Test
    public void testIdentifier2Hashcode() {
        assertNotEquals(
                new Identifier("minecraft:stone").hashCode(),
                new Identifier("minecraft:iron").hashCode()
        );
    }

    @Test
    public void testIdentifierStatesHashcode() {
        assertEquals(
                new Identifier("minecraft:stone", Map.of("hello", new StateValueInt(1))).hashCode(),
                new Identifier("minecraft:stone", Map.of("hello", new StateValueInt(1))).hashCode()
        );
    }

    @Test
    public void testIdentifierString() {
        assertEquals(
                new Identifier("minecraft:stone").toString(),
                new Identifier("minecraft:stone").toString()
        );
    }

    @Test
    public void testIdentifierString2() {
        assertEquals(
                new Identifier("minecraft:stone").toString(),
                "minecraft:stone"
        );
    }

    @Test
    public void testIdentifier2String() {
        assertNotEquals(
                new Identifier("minecraft:stone").toString(),
                new Identifier("minecraft:iron").toString()
        );
    }

    @Test
    public void testIdentifierStatesString() {
        assertEquals(
                new Identifier("minecraft:stone", Map.of("hello", new StateValueInt(1))).toString(),
                new Identifier("minecraft:stone", Map.of("hello", new StateValueInt(1))).toString()
        );
    }

    @Test
    public void testIdentifierStatesString2() {
        assertEquals(
                "minecraft:stone[hello=1]",
                new Identifier("minecraft:stone", Map.of("hello", new StateValueInt(1))).toString()
        );
    }

    @Test
    public void testIdentifierStatesString3() {
        assertEquals(
                "minecraft:stone[hello=true]",
                new Identifier("minecraft:stone", Map.of("hello", StateValueBoolean.TRUE)).toString()
        );
    }
}