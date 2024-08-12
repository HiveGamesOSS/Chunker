package com.hivemc.chunker.conversion.java.resolver.legacy;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.hivemc.chunker.conversion.WorldConverter;
import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.encoding.java.JavaDataVersion;
import com.hivemc.chunker.conversion.encoding.java.JavaEncoders;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack.JavaItemStackResolver;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BannerBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.ChestBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifierType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerCustomBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerItemType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerVanillaItemType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerDyeColor;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemDisplay;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemProperty;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.banner.ChunkerBannerPattern;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.enchantment.ChunkerEnchantmentType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework.ChunkerFireworkExplosion;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework.ChunkerFireworkShape;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework.ChunkerFireworks;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.resolver.property.Property;
import com.hivemc.chunker.util.JsonTextUtil;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure Legacy Java Item properties encode / decode symmetrically.
 */
public class JavaLegacyBasicItemResolverTests {
    public static final Map<ChunkerItemProperty<?>, Object> DEFAULTS = Map.of(ChunkerItemProperty.AMOUNT, 1);
    public static final Set<ChunkerItemProperty<?>> SKIPPED_PROPERTIES = Set.of(
            ChunkerItemProperty.TRIM,
            ChunkerItemProperty.STEW_EFFECT,
            ChunkerItemProperty.ENTITY,
            ChunkerItemProperty.OMINOUS_BOTTLE_AMPLIFIER
    );
    public static final Map<Class<? extends Enum<?>>, Set<?>> UNSUPPORTED_ENUMS = Map.of(
            ChunkerVanillaEntityType.class, Set.of(
                    ChunkerVanillaEntityType.MARKER,
                    ChunkerVanillaEntityType.LIGHTNING_BOLT,
                    ChunkerVanillaEntityType.INTERACTION,
                    ChunkerVanillaEntityType.BLOCK_DISPLAY,
                    ChunkerVanillaEntityType.ITEM_DISPLAY,
                    ChunkerVanillaEntityType.TEXT_DISPLAY,
                    ChunkerVanillaEntityType.ELDER_GUARDIAN_GHOST,
                    ChunkerVanillaEntityType.LINGERING_POTION,
                    ChunkerVanillaEntityType.MOVING_BLOCK,
                    ChunkerVanillaEntityType.SHIELD,
                    ChunkerVanillaEntityType.WITHER_SKULL_DANGEROUS,
                    ChunkerVanillaEntityType.ALLAY,
                    ChunkerVanillaEntityType.BREEZE,
                    ChunkerVanillaEntityType.BREEZE_WIND_CHARGE_PROJECTILE,
                    ChunkerVanillaEntityType.ARMADILLO,
                    ChunkerVanillaEntityType.WIND_CHARGE,
                    ChunkerVanillaEntityType.BOGGED,
                    ChunkerVanillaEntityType.SNIFFER,
                    ChunkerVanillaEntityType.CAMEL,
                    ChunkerVanillaEntityType.PILLAGER,
                    ChunkerVanillaEntityType.RAVAGER,
                    ChunkerVanillaEntityType.VEX,
                    ChunkerVanillaEntityType.VINDICATOR,
                    ChunkerVanillaEntityType.TRADER_LLAMA,
                    ChunkerVanillaEntityType.WANDERING_TRADER,
                    ChunkerVanillaEntityType.PLAYER,
                    ChunkerVanillaEntityType.FISHING_BOBBER,
                    ChunkerVanillaEntityType.TRIDENT,
                    ChunkerVanillaEntityType.PARROT,
                    ChunkerVanillaEntityType.PANDA,
                    ChunkerVanillaEntityType.FOX,
                    ChunkerVanillaEntityType.CAT,
                    ChunkerVanillaEntityType.PIGLIN,
                    ChunkerVanillaEntityType.HOGLIN,
                    ChunkerVanillaEntityType.STRIDER,
                    ChunkerVanillaEntityType.ZOGLIN,
                    ChunkerVanillaEntityType.PIGLIN_BRUTE,
                    ChunkerVanillaEntityType.GOAT,
                    ChunkerVanillaEntityType.GLOW_SQUID,
                    ChunkerVanillaEntityType.AXOLOTL,
                    ChunkerVanillaEntityType.SQUID,
                    ChunkerVanillaEntityType.TADPOLE,
                    ChunkerVanillaEntityType.FROG,
                    ChunkerVanillaEntityType.WARDEN,
                    ChunkerVanillaEntityType.BEE,
                    ChunkerVanillaEntityType.CHEST_BOAT,
                    ChunkerVanillaEntityType.PHANTOM,
                    ChunkerVanillaEntityType.TURTLE,
                    ChunkerVanillaEntityType.COD,
                    ChunkerVanillaEntityType.SALMON,
                    ChunkerVanillaEntityType.PUFFERFISH,
                    ChunkerVanillaEntityType.TROPICAL_FISH,
                    ChunkerVanillaEntityType.DROWNED,
                    ChunkerVanillaEntityType.DOLPHIN,
                    ChunkerVanillaEntityType.LLAMA,
                    ChunkerVanillaEntityType.LLAMA_SPIT,
                    ChunkerVanillaEntityType.EVOKER,
                    ChunkerVanillaEntityType.EVOKER_FANGS,
                    ChunkerVanillaEntityType.GLOW_ITEM_FRAME,
                    ChunkerVanillaEntityType.ILLUSIONER
            ),
            ChunkerPotionType.class, Set.of(
                    ChunkerPotionType.EMPTY,
                    ChunkerPotionType.WITHER,
                    ChunkerPotionType.LONG_MUNDANE,
                    ChunkerPotionType.WIND_CHARGED,
                    ChunkerPotionType.WEAVING,
                    ChunkerPotionType.OOZING,
                    ChunkerPotionType.INFESTED
            ),
            ChunkerEnchantmentType.class, Set.of(
                    ChunkerEnchantmentType.SOUL_SPEED,
                    ChunkerEnchantmentType.SWIFT_SNEAK,
                    ChunkerEnchantmentType.MULTISHOT,
                    ChunkerEnchantmentType.PIERCING,
                    ChunkerEnchantmentType.QUICK_CHARGE,
                    ChunkerEnchantmentType.IMPALING,
                    ChunkerEnchantmentType.RIPTIDE,
                    ChunkerEnchantmentType.LOYALTY,
                    ChunkerEnchantmentType.CHANNELING,
                    ChunkerEnchantmentType.WIND_BURST,
                    ChunkerEnchantmentType.DENSITY,
                    ChunkerEnchantmentType.BREACH
            )
    );
    public static final JavaDataVersion VERSION = JavaDataVersion.V1_12_2;
    // Mock converter with two mock maps
    public static final WorldConverter converter = new MockConverter(new ChunkerLevel(null, null, List.of(
            new ChunkerMap(
                    1,
                    1,
                    100,
                    100,
                    (byte) 0,
                    Dimension.OVERWORLD,
                    0,
                    0,
                    true,
                    true,
                    null
            ),
            new ChunkerMap(
                    2,
                    2,
                    100,
                    100,
                    (byte) 0,
                    Dimension.OVERWORLD,
                    0,
                    0,
                    true,
                    true,
                    null
            )

    ), null, Collections.emptyList()));
    public static final JavaResolvers resolvers = JavaEncoders.getNearestEncoder(VERSION)
            .writerConstructor()
            .construct(null, VERSION.getVersion(), converter)
            .buildResolvers(converter).build();
    public static final JavaItemStackResolver RESOLVER = new JavaItemStackResolver(resolvers);

