package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ComparableItemProperty;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.enchantment.ChunkerEnchantmentType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework.ChunkerFireworks;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.horn.ChunkerHornInstrument;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.stew.ChunkerStewEffect;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrim;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.resolver.property.Property;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * A property type which can be set on ItemStacks.
 *
 * @param <V> the type that the property holds.
 */
public class ChunkerItemProperty<V> extends Property<ChunkerItemStack, V> implements ComparableItemProperty<V> {
    /**
     * The amount of the item in the stack, ranging from 1-64 (can be higher on newer Java versions).
     */
    public static final ChunkerItemProperty<Integer> AMOUNT = new ChunkerItemProperty<>(
            "amount",
            Integer.class
    );
    /**
     * The block entity attached to the item (can happen when it's not a block e.g. shields having banners).
     */
    public static final ChunkerItemProperty<BlockEntity> BLOCK_ENTITY = new ChunkerItemProperty<>(
            "block_entity",
            BlockEntity.class
    );
    /**
     * The author of a book.
     */
    public static final ChunkerItemProperty<String> BOOK_AUTHOR = new ChunkerItemProperty<>(
            "book_author",
            String.class
    );
    /**
     * The JSON for each page of a book.
     */
    public static final ChunkerItemProperty<List<JsonElement>> BOOK_PAGES = new ChunkerItemProperty<>(
            "book_pages",
            new TypeToken<>() {
            }
    );
    /**
     * The title of a book.
     */
    public static final ChunkerItemProperty<String> BOOK_TITLE = new ChunkerItemProperty<>(
            "book_title",
            String.class
    );
    /**
     * A list of blocks this item can destroy.
     */
    public static final ChunkerItemProperty<List<ChunkerBlockIdentifier>> CAN_DESTROY = new ChunkerItemProperty<>(
            "can_destroy",
            new TypeToken<>() {
            }
    );
    /**
     * A list of blocks this block can be placed on.
     */
    public static final ChunkerItemProperty<List<ChunkerBlockIdentifier>> CAN_PLACE_ON = new ChunkerItemProperty<>(
            "can_place_on",
            new TypeToken<>() {
            }
    );
    /**
     * The display properties for the item (hover text).
     */
    public static final ChunkerItemProperty<ChunkerItemDisplay> DISPLAY = new ChunkerItemProperty<>(
            "display",
            ChunkerItemDisplay.class
    );
    /**
     * The durability of the item when it's a tool / armor.
     */
    public static final ChunkerItemProperty<Integer> DURABILITY = new ChunkerItemProperty<>(
            "durability",
            Integer.class
    );
    /**
     * A map of enchantments and level that are active on the item.
     */
    public static final ChunkerItemProperty<Map<ChunkerEnchantmentType, Integer>> ENCHANTMENTS = new ChunkerItemProperty<>(
            "enchantments",
            new TypeToken<>() {
            }
    );
    /**
     * The entity attached to the item (used for paintings on Java).
     */
    public static final ChunkerItemProperty<Entity> ENTITY = new ChunkerItemProperty<>(
            "entity",
            Entity.class
    );
    /**
     * The firework effects when it is launched.
     */
    public static final ChunkerItemProperty<ChunkerFireworks> FIREWORKS = new ChunkerItemProperty<>(
            "fireworks",
            ChunkerFireworks.class
    );
    /**
     * The horn instrument to use.
     */
    public static final ChunkerItemProperty<ChunkerHornInstrument> HORN_INSTRUMENT = new ChunkerItemProperty<>(
            "horn_instrument",
            ChunkerHornInstrument.class
    );
    /**
     * The index of the map from the level to use when shown in-game.
     */
    public static final ChunkerItemProperty<Integer> MAP_INDEX = new ChunkerItemProperty<>(
            "map_index",
            Integer.class
    );
    /**
     * The amplifier for the ominous bottle.
     */
    public static final ChunkerItemProperty<Integer> OMINOUS_BOTTLE_AMPLIFIER = new ChunkerItemProperty<>(
            "ominous_bottle_amplifier",
            Integer.class
    );
    /**
     * The potion effect which is used when consumed.
     */
    public static final ChunkerItemProperty<ChunkerPotionType> POTION = new ChunkerItemProperty<>(
            "potion",
            ChunkerPotionType.class
    );
    /**
     * The cost of repairing the tool / armor in an anvil.
     */
    public static final ChunkerItemProperty<Integer> REPAIR_COST = new ChunkerItemProperty<>(
            "repair_cost",
            new TypeToken<>() {
            }
    );
    /**
     * The entity which should be spawned from an egg.
     */
    public static final ChunkerItemProperty<ChunkerEntityType> SPAWN_EGG_MOB = new ChunkerItemProperty<>(
            "spawn_egg_mob",
            ChunkerEntityType.class
    );
    /**
     * The stew effect which occurs when the item is consumed.
     */
    public static final ChunkerItemProperty<ChunkerStewEffect> STEW_EFFECT = new ChunkerItemProperty<>(
            "stew_effects",
            ChunkerStewEffect.class
    );
    /**
     * The trim decoration for armor.
     */
    public static final ChunkerItemProperty<ChunkerTrim> TRIM = new ChunkerItemProperty<>(
            "trim",
            ChunkerTrim.class
    );
    /**
     * The inventory for a bundle.
     */
    public static final ChunkerItemProperty<List<ChunkerItemStack>> BUNDLE_CONTENTS = new ChunkerItemProperty<>(
            "bundle_contents",
            new TypeToken<>() {
            }
    );

    /**
     * Create a new item property using a type token.
     *
     * @param name the unique name of the property, used for comparison.
     * @param type the type used.
     */
    private ChunkerItemProperty(String name, Class<? super V> type) {
        super(name, type);
    }

    /**
     * Create a new item property using a type token.
     *
     * @param name the unique name of the property, used for comparison.
     * @param type the type token used to capture the exact generic parameters of the type.
     */
    private ChunkerItemProperty(String name, TypeToken<? super V> type) {
        super(name, type.getType());
    }

    @Override
    public int compareTo(@NotNull ComparableItemProperty<?> other) {
        if (other instanceof ChunkerItemProperty<?> o) {
            // Compare name
            int value = getName().compareTo(o.getName());
            if (value != 0) return value;
        }

        // Compare class name
        return getClass().getName().compareTo(other.getClass().getName());
    }
}
