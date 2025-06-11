package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack.legacy;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.resolver.itemstack.ItemStackResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy.JavaLegacyEntityTypeIDResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy.JavaLegacyItemIDResolver;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerVanillaItemType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemDisplay;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemProperty;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.enchantment.ChunkerEnchantmentType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework.ChunkerFireworkExplosion;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework.ChunkerFireworkShape;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework.ChunkerFireworks;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.ShortTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import com.hivemc.chunker.resolver.property.PropertyHandler;
import com.hivemc.chunker.util.JsonTextUtil;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Resolver for converting Java Legacy NBT to the Chunker ItemStack and resolving all the properties of the item.
 */
public class JavaLegacyItemStackResolver extends ItemStackResolver<JavaResolvers, CompoundTag> {
    private JavaLegacyItemIDResolver legacyItemIDResolver;

    /**
     * Create a new legacy java item stack resolver.
     *
     * @param resolvers the resolvers to use.
     */
    public JavaLegacyItemStackResolver(JavaResolvers resolvers) {
        super(resolvers);
        this.legacyItemIDResolver = new JavaLegacyItemIDResolver(resolvers.dataVersion().getVersion());
    }

    @Override
    protected void registerHandlers(JavaResolvers resolvers) {
        registerHandler(ChunkerItemProperty.AMOUNT, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull CompoundTag value) {
                return Optional.of((int) value.getByte("Count", (byte) 1));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer count) {
                value.put("Count", count.byteValue());
            }
        });
        registerHandler(ChunkerItemProperty.DURABILITY, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull CompoundTag value) {
                return value.getOptional("tag", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalValue("Damage", Short.class).map(Short::intValue));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer damage) {
                value.getOrCreateCompound("tag").put("Damage", damage.shortValue());
            }
        });
        registerHandler(ChunkerItemProperty.DISPLAY, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerItemDisplay> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("tag");
                if (tag == null) return Optional.empty();

                // Check for display tag
                String name = null;
                List<JsonElement> lore = null;
                Color color = null;

                CompoundTag display = tag.getCompound("display");
                if (display != null) {
                    name = display.getString("Name", null);
                    List<String> loreString = display.getListValues("Lore", StringTag.class, null);

                    // Convert string lore to JSON
                    if (loreString != null) {
                        lore = loreString.stream().map(JsonTextUtil::fromJSON).collect(Collectors.toList());
                    }

                    if (display.contains("color")) {
                        color = new Color(display.getInt("color"));
                    }
                }

                // Build the display if one of the components is present
                return name != null || lore != null || color != null ? Optional.of(new ChunkerItemDisplay(
                        name != null ? JsonTextUtil.fromJSON(name) : null,
                        lore,
                        color
                )) : Optional.empty();
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull ChunkerItemDisplay chunkerItemDisplay) {
                CompoundTag display = value.getOrCreateCompound("tag").getOrCreateCompound("display");

                // Display Name
                if (chunkerItemDisplay.displayName() != null) {
                    display.put("Name", JsonTextUtil.toJSON(chunkerItemDisplay.displayName()));
                }

                // Lore
                if (chunkerItemDisplay.lore() != null) {
                    display.put(
                            "Lore",
                            ListTag.fromValues(TagType.STRING, Lists.transform(chunkerItemDisplay.lore(), JsonTextUtil::toJSON))
                    );
                }

                // Color
                if (chunkerItemDisplay.color() != null) {
                    // Note: This is in the tag root
                    display.put("color", chunkerItemDisplay.color().getRGB());
                }
            }
        });

        // General block properties
        registerHandler(ChunkerItemProperty.CAN_PLACE_ON, new PropertyHandler<>() {
            @Override
            public Optional<List<ChunkerBlockIdentifier>> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("tag");
                if (tag == null) return Optional.empty();

                // Check for CanPlaceOn
                List<String> names = tag.getListValues("CanPlaceOn", StringTag.class, null);
                if (names == null) return Optional.empty();

                // Create the new list and resolve each block
                List<ChunkerBlockIdentifier> identifiers = new ArrayList<>(names.size());
                for (String name : names) {
                    ChunkerBlockIdentifier identifier = resolvers.readBlockIdentifier(new Identifier(name)).copyWithout(VanillaBlockStates.WATERLOGGED);
                    if (!identifier.isAir()) {
                        identifiers.add(identifier);
                    }
                }
                return Optional.of(identifiers);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull List<ChunkerBlockIdentifier> chunkerBlockIdentifiers) {
                // Create the new list and resolve each block
                List<String> identifiers = new ArrayList<>(chunkerBlockIdentifiers.size());
                for (ChunkerBlockIdentifier chunkerBlockIdentifier : chunkerBlockIdentifiers) {
                    Optional<Identifier> identifier = resolvers.writeBlockIdentifier(chunkerBlockIdentifier, true);
                    if (identifier.isPresent() && !identifier.get().getIdentifier().equals("minecraft:air")) {
                        identifiers.add(identifier.get().getIdentifier());
                    }
                }

                CompoundTag tag = value.getOrCreateCompound("tag");
                tag.put("CanPlaceOn", ListTag.fromValues(TagType.STRING, identifiers));
            }
        });
        registerHandler(ChunkerItemProperty.CAN_DESTROY, new PropertyHandler<>() {
            @Override
            public Optional<List<ChunkerBlockIdentifier>> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("tag");
                if (tag == null) return Optional.empty();

                // Check for CanDestroy
                List<String> names = tag.getListValues("CanDestroy", StringTag.class, null);
                if (names == null) return Optional.empty();

                // Create the new list and resolve each block
                List<ChunkerBlockIdentifier> identifiers = new ArrayList<>(names.size());
                for (String name : names) {
                    ChunkerBlockIdentifier identifier = resolvers.readBlockIdentifier(new Identifier(name))
                            .copyWithout(VanillaBlockStates.WATERLOGGED);
                    if (!identifier.isAir()) {
                        identifiers.add(identifier);
                    }
                }
                return Optional.of(identifiers);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull List<ChunkerBlockIdentifier> chunkerBlockIdentifiers) {
                // Create the new list and resolve each block
                List<String> identifiers = new ArrayList<>(chunkerBlockIdentifiers.size());
                for (ChunkerBlockIdentifier chunkerBlockIdentifier : chunkerBlockIdentifiers) {
                    Optional<Identifier> identifier = resolvers.writeBlockIdentifier(chunkerBlockIdentifier, true);
                    if (identifier.isPresent() && !identifier.get().getIdentifier().equals("minecraft:air")) {
                        identifiers.add(identifier.get().getIdentifier());
                    }
                }

                CompoundTag tag = value.getOrCreateCompound("tag");
                tag.put("CanDestroy", ListTag.fromValues(TagType.STRING, identifiers));
            }
        });
        registerHandler(ChunkerItemProperty.REPAIR_COST, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull CompoundTag value) {
                return value.getOptional("tag", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalValue("RepairCost", Integer.class));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer repairCost) {
                value.getOrCreateCompound("tag").put("RepairCost", repairCost);
            }
        });
        registerContextualHandler(ChunkerItemProperty.ENCHANTMENTS, new PropertyHandler<>() {
            @Override
            public Optional<Map<ChunkerEnchantmentType, Integer>> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Check if tag is present
                CompoundTag tag = state.value().getCompound("tag");
                if (tag == null) return Optional.empty();

                // Check for enchantments
                String name = tag.contains("StoredEnchantments") ? "StoredEnchantments" : "Enchantments";
                ListTag<CompoundTag, Map<String, Tag<?>>> enchantmentTags = tag.getList(name, CompoundTag.class, null);
                if (enchantmentTags == null) return Optional.empty();

                // Create the new map and resolve each enchantment
                Map<ChunkerEnchantmentType, Integer> enchantments = new EnumMap<>(ChunkerEnchantmentType.class);
                for (CompoundTag enchantment : enchantmentTags) {
                    short id = enchantment.getShort("id", (short) -1);
                    if (id == -1) continue; // Skip enchantment

                    // Resolve the enchantment
                    Optional<ChunkerEnchantmentType> enchantmentType = resolvers.enchantmentIDResolver().to((int) id);
                    if (enchantmentType.isEmpty()) {
                        resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENCHANTMENT, String.valueOf(id));
                        continue; // Skip enchantment if it's not found
                    }

                    int level = enchantment.getShort("lvl", (short) 1); // Assume level 1 if the value isn't present

                    // Add the enchantment
                    enchantments.put(enchantmentType.get(), level);
                }
                return Optional.of(enchantments);
            }

            @Override
            public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull Map<ChunkerEnchantmentType, Integer> value) {
                ListTag<CompoundTag, Map<String, Tag<?>>> enchantments = new ListTag<>(TagType.COMPOUND, new ArrayList<>(value.size()));
                for (Map.Entry<ChunkerEnchantmentType, Integer> enchantment : value.entrySet()) {
                    Optional<Integer> id = resolvers.enchantmentIDResolver().from(enchantment.getKey());
                    if (id.isEmpty()) {
                        resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENCHANTMENT, String.valueOf(enchantment.getKey()));
                        continue; // Don't include not supported enchantments
                    }

                    CompoundTag enchantmentTag = new CompoundTag();
                    enchantmentTag.put("id", id.get().shortValue());
                    enchantmentTag.put("lvl", enchantment.getValue().shortValue());
                    enchantments.add(enchantmentTag);
                }

                // Finally add the tag
                String name = state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments";
                state.value().getOrCreateCompound("tag").put(name, enchantments);
            }
        });

        // Maps
        registerContextualHandler(ChunkerItemProperty.MAP_INDEX, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                if (state.key().getIdentifier().getItemStackType() != ChunkerVanillaItemType.FILLED_MAP) {
                    return Optional.empty(); // Not a map
                }

                // Check for map
                Optional<Short> id = state.value().getOptionalValue("Damage", Short.class);
                if (id.isPresent() && resolvers.converter().level().isPresent()) {
                    ChunkerLevel level = resolvers.converter().level().get();
                    return level.findMapIndexByOriginalID(id.get());
                }
                return Optional.empty();
            }

            @Override
            public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull Integer index) {
                if (state.key().getIdentifier().getItemStackType() != ChunkerVanillaItemType.FILLED_MAP) {
                    return; // Not a map
                }

                if (resolvers.converter().level().isPresent()) {
                    Optional<ChunkerMap> chunkerMap = resolvers.converter().level().get().getMapByIndex(index);
                    chunkerMap.ifPresent(map -> state.value().put("Damage", (short) map.getId()));
                }
            }
        });

        // Spawn Eggs (NBT is used on 1.9 and above)
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 9, 0)) {
            registerContextualHandler(ChunkerItemProperty.SPAWN_EGG_MOB, new PropertyHandler<>() {
                @Override
                public Optional<ChunkerEntityType> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                    // Check if tag is present
                    CompoundTag tag = state.value().getCompound("tag");
                    if (tag == null) return Optional.empty();

                    // Check for EntityTag
                    CompoundTag entityTag = tag.getCompound("EntityTag");
                    if (entityTag == null) return Optional.empty();

                    // Turn the ID into a chunker entity type
                    return entityTag.getOptionalValue("id", String.class).flatMap((identifier) -> {
                        Optional<ChunkerEntityType> type = resolvers.entityTypeResolver().to(identifier);
                        if (type.isEmpty()) {
                            // Report missing mapping
                            resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENTITY_TYPE, identifier);

                            // If it's a spawn egg, turn the holder to null as it's not valid
                            if (state.key().getIdentifier() == ChunkerVanillaItemType.SPAWN_EGG) {
                                state.key(null);
                            }
                        }
                        return type;
                    });
                }

                @Override
                public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull ChunkerEntityType entityType) {
                    Optional<String> type = resolvers.entityTypeResolver().from(entityType);
                    if (type.isPresent()) {
                        CompoundTag entityTag = state.value().getOrCreateCompound("tag").getOrCreateCompound("EntityTag");
                        entityTag.put("id", type.get());
                    } else {
                        // Report missing mapping
                        resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENTITY_TYPE, String.valueOf(entityType));

                        // If it's a spawn egg, turn the output to null as it's not valid
                        if (state.key().getIdentifier() == ChunkerVanillaItemType.SPAWN_EGG) {
                            state.value(null);
                        }
                    }
                }
            });
        } else {
            registerContextualHandler(ChunkerItemProperty.SPAWN_EGG_MOB, new PropertyHandler<>() {
                private final JavaLegacyEntityTypeIDResolver entityTypeIDResolver = new JavaLegacyEntityTypeIDResolver(resolvers.dataVersion().getVersion());

                @Override
                public Optional<ChunkerEntityType> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                    if (state.key().getIdentifier().getItemStackType() != ChunkerVanillaItemType.SPAWN_EGG) {
                        return Optional.empty(); // Not an egg
                    }

                    short dataValue = state.value().getShort("Damage", (short) 0);
                    return entityTypeIDResolver.to(dataValue & 0xFF);
                }

                @Override
                public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull ChunkerEntityType value) {
                    if (state.key().getIdentifier().getItemStackType() != ChunkerVanillaItemType.SPAWN_EGG) {
                        return; // Not possible
                    }

                    short damageValue = entityTypeIDResolver.from(value).orElse(0).shortValue();

                    // Set the damage value
                    state.value().put("Damage", damageValue);
                }
            });
        }

        // Books
        registerHandler(ChunkerItemProperty.BOOK_TITLE, new PropertyHandler<>() {
            @Override
            public Optional<String> read(@NotNull CompoundTag value) {
                return value.getOptional("tag", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalValue("title", String.class));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull String title) {
                CompoundTag tag = value.getOrCreateCompound("tag");
                tag.put("title", title);
            }
        });
        registerHandler(ChunkerItemProperty.BOOK_AUTHOR, new PropertyHandler<>() {
            @Override
            public Optional<String> read(@NotNull CompoundTag value) {
                return value.getOptional("tag", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalValue("author", String.class));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull String author) {
                CompoundTag tag = value.getOrCreateCompound("tag");
                tag.put("author", author);
            }
        });
        registerContextualHandler(ChunkerItemProperty.BOOK_PAGES, new PropertyHandler<>() {
            @Override
            public Optional<List<JsonElement>> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Check if tag is present
                CompoundTag tag = state.right().getCompound("tag");
                if (tag == null) return Optional.empty();

                // Check for pages
                List<String> pages = tag.getListValues("pages", StringTag.class, null);
                if (pages == null) return Optional.empty();

                // Loop through each page and extract the text
                List<JsonElement> pagesJSON = new ArrayList<>(pages.size());
                for (String page : pages) {
                    // If it's a writable book it's not json
                    if (state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK) {
                        pagesJSON.add(JsonTextUtil.fromText(page));
                    } else {
                        pagesJSON.add(JsonTextUtil.fromJSON(page));
                    }
                }
                return Optional.of(pagesJSON);
            }

            @Override
            public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull List<JsonElement> pagesJSON) {
                CompoundTag tag = state.right().getOrCreateCompound("tag");
                ListTag<StringTag, String> pages = new ListTag<>(TagType.STRING, new ArrayList<>(pagesJSON.size()));
                for (JsonElement pageJSON : pagesJSON) {
                    // If it's a writable book then it's not json
                    if (state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK) {
                        pages.add(new StringTag(JsonTextUtil.toLegacy(pageJSON, false)));
                    } else {
                        pages.add(new StringTag(JsonTextUtil.toJSON(pageJSON)));
                    }
                }

                tag.put("pages", pages);
            }

            @Override
            public @Nullable List<JsonElement> getDefaultValue(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Always use empty list for written book
                if (state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITTEN_BOOK) {
                    return Collections.emptyList();
                }

                // Return normal default
                return PropertyHandler.super.getDefaultValue(state);
            }
        });

        // Potions (NBT is used on 1.9 and above)
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 9, 0)) {
            registerHandler(ChunkerItemProperty.POTION, new PropertyHandler<>() {
                @Override
                public Optional<ChunkerPotionType> read(@NotNull CompoundTag value) {
                    return value.getOptional("tag", CompoundTag.class)
                            .flatMap(tag -> tag.getOptionalValue("Potion", String.class))
                            .map(resolvers::readPotionType);
                }

                @Override
                public void write(@NotNull CompoundTag value, @NotNull ChunkerPotionType potionType) {
                    CompoundTag tag = value.getOrCreateCompound("tag");
                    tag.put("Potion", resolvers.writePotionType(potionType));
                }
            });
        } else {
            registerContextualHandler(ChunkerItemProperty.POTION, new PropertyHandler<>() {
                private final JavaLegacyPotionIDResolver potionIDResolver = new JavaLegacyPotionIDResolver(resolvers.dataVersion().getVersion());

                @Override
                public Optional<ChunkerPotionType> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                    if (state.key().getIdentifier().getItemStackType() != ChunkerVanillaItemType.POTION) {
                        return Optional.empty(); // No potion
                    }

                    short dataValue = state.value().getShort("Damage", (short) 0);
                    boolean splash = (dataValue & 0x4000) == 0x4000;

                    // Change the potion to a splash potion
                    if (splash) {
                        state.key(new ChunkerItemStack(
                                ChunkerVanillaItemType.SPLASH_POTION,
                                state.key().getPreservedIdentifier(),
                                state.key().getProperties()
                        ));
                    }

                    // Parse the data value
                    return potionIDResolver.to(dataValue & 0x7F);
                }

                @Override
                public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull ChunkerPotionType value) {
                    boolean splash = state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.SPLASH_POTION;
                    if (state.key().getIdentifier().getItemStackType() != ChunkerVanillaItemType.POTION && !splash) {
                        return; // Not possible to use potion
                    }

                    short damageValue = potionIDResolver.from(value).orElse(0).shortValue();
                    if (splash) {
                        damageValue |= 0x4000;
                    }

                    // Set the damage value
                    state.value().put("Damage", damageValue);
                }
            });
        }

        // Fireworks
        registerHandler(ChunkerItemProperty.FIREWORKS, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerFireworks> read(@NotNull CompoundTag value) {
                Optional<CompoundTag> component = value.getOptional("tag", CompoundTag.class)
                        .flatMap(tag -> tag.getOptional("Fireworks", CompoundTag.class));
                if (component.isEmpty()) return Optional.empty();

                // Get the duration
                byte duration = component.get().getByte("Flight", (byte) 1);

                // Get the explosions
                List<ChunkerFireworkExplosion> explosions = Collections.emptyList();
                ListTag<CompoundTag, Map<String, Tag<?>>> explosionsTag = component.get().getList("Explosions", CompoundTag.class, null);
                if (explosionsTag != null) {
                    explosions = new ArrayList<>(explosionsTag.size());
                    for (CompoundTag explosionTag : explosionsTag) {
                        // Parse the explosion properties
                        ChunkerFireworkShape shape = explosionTag.getOptionalValue("Type", Byte.class)
                                .map(Byte::intValue)
                                .flatMap(ChunkerFireworkShape::getByID)
                                .orElse(ChunkerFireworkShape.SMALL_BALL);

                        // Parse colors
                        int[] colorsRGB = explosionTag.getIntArray("Colors", null);
                        List<Color> colors = colorsRGB == null ? Collections.emptyList() : IntStream.of(colorsRGB)
                                .mapToObj(Color::new)
                                .toList();

                        // Parse fade colors
                        int[] fadeColorsRGB = explosionTag.getIntArray("FadeColors", null);
                        List<Color> fadeColors = fadeColorsRGB == null ? Collections.emptyList() : IntStream.of(fadeColorsRGB)
                                .mapToObj(Color::new)
                                .toList();

                        // Parse trail / twinkle
                        boolean trail = explosionTag.getByte("Trail", (byte) 0) == (byte) 1;
                        boolean twinkle = explosionTag.getByte("Flicker", (byte) 0) == (byte) 1;

                        explosions.add(new ChunkerFireworkExplosion(
                                shape,
                                colors,
                                fadeColors,
                                trail,
                                twinkle
                        ));
                    }
                }

                // Create the firework
                return Optional.of(new ChunkerFireworks(duration, explosions));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull ChunkerFireworks chunkerFireworks) {
                // Don't write the default
                if (chunkerFireworks.getExplosions().isEmpty() && chunkerFireworks.getFlightDuration() == 0)
                    return;

                // Write the tag
                CompoundTag tag = value.getOrCreateCompound("tag").getOrCreateCompound("Fireworks");
                tag.put("Flight", chunkerFireworks.getFlightDuration());

                // Write explosions
                if (!chunkerFireworks.getExplosions().isEmpty()) {
                    ListTag<CompoundTag, Map<String, Tag<?>>> explosions = new ListTag<>(TagType.COMPOUND);
                    for (ChunkerFireworkExplosion chunkerFireworkExplosion : chunkerFireworks.getExplosions()) {
                        CompoundTag explosion = new CompoundTag();
                        explosion.put("Type", (byte) chunkerFireworkExplosion.getShape().getID());
                        explosion.put("Colors", chunkerFireworkExplosion.getColors().stream()
                                .mapToInt(Color::getRGB)
                                .toArray()
                        );
                        explosion.put("FadeColors", chunkerFireworkExplosion.getFadeColors().stream()
                                .mapToInt(Color::getRGB)
                                .toArray()
                        );
                        explosion.put("Trail", chunkerFireworkExplosion.isTrail() ? (byte) 1 : (byte) 0);
                        explosion.put("Flicker", chunkerFireworkExplosion.isTwinkle() ? (byte) 1 : (byte) 0);
                        explosions.add(explosion);
                    }
                    tag.put("Explosions", explosions);
                }
            }
        });

        // Block Entities
        registerContextualHandler(ChunkerItemProperty.BLOCK_ENTITY, new PropertyHandler<>() {
            @Override
            public Optional<BlockEntity> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Check if tag is present
                CompoundTag tag = state.value().getCompound("tag");
                if (tag == null) return Optional.empty();

                // Check the blockEntity class associated with the chunker block / item
                Optional<Class<? extends BlockEntity>> blockEntityClass = resolvers.blockEntityResolver().getBlockEntityClass(state.key().getIdentifier().getItemStackType());
                if (blockEntityClass.isEmpty()) return Optional.empty();

                CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
                Optional<BlockEntity> generated = Optional.empty();
                if (blockEntityTag != null) {
                    generated = resolvers.blockEntityResolver().to(blockEntityClass.get(), blockEntityTag);
                }

                // Use the Item NBT block entity generator
                generated = resolvers.blockEntityResolver().generateFromItemNBT(
                        blockEntityClass.get(),
                        state.key(),
                        generated.orElse(null),
                        state.value()
                );

                // Finally apply the before process handler to ensure that we're not using non-intermediate block entities
                return generated.map(blockEntity -> resolvers.blockEntityResolver().updateBeforeProcess(
                        state.value(),
                        state.key(),
                        blockEntity
                ));
            }

            @Override
            public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull BlockEntity value) {
                // Apply the before write handler to ensure that we're using the right types
                value = resolvers.blockEntityResolver().updateBeforeWrite(
                        state.value(),
                        state.key(),
                        value
                );

                // Use the Item NBT block entity generator
                boolean writeBlockEntityData = resolvers.blockEntityResolver().writeToItemNBT(
                        state.key(),
                        value,
                        state.value()
                );

                // Parse the block entity using our normal resolver
                if (writeBlockEntityData) {
                    Optional<CompoundTag> blockEntityTag = resolvers.blockEntityResolver().from(value);
                    if (blockEntityTag.isPresent()) {
                        CompoundTag tag = blockEntityTag.get();

                        // Remove any ID/position based data
                        tag.remove("x");
                        tag.remove("y");
                        tag.remove("z");
                        tag.remove("id");
                        tag.remove("Rotation");
                        tag.remove("Rot");
                        tag.remove("facing");
                        tag.remove("Facing");

                        // Add the tag
                        state.value().getOrCreateCompound("tag").put("BlockEntityTag", tag);
                    }
                }
            }
        });
    }

    @Override
    protected Optional<ChunkerItemStack> createPropertyHolder(CompoundTag input) {
        // First turn the NBT into an identifier
        Tag<?> id = input.get("id");
        Identifier identifier;
        if (id instanceof StringTag stringTag) {
            identifier = Identifier.fromData(
                    stringTag.getValue(),
                    OptionalInt.of(input.getShort("Damage", (short) 0))
            );
        } else if (id instanceof ShortTag shortTag)  {
            Optional<String> value = legacyItemIDResolver.inverse().from((int) shortTag.getValue());

            // Couldn't find the legacy ID
            if (value.isEmpty()) {
                return Optional.empty();
            }

            // Create an identifier using the converter ID
            identifier = Identifier.fromData(
                    value.get(),
                    OptionalInt.of(input.getShort("Damage", (short) 0))
            );
        } else {
            // Couldn't find the ID
            return Optional.empty();
        }


        // Now use the item identifier reader to turn it into an item/block
        return Optional.of(resolvers.readItemIdentifier(identifier));
    }

    @Override
    protected Optional<CompoundTag> createOutput(ChunkerItemStack input) {
        CompoundTag output = new CompoundTag();

        // Use the item identifier writer to turn it into an item (if it fails, we'll encode it as a block)
        Optional<Identifier> itemIdentifier = resolvers.chunkerItemIdentifierResolver().from(input);

        // If it's an item write it as one
        if (itemIdentifier.isPresent()) {
            // Write as an item
            output.put("id", itemIdentifier.get().getIdentifier());
            if (itemIdentifier.get().getDataValue().isPresent()) {
                output.put("Damage", (short) itemIdentifier.get().getDataValue().getAsInt());
            }
            return Optional.of(output);
        } else if (input.getIdentifier() instanceof ChunkerBlockIdentifier chunkerBlockIdentifier) {
            // Before turning it into a block remove any directional states
            // This is because direction isn't used in the ID
            chunkerBlockIdentifier = chunkerBlockIdentifier.copyWithout(VanillaBlockStates.FACING_HORIZONTAL);
            chunkerBlockIdentifier = chunkerBlockIdentifier.copyWithout(VanillaBlockStates.FACING_ALL);
            chunkerBlockIdentifier = chunkerBlockIdentifier.copyWithout(VanillaBlockStates.FACING_HORIZONTAL_DOWN);

            // Turn it into a block identifier
            Optional<Identifier> blockIdentifier = resolvers.writeBlockIdentifier(chunkerBlockIdentifier, false);
            if (blockIdentifier.isEmpty()) return Optional.empty(); // Not possible to create the stack

            // Just write the id / data
            output.put("id", blockIdentifier.get().getIdentifier());
            if (blockIdentifier.get().getDataValue().isPresent()) {
                output.put("Damage", (short) blockIdentifier.get().getDataValue().getAsInt());
            }
            return Optional.of(output);
        } else {
            return Optional.empty(); // Unable to write item / block
        }
    }
}