    @SuppressWarnings({"rawtypes"})
    public static Stream<Arguments> propertyTests() {
        Map<String, ChunkerItemProperty> properties = Property.getProperties(ChunkerItemProperty.class);

        return properties.values().stream().filter(a -> !SKIPPED_PROPERTIES.contains(a)).map(chunkerItemProperty -> Arguments.of(
                chunkerItemProperty,
                generatePropertyValues(chunkerItemProperty.getType(), chunkerItemProperty)
        ));
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] generatePropertyValues(Type type, ChunkerItemProperty<?> property) {
        Class<T> asClass = type instanceof Class<?> ? (Class<T>) type : ((Class<T>) ((ParameterizedType) type).getRawType());
        if (asClass.isEnum()) {
            Preconditions.checkArgument(asClass.getEnumConstants().length > 0, "Enum missing values! " + asClass.getName());

            // Ensure we only return constants that can actually be converted
            T[] constants = asClass.getEnumConstants();
            Set<T> unsupported = (Set<T>) UNSUPPORTED_ENUMS.get(asClass);

            // Remove unsupported entries
            if (unsupported != null) {
                constants = (T[]) Arrays.stream(constants).filter(entry -> !unsupported.contains(entry)).toArray();
            }
            return constants;
        } else if (asClass.equals(String.class)) {
            return (T[]) new String[]{"Test", "Test 123", "", "123"};
        } else if (asClass.equals(JsonElement.class)) {
            return (T[]) new JsonElement[]{
                    JsonTextUtil.fromText("Text"),
                    JsonTextUtil.fromText("Test 123"),
                    JsonTextUtil.fromText(""),
                    JsonTextUtil.fromText("123"),
            };
        } else if (asClass.equals(Integer.class)) {
            if (property == ChunkerItemProperty.DURABILITY || property == ChunkerItemProperty.ENCHANTMENTS) {
                // Bedrock only handles durability up to short max value
                return (T[]) new Integer[]{0, -1, (int) Short.MIN_VALUE, (int) Short.MAX_VALUE, 1337};
            } else if (property == ChunkerItemProperty.AMOUNT) {
                // Amount is actually a byte
                return (T[]) new Integer[]{0, -1, (int) Byte.MAX_VALUE, (int) Byte.MIN_VALUE};
            } else if (property == ChunkerItemProperty.MAP_INDEX) {
                // Just use index 0 and 1 since that is our pretend maps
                return (T[]) new Integer[]{0, 1};
            } else {
                return (T[]) new Integer[]{0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1337};
            }
        } else if (asClass.equals(Boolean.class)) {
            return (T[]) new Boolean[]{true, false};
        } else if (asClass.equals(BlockEntity.class)) {
            ChestBlockEntity chestBlockEntity = new ChestBlockEntity();
            chestBlockEntity.getItems().put((byte) 1, new ChunkerItemStack(ChunkerVanillaItemType.BRICK, Map.of(ChunkerItemProperty.AMOUNT, 1)));

            BannerBlockEntity bannerBlockEntity = new BannerBlockEntity();
            bannerBlockEntity.getPatterns().add(Pair.of(ChunkerDyeColor.BLUE, ChunkerBannerPattern.DIAGONAL_LEFT));
            return (T[]) new BlockEntity[]{
                    chestBlockEntity,
                    bannerBlockEntity
            };
        } else if (asClass.equals(ChunkerBlockIdentifier.class)) {
            return (T[]) new ChunkerBlockIdentifier[]{
                    new ChunkerBlockIdentifier(ChunkerVanillaBlockType.SAND),
                    new ChunkerBlockIdentifier(ChunkerVanillaBlockType.END_STONE),
                    new ChunkerBlockIdentifier(new ChunkerCustomBlockType("test:block", Collections.emptySet())),
            };
        } else if (asClass.equals(ChunkerItemStackIdentifier.class)) {
            return (T[]) Stream.<ChunkerItemStackIdentifier>concat(
                    Arrays.stream(generatePropertyValues(ChunkerVanillaItemType.class, property)),
                    Arrays.stream(generatePropertyValues(ChunkerBlockIdentifier.class, property))
            ).toArray();
        } else if (asClass.equals(ChunkerItemStackIdentifierType.class)) {
            return (T[]) Stream.<ChunkerItemStackIdentifierType>concat(
                    Arrays.stream(generatePropertyValues(ChunkerVanillaItemType.class, property)),
                    Stream.of(new ChunkerCustomBlockType("customblock:test", Collections.emptySet()))
            ).toArray();
        } else if (asClass.equals(ChunkerItemType.class)) {
            return generatePropertyValues(ChunkerVanillaItemType.class, property);
        } else if (asClass.equals(ChunkerItemDisplay.class)) {
            return (T[]) new ChunkerItemDisplay[]{
                    new ChunkerItemDisplay(JsonTextUtil.fromText("Hi"), null, null),
                    new ChunkerItemDisplay(null, List.of(JsonTextUtil.fromText("Hi!")), null),
                    new ChunkerItemDisplay(null, null, Color.BLACK),
                    new ChunkerItemDisplay(JsonTextUtil.fromText("Hi"), List.of(JsonTextUtil.fromText("Hello")), Color.RED),
                    new ChunkerItemDisplay(JsonTextUtil.fromText("Hi"), List.of(), Color.RED),
                    new ChunkerItemDisplay(JsonTextUtil.fromText("Hi"), List.of(JsonTextUtil.fromText("Hi"), JsonTextUtil.fromText("Hello")), null)
            };
        } else if (asClass.equals(ChunkerFireworks.class)) {
            return (T[]) new ChunkerFireworks[]{
                    new ChunkerFireworks((byte) 2, Collections.emptyList()),
                    new ChunkerFireworks((byte) 1, List.of(new ChunkerFireworkExplosion(
                            ChunkerFireworkShape.SMALL_BALL,
                            List.of(Color.RED, Color.GREEN),
                            List.of(Color.RED, Color.GREEN),
                            false,
                            false
                    ))),
                    new ChunkerFireworks((byte) 1, List.of(new ChunkerFireworkExplosion(
                            ChunkerFireworkShape.LARGE_BALL,
                            List.of(Color.RED),
                            List.of(Color.GREEN),
                            true,
                            true
                    ))),
                    new ChunkerFireworks((byte) 1, List.of(new ChunkerFireworkExplosion(
                            ChunkerFireworkShape.CREEPER,
                            List.of(),
                            List.of(),
                            true,
                            true
                    ))),
            };
        } else if (asClass.equals(List.class)) {
            Type listType = Object.class;
            if (type instanceof ParameterizedType) {
                listType = ((ParameterizedType) type).getActualTypeArguments()[0];
            }
            return (T[]) new List[]{
                    List.of(), // Empty list
                    List.of(generatePropertyValues(listType, property)), // Single value list
                    List.of(generatePropertyValues(listType, property)) // Every value list
            };
        } else if (asClass.equals(Pair.class)) {
            Type keyType = Object.class;
            if (type instanceof ParameterizedType) {
                keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
            }
            Type valueType = Object.class;
            if (type instanceof ParameterizedType) {
                valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
            }
            Object[] keys = generatePropertyValues(keyType, property);
            Object[] values = generatePropertyValues(valueType, property);
            List<Pair<?, ?>> pairs = new ArrayList<>();
            // Now add entries ascending up to the amount
            for (int i = 0; i < Math.max(keys.length, values.length); i++) {
                pairs.add(Pair.of(keys[i % keys.length], values[i % values.length]));
            }

            return (T[]) pairs.toArray();
        } else if (asClass.equals(Map.class)) {
            Type keyType = Object.class;
            if (type instanceof ParameterizedType) {
                keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
            }
            Type valueType = Object.class;
            if (type instanceof ParameterizedType) {
                valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
            }
            Object[] keys = generatePropertyValues(keyType, property);
            Object[] values = generatePropertyValues(valueType, property);
            List<Map<?, ?>> maps = new ArrayList<>();
            maps.add(Map.of()); // Empty map

            // Now add entries ascending up to the amount
            for (int i = 0; i < Math.max(keys.length, values.length); i++) {
                Map<Object, Object> map = new Object2ObjectOpenHashMap<>();
                for (int j = 0; j < i; j++) {
                    maps.add(Map.of(keys[j % keys.length], values[j % values.length]));
                }
                maps.add(map);
            }

            return (T[]) maps.toArray();
        } else {
            throw new IllegalArgumentException("Please add example tests for " + type);
        }
    }

