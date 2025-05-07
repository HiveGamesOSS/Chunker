package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.resolver.itemstack.ItemStackResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
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
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.horn.ChunkerHornInstrument;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerEffectType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.stew.ChunkerStewEffect;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrim;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimMaterial;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimPattern;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import com.hivemc.chunker.resolver.property.PropertyHandler;
import com.hivemc.chunker.util.JsonTextUtil;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Resolver for converting Java component NBT to the Chunker ItemStack and resolving all the properties of the item.
 */
public class JavaComponentItemStackResolver extends ItemStackResolver<JavaResolvers, CompoundTag> {
    /**
     * Create a new java component item stack resolver.
     *
     * @param resolvers the resolvers to use.
     */
    public JavaComponentItemStackResolver(JavaResolvers resolvers) {
        super(resolvers);
    }

    @Override
    protected void registerHandlers(JavaResolvers resolvers) {
        registerHandler(ChunkerItemProperty.AMOUNT, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull CompoundTag value) {
                return Optional.of(value.getInt("count", 1));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer count) {
                value.put("count", count);
            }
        });
        registerHandler(ChunkerItemProperty.DURABILITY, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull CompoundTag value) {
                return value.getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalValue("minecraft:damage", Integer.class));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer damage) {
                value.getOrCreateCompound("components").put("minecraft:damage", damage);
            }
        });
        registerHandler(ChunkerItemProperty.DISPLAY, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerItemDisplay> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("components");
                if (tag == null) return Optional.empty();

                // Check for display tag
                List<JsonElement> lore = null;
                Color color = null;

                Tag<?> nameTag = tag.get("minecraft:custom_name");
                Tag<?> loreTag = tag.get("minecraft:lore");

                // Convert string lore to JSON
                if (loreTag instanceof ListTag<?, ?> loreTagList) {
                    lore = loreTagList.getValue().stream().map(JsonTextUtil::fromNBT).collect(Collectors.toList());
                }

                // Load color from the compound/int if it's present
                Tag<?> dyedColor = tag.get("minecraft:dyed_color");
                if (dyedColor instanceof CompoundTag compoundTag && compoundTag.contains("rgb")) {
                    color = new Color(compoundTag.getInt("rgb"));
                } else if (dyedColor instanceof IntTag intTag) {
                    // Used on 1.21.5+
                    color = new Color(intTag.getValue());
                }

                // Build the display if one of the components is present
                return nameTag != null || lore != null || color != null ? Optional.of(new ChunkerItemDisplay(
                        nameTag != null ? JsonTextUtil.fromNBT(nameTag) : null,
                        lore,
                        color
                )) : Optional.empty();
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull ChunkerItemDisplay chunkerItemDisplay) {
                CompoundTag components = value.getOrCreateCompound("components");

                // Display Name
                if (chunkerItemDisplay.displayName() != null) {
                    components.put("minecraft:custom_name", JsonTextUtil.toNBT(chunkerItemDisplay.displayName(), resolvers.dataVersion()));
                }

                // Lore
                if (chunkerItemDisplay.lore() != null) {
                    components.put(
                            "minecraft:lore",
                            JsonTextUtil.toNBT(chunkerItemDisplay.lore(), 0, resolvers.dataVersion())
                    );
                }

                // Color
                if (chunkerItemDisplay.color() != null) {
                    if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 21, 5)) {
                        // Direct color tag
                        components.put("minecraft:dyed_color", chunkerItemDisplay.color().getRGB());
                    } else {
                        // Nested in older versions
                        CompoundTag dyedColor = new CompoundTag();
                        dyedColor.put("rgb", chunkerItemDisplay.color().getRGB());
                        components.put("minecraft:dyed_color", dyedColor);
                    }
                }
            }
        });

        // General block properties
        registerHandler(ChunkerItemProperty.CAN_PLACE_ON, new PropertyHandler<>() {
            @Override
            public Optional<List<ChunkerBlockIdentifier>> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("components");
                if (tag == null) return Optional.empty();

                CompoundTag component = tag.getCompound("minecraft:can_place_on");
                if (component == null) return Optional.empty();

                // Check for the predicates
                ListTag<CompoundTag, Map<String, Tag<?>>> predicates = component.getList("predicates", CompoundTag.class, null);
                if (predicates == null) return Optional.empty();

                // Create the new list and resolve each block
                List<ChunkerBlockIdentifier> identifiers = new ArrayList<>(predicates.size());
                for (CompoundTag predicate : predicates) {
                    String block = predicate.getString("blocks");
                    if (block == null) continue;

                    // Attempt to load identifier
                    ChunkerBlockIdentifier identifier = resolvers.readBlockIdentifier(new Identifier(block)).copyWithout(VanillaBlockStates.WATERLOGGED);
                    if (!identifier.isAir()) {
                        identifiers.add(identifier);
                    }
                }
                return Optional.of(identifiers);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull List<ChunkerBlockIdentifier> chunkerBlockIdentifiers) {
                // Create the new list and resolve each block
                ListTag<CompoundTag, Map<String, Tag<?>>> predicates = new ListTag<>(TagType.COMPOUND);
                for (ChunkerBlockIdentifier chunkerBlockIdentifier : chunkerBlockIdentifiers) {
                    Optional<Identifier> identifier = resolvers.writeBlockIdentifier(chunkerBlockIdentifier, true);
                    if (identifier.isPresent() && !identifier.get().getIdentifier().equals("minecraft:air")) {
                        CompoundTag predicate = new CompoundTag();
                        predicate.put("blocks", identifier.get().getIdentifier());
                        predicates.add(predicate);
                    }
                }

                CompoundTag component = new CompoundTag();
                component.put("predicates", predicates);

                CompoundTag tag = value.getOrCreateCompound("components");
                tag.put("minecraft:can_place_on", component);
            }
        });
        registerHandler(ChunkerItemProperty.CAN_DESTROY, new PropertyHandler<>() {
            @Override
            public Optional<List<ChunkerBlockIdentifier>> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("components");
                if (tag == null) return Optional.empty();

                CompoundTag component = tag.getCompound("minecraft:can_break");
                if (component == null) return Optional.empty();

                // Check for the predicates
                ListTag<CompoundTag, Map<String, Tag<?>>> predicates = component.getList("predicates", CompoundTag.class, null);
                if (predicates == null) return Optional.empty();

                // Create the new list and resolve each block
                List<ChunkerBlockIdentifier> identifiers = new ArrayList<>(predicates.size());
                for (CompoundTag predicate : predicates) {
                    String block = predicate.getString("blocks");
                    if (block == null) continue;

                    // Attempt to load identifier
                    ChunkerBlockIdentifier identifier = resolvers.readBlockIdentifier(new Identifier(block)).copyWithout(VanillaBlockStates.WATERLOGGED);
                    if (!identifier.isAir()) {
                        identifiers.add(identifier);
                    }
                }
                return Optional.of(identifiers);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull List<ChunkerBlockIdentifier> chunkerBlockIdentifiers) {
                // Create the new list and resolve each block
                ListTag<CompoundTag, Map<String, Tag<?>>> predicates = new ListTag<>(TagType.COMPOUND);
                for (ChunkerBlockIdentifier chunkerBlockIdentifier : chunkerBlockIdentifiers) {
                    Optional<Identifier> identifier = resolvers.writeBlockIdentifier(chunkerBlockIdentifier, true);
                    if (identifier.isPresent() && !identifier.get().getIdentifier().equals("minecraft:air")) {
                        CompoundTag predicate = new CompoundTag();
                        predicate.put("blocks", identifier.get().getIdentifier());
                        predicates.add(predicate);
                    }
                }

                CompoundTag component = new CompoundTag();
                component.put("predicates", predicates);

                CompoundTag tag = value.getOrCreateCompound("components");
                tag.put("minecraft:can_break", component);
            }
        });
        registerHandler(ChunkerItemProperty.REPAIR_COST, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull CompoundTag value) {
                return value.getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalValue("minecraft:repair_cost", Integer.class));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer repairCost) {
                value.getOrCreateCompound("components").put("minecraft:repair_cost", repairCost);
            }
        });
        registerContextualHandler(ChunkerItemProperty.ENCHANTMENTS, new PropertyHandler<>() {
            @Override
            public Optional<Map<ChunkerEnchantmentType, Integer>> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Check if tag is present
                CompoundTag tag = state.value().getCompound("components");
                if (tag == null) return Optional.empty();

                // Check for the component
                String name = tag.contains("minecraft:enchantments") ? "minecraft:enchantments" : "minecraft:stored_enchantments";
                CompoundTag component = tag.getCompound(name);
                if (component == null) return Optional.empty();

                // Unwrap the levels tag (used on lower than 1.21.5)
                CompoundTag enchantmentTags = component.getCompound("levels");
                if (enchantmentTags == null) {
                    if (resolvers.dataVersion().getVersion().isLessThan(1, 21, 5)) {
                        return Optional.empty();
                    } else {
                        // In 1.21.5 the component has the levels
                        enchantmentTags = component;
                    }
                }

                // Create the new map and resolve each enchantment
                Map<ChunkerEnchantmentType, Integer> enchantments = new EnumMap<>(ChunkerEnchantmentType.class);
                for (Map.Entry<String, Tag<?>> enchantment : enchantmentTags) {
                    String id = enchantment.getKey();

                    // Resolve the enchantment
                    Optional<ChunkerEnchantmentType> enchantmentType = resolvers.enchantmentResolver().to(id);
                    if (enchantmentType.isEmpty()) {
                        resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENCHANTMENT, id);
                        continue; // Skip enchantment if it's not found
                    }

                    int level = enchantment.getValue() instanceof IntTag levelTag ? levelTag.getValue() : 1; // Assume level 1 if the value isn't present

                    // Add the enchantment
                    enchantments.put(enchantmentType.get(), level);
                }
                return Optional.of(enchantments);
            }

            @Override
            public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull Map<ChunkerEnchantmentType, Integer> value) {
                CompoundTag enchantments = new CompoundTag();
                for (Map.Entry<ChunkerEnchantmentType, Integer> enchantment : value.entrySet()) {
                    Optional<String> id = resolvers.enchantmentResolver().from(enchantment.getKey());
                    if (id.isEmpty()) {
                        resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENCHANTMENT, String.valueOf(enchantment.getKey()));
                        continue; // Don't include not supported enchantments
                    }

                    // Add the enchantment
                    enchantments.put(id.get(), enchantment.getValue());
                }

                // Finally add the tag
                String name = state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.ENCHANTED_BOOK ? "minecraft:stored_enchantments" : "minecraft:enchantments";

                // In 1.21.5 the enchantments aren't wrapped with a levels tag, older needs this
                if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 21, 5)) {
                    state.value().getOrCreateCompound("components").put(name, enchantments);
                } else {
                    state.value().getOrCreateCompound("components").getOrCreateCompound(name).put("levels", enchantments);
                }
            }
        });

        // Maps
        registerHandler(ChunkerItemProperty.MAP_INDEX, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("components");
                if (tag == null) return Optional.empty();

                // Check for map
                Optional<Integer> id = tag.getOptionalValue("minecraft:map_id", Integer.class);
                if (id.isPresent() && resolvers.converter().level().isPresent()) {
                    ChunkerLevel level = resolvers.converter().level().get();
                    return level.findMapIndexByOriginalID(id.get());
                }
                return Optional.empty();
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer index) {
                if (resolvers.converter().level().isPresent()) {
                    Optional<ChunkerMap> chunkerMap = resolvers.converter().level().get().getMapByIndex(index);
                    chunkerMap.ifPresent(map -> value.getOrCreateCompound("components").put("minecraft:map_id", (int) map.getId()));
                }
            }
        });

        // Spawn Eggs
        registerContextualHandler(ChunkerItemProperty.SPAWN_EGG_MOB, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerEntityType> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Check if tag is present
                CompoundTag tag = state.value().getCompound("components");
                if (tag == null) return Optional.empty();

                // Check for EntityTag
                CompoundTag entityTag = tag.getCompound("minecraft:entity_data");
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
                    CompoundTag entityTag = state.value().getOrCreateCompound("components")
                            .getOrCreateCompound("minecraft:entity_data");
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

        // Books
        registerContextualHandler(ChunkerItemProperty.BOOK_TITLE, new PropertyHandler<>() {
            @Override
            public Optional<String> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                String componentName = state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK ?
                        "minecraft:writable_book_content" : "minecraft:written_book_content";

                // Grab the title.raw text
                return state.value().getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptional(componentName, CompoundTag.class))
                        .flatMap(tag -> tag.getOptional("title", CompoundTag.class))
                        .flatMap(tag -> tag.getOptionalValue("raw", String.class));
            }

            @Override
            public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull String title) {
                String componentName = state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK ?
                        "minecraft:writable_book_content" : "minecraft:written_book_content";

                CompoundTag tag = state.value().getOrCreateCompound("components")
                        .getOrCreateCompound(componentName)
                        .getOrCreateCompound("title");
                tag.put("raw", title);
            }
        });
        registerContextualHandler(ChunkerItemProperty.BOOK_AUTHOR, new PropertyHandler<>() {
            @Override
            public Optional<String> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                String componentName = state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK ?
                        "minecraft:writable_book_content" : "minecraft:written_book_content";

                // Grab the author text
                return state.value().getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptional(componentName, CompoundTag.class))
                        .flatMap(tag -> tag.getOptionalValue("author", String.class));
            }

            @Override
            public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull String author) {
                String componentName = state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK ?
                        "minecraft:writable_book_content" : "minecraft:written_book_content";

                CompoundTag tag = state.value().getOrCreateCompound("components")
                        .getOrCreateCompound(componentName);
                tag.put("author", author);
            }
        });
        registerContextualHandler(ChunkerItemProperty.BOOK_PAGES, new PropertyHandler<>() {
            @Override
            public Optional<List<JsonElement>> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Check if tag is present
                CompoundTag tag = state.right().getCompound("components");
                if (tag == null) return Optional.empty();

                // Check for the component
                String componentName = state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK ?
                        "minecraft:writable_book_content" : "minecraft:written_book_content";
                CompoundTag component = tag.getCompound(componentName);
                if (component == null) return Optional.empty();

                // Check for pages
                ListTag<CompoundTag, Map<String, Tag<?>>> pages = component.getList("pages", CompoundTag.class, null);
                if (pages == null) return Optional.empty();

                // Loop through each page and extract the text
                List<JsonElement> pagesJSON = new ArrayList<>(pages.size());
                for (CompoundTag page : pages) {
                    Tag<?> rawTag = page.get("raw");

                    // If it's a writable book it's not json
                    if (state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK) {
                        if (rawTag instanceof StringTag stringTag) {
                            pagesJSON.add(JsonTextUtil.fromText(stringTag.getValue()));
                        } else {
                            pagesJSON.add(JsonTextUtil.EMPTY_TEXT_TAG);
                        }
                    } else {
                        pagesJSON.add(JsonTextUtil.fromNBT(rawTag));
                    }
                }
                return Optional.of(pagesJSON);
            }

            @Override
            public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull List<JsonElement> pagesJSON) {
                String componentName = state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK ?
                        "minecraft:writable_book_content" : "minecraft:written_book_content";
                CompoundTag tag = state.right().getOrCreateCompound("components")
                        .getOrCreateCompound(componentName);

                // Create pages
                ListTag<CompoundTag, Map<String, Tag<?>>> pages = new ListTag<>(TagType.COMPOUND, new ArrayList<>(pagesJSON.size()));
                for (JsonElement pageJSON : pagesJSON) {
                    CompoundTag page = new CompoundTag();

                    // If it's a writable book then it's not json
                    if (state.key().getIdentifier().getItemStackType() == ChunkerVanillaItemType.WRITABLE_BOOK) {
                        page.put("raw", new StringTag(JsonTextUtil.toLegacy(pageJSON, false)));
                    } else {
                        page.put("raw", JsonTextUtil.toNBT(pageJSON, resolvers.dataVersion()));
                    }
                    pages.add(page);
                }

                tag.put("pages", pages);
            }
        });

        // Goat Horn
        registerHandler(ChunkerItemProperty.HORN_INSTRUMENT, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerHornInstrument> read(@NotNull CompoundTag value) {
                return value.getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalValue("minecraft:instrument", String.class))
                        .map(resolvers::readHornInstrument);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull ChunkerHornInstrument hornInstrument) {
                CompoundTag tag = value.getOrCreateCompound("components");
                tag.put("minecraft:instrument", resolvers.writeHornInstrument(hornInstrument));
            }
        });

        // Potions
        registerHandler(ChunkerItemProperty.POTION, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerPotionType> read(@NotNull CompoundTag value) {
                return value.getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptional("minecraft:potion_contents", CompoundTag.class))
                        .flatMap(tag -> tag.getOptionalValue("potion", String.class))
                        .map(resolvers::readPotionType);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull ChunkerPotionType potionType) {
                CompoundTag tag = value.getOrCreateCompound("components").getOrCreateCompound("minecraft:potion_contents");
                tag.put("potion", resolvers.writePotionType(potionType));
            }
        });

        // Trim
        registerHandler(ChunkerItemProperty.TRIM, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerTrim> read(@NotNull CompoundTag value) {
                Optional<CompoundTag> trim = value.getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptional("minecraft:trim", CompoundTag.class));
                if (trim.isEmpty()) return Optional.empty();
                // Resolve the pattern identifier
                Optional<ChunkerTrimPattern> trimPattern = trim.get().getOptionalValue("pattern", String.class)
                        .flatMap((identifier) -> {
                            Optional<ChunkerTrimPattern> result = resolvers.trimPatternResolver().to(identifier);
                            if (result.isEmpty()) {
                                // Report missing mapping
                                resolvers.converter().logMissingMapping(Converter.MissingMappingType.TRIM_PATTERN, identifier);
                            }
                            return result;
                        });

                // Don't write if empty
                if (trimPattern.isEmpty()) return Optional.empty();

                // Resolve the material identifier
                Optional<ChunkerTrimMaterial> trimMaterial = trim.get().getOptionalValue("material", String.class)
                        .flatMap((identifier) -> {
                            Optional<ChunkerTrimMaterial> result = resolvers.trimMaterialResolver().to(identifier);
                            if (result.isEmpty()) {
                                // Report missing mapping
                                resolvers.converter().logMissingMapping(Converter.MissingMappingType.TRIM_MATERIAL, identifier);
                            }
                            return result;
                        });

                // Don't write if empty
                if (trimMaterial.isEmpty()) return Optional.empty();

                // Create the trim
                return Optional.of(new ChunkerTrim(trimMaterial.get(), trimPattern.get()));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull ChunkerTrim chunkerTrim) {
                // Resolve the pattern identifier
                Optional<String> trimPattern = resolvers.trimPatternResolver().from(chunkerTrim.getPattern());
                if (trimPattern.isEmpty()) {
                    // Report missing mapping
                    resolvers.converter().logMissingMapping(Converter.MissingMappingType.TRIM_PATTERN, String.valueOf(chunkerTrim.getPattern()));
                    return; // Don't write
                }

                // Resolve the material identifier
                Optional<String> trimMaterial = resolvers.trimMaterialResolver().from(chunkerTrim.getMaterial());
                if (trimMaterial.isEmpty()) {
                    // Report missing mapping
                    resolvers.converter().logMissingMapping(Converter.MissingMappingType.TRIM_MATERIAL, String.valueOf(chunkerTrim.getMaterial()));
                    return; // Don't write
                }

                // Write the tag
                CompoundTag tag = value.getOrCreateCompound("components").getOrCreateCompound("minecraft:trim");
                tag.put("material", trimMaterial.get());
                tag.put("pattern", trimPattern.get());
            }
        });

        // Suspicious Stew Effects
        registerHandler(ChunkerItemProperty.STEW_EFFECT, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerStewEffect> read(@NotNull CompoundTag value) {
                Optional<ListTag<CompoundTag, Map<String, Tag<?>>>> component = value.getOptional("components", CompoundTag.class)
                        .flatMap(tag -> Optional.ofNullable(tag.getList("minecraft:suspicious_stew_effects", CompoundTag.class, null)));
                if (component.isEmpty()) return Optional.empty();

                // Load each effect
                ListTag<CompoundTag, Map<String, Tag<?>>> effectTags = component.get();
                EnumMap<ChunkerEffectType, Integer> effectDurationMap = new EnumMap<>(ChunkerEffectType.class);
                for (CompoundTag effectTag : effectTags) {
                    // Resolve the effect id
                    ChunkerEffectType effectType = resolvers.readEffect(effectTag.getString("id", null));
                    if (effectType == ChunkerEffectType.EMPTY) continue; // Skip empty

                    // Read duration and add the effect
                    int duration = effectTag.getInt("duration", 160);
                    effectDurationMap.put(effectType, duration);

                }

                // Create the effect
                return Optional.of(new ChunkerStewEffect(effectDurationMap));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull ChunkerStewEffect chunkerStewEffect) {
                // Don't write empty effects
                if (chunkerStewEffect.getEffects().isEmpty())
                    return;

                ListTag<CompoundTag, Map<String, Tag<?>>> effectTags = new ListTag<>(TagType.COMPOUND);
                for (Map.Entry<ChunkerEffectType, Integer> effect : chunkerStewEffect.getEffects().entrySet()) {
                    // Resolve the effect id
                    String id = resolvers.writeEffect(effect.getKey());

                    // Write the tag
                    CompoundTag effectTag = new CompoundTag();
                    effectTag.put("duration", effect.getValue());
                    effectTag.put("id", id);
                    effectTags.add(effectTag);
                }

                // Write the list
                value.getOrCreateCompound("components").put("minecraft:suspicious_stew_effects", effectTags);
            }
        });

        // Fireworks
        registerHandler(ChunkerItemProperty.FIREWORKS, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerFireworks> read(@NotNull CompoundTag value) {
                Optional<CompoundTag> component = value.getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptional("minecraft:fireworks", CompoundTag.class));
                if (component.isEmpty()) return Optional.empty();

                // Get the duration
                byte duration = component.get().getByte("flight_duration", (byte) 1);

                // Get the explosions
                List<ChunkerFireworkExplosion> explosions = Collections.emptyList();
                ListTag<CompoundTag, Map<String, Tag<?>>> explosionsTag = component.get().getList("explosions", CompoundTag.class, null);
                if (explosionsTag != null) {
                    explosions = new ArrayList<>(explosionsTag.size());
                    for (CompoundTag explosionTag : explosionsTag) {
                        // Parse the explosion properties
                        ChunkerFireworkShape shape = explosionTag.getOptionalValue("shape", String.class)
                                .flatMap(ChunkerFireworkShape::getByName)
                                .orElse(ChunkerFireworkShape.SMALL_BALL);

                        // Parse colors
                        int[] colorsRGB = explosionTag.getIntArray("colors", null);
                        List<Color> colors = colorsRGB == null ? Collections.emptyList() : IntStream.of(colorsRGB)
                                .mapToObj(Color::new)
                                .toList();

                        // Parse fade colors
                        int[] fadeColorsRGB = explosionTag.getIntArray("fade_colors", null);
                        List<Color> fadeColors = fadeColorsRGB == null ? Collections.emptyList() : IntStream.of(fadeColorsRGB)
                                .mapToObj(Color::new)
                                .toList();

                        // Parse trail / twinkle
                        boolean trail = explosionTag.getByte("has_trail", (byte) 0) == (byte) 1;
                        boolean twinkle = explosionTag.getByte("has_twinkle", (byte) 0) == (byte) 1;

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
                CompoundTag tag = value.getOrCreateCompound("components").getOrCreateCompound("minecraft:fireworks");
                tag.put("flight_duration", chunkerFireworks.getFlightDuration());

                // Write explosions
                if (!chunkerFireworks.getExplosions().isEmpty()) {
                    ListTag<CompoundTag, Map<String, Tag<?>>> explosions = new ListTag<>(TagType.COMPOUND);
                    for (ChunkerFireworkExplosion chunkerFireworkExplosion : chunkerFireworks.getExplosions()) {
                        CompoundTag explosion = new CompoundTag();
                        explosion.put("shape", chunkerFireworkExplosion.getShape().getName());
                        explosion.put("colors", chunkerFireworkExplosion.getColors().stream()
                                .mapToInt(Color::getRGB)
                                .toArray()
                        );
                        explosion.put("fade_colors", chunkerFireworkExplosion.getFadeColors().stream()
                                .mapToInt(Color::getRGB)
                                .toArray()
                        );
                        explosion.put("has_trail", chunkerFireworkExplosion.isTrail() ? (byte) 1 : (byte) 0);
                        explosion.put("has_twinkle", chunkerFireworkExplosion.isTwinkle() ? (byte) 1 : (byte) 0);
                        explosions.add(explosion);
                    }
                    tag.put("explosions", explosions);
                }
            }
        });

        // Ominous Bottle Amplifier
        registerHandler(ChunkerItemProperty.OMINOUS_BOTTLE_AMPLIFIER, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull CompoundTag value) {
                return value.getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalValue("minecraft:ominous_bottle_amplifier", Integer.class));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer repairCost) {
                value.getOrCreateCompound("components").put("minecraft:ominous_bottle_amplifier", repairCost);
            }
        });

        // Bundle contents
        registerHandler(ChunkerItemProperty.BUNDLE_CONTENTS, new PropertyHandler<>() {
            @Override
            public Optional<List<ChunkerItemStack>> read(@NotNull CompoundTag value) {
                Optional<ListTag<CompoundTag, Map<String, Tag<?>>>> component = value.getOptional("components", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalList("minecraft:bundle_contents", CompoundTag.class));
                if (component.isEmpty()) return Optional.empty();

                // Read the items
                ListTag<CompoundTag, Map<String, Tag<?>>> items = component.get();
                List<ChunkerItemStack> bundleContents = new ArrayList<>();
                for (CompoundTag itemTag : items) {
                    // Read the tag
                    ChunkerItemStack item = resolvers.readItem(itemTag);
                    if (item.getIdentifier().isAir()) continue;

                    // Add the item to the bundle
                    bundleContents.add(item);
                }
                return Optional.of(bundleContents);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull List<ChunkerItemStack> bundleContents) {
                // Write items
                ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(bundleContents.size()));
                for (ChunkerItemStack item : bundleContents) {
                    // Don't write air to bundles
                    if (item.getIdentifier().isAir()) continue;

                    // Write the item with slot
                    Optional<CompoundTag> itemTag = resolvers.writeItem(item);
                    if (itemTag.isEmpty()) continue;

                    // Add to items
                    items.add(itemTag.get());
                }
                value.getOrCreateCompound("components").put("minecraft:bundle_contents", items);
            }
        });

        // Entities (paintings)
        registerContextualHandler(ChunkerItemProperty.ENTITY, new PropertyHandler<>() {
            @Override
            public Optional<Entity> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Check if tag is present
                CompoundTag tag = state.value().getCompound("components");
                if (tag == null) return Optional.empty();

                CompoundTag entityTag = tag.getCompound("minecraft:entity_data");
                Optional<Entity> generated = Optional.empty();
                if (entityTag != null) {
                    generated = resolvers.entityResolver().to(entityTag);
                }

                // Use the Item NBT block entity generator
                return generated;
            }

            @Override
            public void write(@NotNull Pair<ChunkerItemStack, CompoundTag> state, @NotNull Entity value) {
                // Parse the entity using our normal resolver
                Optional<CompoundTag> entityTag = resolvers.entityResolver().from(value);
                if (entityTag.isPresent()) {
                    CompoundTag tag = entityTag.get();

                    // Remove any position based data
                    tag.remove("block_pos");
                    tag.remove("TileX");
                    tag.remove("TileY");
                    tag.remove("TileZ");
                    tag.remove("Pos");
                    tag.remove("Motion");
                    tag.remove("Rotation");
                    tag.remove("Rot");
                    tag.remove("facing");
                    tag.remove("Facing");

                    // Add the tag
                    state.value().getOrCreateCompound("components").put("minecraft:entity_data", tag);
                }
            }
        });

        // Block Entities
        registerContextualHandler(ChunkerItemProperty.BLOCK_ENTITY, new PropertyHandler<>() {
            @Override
            public Optional<BlockEntity> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Check if tag is present
                CompoundTag tag = state.value().getCompound("components");
                if (tag == null) return Optional.empty();

                // Check the blockEntity class associated with the chunker block / item
                Optional<Class<? extends BlockEntity>> blockEntityClass = resolvers.blockEntityResolver().getBlockEntityClass(state.key().getIdentifier().getItemStackType());
                if (blockEntityClass.isEmpty()) return Optional.empty();

                CompoundTag blockEntityTag = tag.getCompound("minecraft:block_entity_data");
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
                        tag.remove("Rotation");
                        tag.remove("Rot");
                        tag.remove("facing");
                        tag.remove("Facing");

                        // Add the tag
                        state.value().getOrCreateCompound("components").put("minecraft:block_entity_data", tag);
                    }
                }
            }
        });
    }

    @Override
    protected Optional<ChunkerItemStack> createPropertyHolder(CompoundTag input) {
        // First turn the NBT into an identifier
        Identifier identifier = Identifier.fromData(
                input.getString("id"),
                OptionalInt.of(input.getInt("Damage", 0))
        );

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
                output.put("Damage", itemIdentifier.get().getDataValue().getAsInt());
            }
            return Optional.of(output);
        } else if (input.getIdentifier() instanceof ChunkerBlockIdentifier chunkerBlockIdentifier) {
            // Turn it into a block identifier
            Optional<Identifier> blockIdentifier = resolvers.writeBlockIdentifier(chunkerBlockIdentifier, false);
            if (blockIdentifier.isEmpty()) return Optional.empty(); // Not possible to create the stack

            // Just write the id
            output.put("id", blockIdentifier.get().getIdentifier());
            return Optional.of(output);
        } else {
            return Optional.empty(); // Unable to write item / block
        }
    }
}