    @ParameterizedTest
    @MethodSource("propertyTests")
    public <T> void checkPropertySupported(ChunkerItemProperty<T> property) {
        assertTrue(RESOLVER.getSupportedProperties().contains(property));
    }

    @ParameterizedTest
    @MethodSource("propertyTests")
    public <T> void checkPropertyValues(ChunkerItemProperty<T> property, T[] propertyValues) {
        for (T value : propertyValues) {
            ChunkerItemStack item;

            // Block entity needs specific block types
            if (property == ChunkerItemProperty.BLOCK_ENTITY) {
                if (value instanceof ChestBlockEntity) {
                    item = new ChunkerItemStack(new ChunkerBlockIdentifier(ChunkerVanillaBlockType.CHEST));
                } else {
                    item = new ChunkerItemStack(new ChunkerBlockIdentifier(ChunkerVanillaBlockType.WHITE_BANNER));
                }
            } else {
                // Use AIR
                item = new ChunkerItemStack(ChunkerBlockIdentifier.AIR);
            }
            item.put(property, value);
            Optional<CompoundTag> from = RESOLVER.from(item);
            assertTrue(from.isPresent(), () -> "Failed to convert " + item);

            Optional<ChunkerItemStack> to = RESOLVER.to(from.get());
            assertTrue(to.isPresent(), () -> "Failed to convert " + from.get());

            // Check property equals
            assertEquals(value, to.get().get(property));
        }
    }

    @ParameterizedTest
    @MethodSource("propertyTests")
    public <T> void checkPropertyAbsent(ChunkerItemProperty<T> property) {
        ChunkerItemStack item = new ChunkerItemStack(ChunkerBlockIdentifier.AIR);
        Optional<CompoundTag> from = RESOLVER.from(item);
        assertTrue(from.isPresent());

        Optional<ChunkerItemStack> to = RESOLVER.to(from.get());
        assertTrue(to.isPresent());

        // If the property has a default value check it is the default value
        if (DEFAULTS.containsKey(property)) {
            assertEquals(DEFAULTS.get(property), to.get().get(property));
        } else {
            assertNull(to.get().get(property));
        }
    }
}
