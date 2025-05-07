package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.itemstack;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.resolver.itemstack.ItemStackResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
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
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrim;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimMaterial;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimPattern;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
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
 * Resolver for converting Bedrock NBT to the Chunker ItemStack and resolving all the properties of the item.
 */
public class BedrockItemStackResolver extends ItemStackResolver<BedrockResolvers, CompoundTag> {
    /**
     * Names which need fixing for the "Name" tag based on legacy behaviour.
     */
    public static final Map<String, String> FIXED_LEGACY_NAMES = new ImmutableMap.Builder<String, String>()
            .put("minecraft:concretePowder", "minecraft:concrete_powder")
            .put("minecraft:stone_slab", "minecraft:double_stone_slab")
            .put("minecraft:stone_slab2", "minecraft:double_stone_slab2")
            .put("minecraft:stone_slab3", "minecraft:double_stone_slab3")
            .put("minecraft:stone_slab4", "minecraft:double_stone_slab4")
            .build();

    /**
     * Create a new bedrock item stack resolver.
     *
     * @param resolvers the resolvers to use.
     */
    public BedrockItemStackResolver(BedrockResolvers resolvers) {
        super(resolvers);
    }

    @Override
    protected void registerHandlers(BedrockResolvers resolvers) {
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
                        .flatMap(tag -> tag.getOptionalValue("Damage", Integer.class));
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer durability) {
                CompoundTag tag = value.getOrCreateCompound("tag");
                tag.put("Damage", durability);
            }
        });
        registerHandler(ChunkerItemProperty.DISPLAY, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerItemDisplay> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("tag");
                if (tag == null) return Optional.empty();

                // Check for the display tag
                JsonElement name = null;
                List<JsonElement> lore = null;
                Color color = null;

                CompoundTag display = tag.getCompound("display");
                if (display != null) {
                    name = display.getOptionalValue("Name", String.class).map(JsonTextUtil::fromText).orElse(null);
                    List<String> loreString = display.getListValues("Lore", StringTag.class, null);

                    // Convert string lore to JSON
                    if (loreString != null) {
                        lore = loreString.stream().map(JsonTextUtil::fromText).collect(Collectors.toList());
                    }
                }

                if (tag.contains("customColor")) {
                    color = new Color(tag.getInt("customColor"));
                }

                // Build the display if one of the components is present
                return name != null || lore != null || color != null ? Optional.of(new ChunkerItemDisplay(
                        name,
                        lore,
                        color
                )) : Optional.empty();
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull ChunkerItemDisplay chunkerItemDisplay) {
                CompoundTag tag = value.getOrCreateCompound("tag");
                CompoundTag display = tag.getOrCreateCompound("display");

                // Display Name
                if (chunkerItemDisplay.displayName() != null) {
                    display.put("Name", JsonTextUtil.toLegacy(chunkerItemDisplay.displayName(), true));
                }

                // Lore
                if (chunkerItemDisplay.lore() != null) {
                    display.put(
                            "Lore",
                            ListTag.fromValues(
                                    TagType.STRING,
                                    Lists.transform(chunkerItemDisplay.lore(), (text) -> JsonTextUtil.toLegacy(text, true))
                            )
                    );
                }

                // Color
                if (chunkerItemDisplay.color() != null) {
                    // Note: This is in the tag root
                    tag.put("customColor", chunkerItemDisplay.color().getRGB());
                }
            }
        });

        // General block properties
        registerHandler(ChunkerItemProperty.CAN_PLACE_ON, new PropertyHandler<>() {
            @Override
            public Optional<List<ChunkerBlockIdentifier>> read(@NotNull CompoundTag value) {
                List<String> names = value.getListValues("CanPlaceOn", StringTag.class, null);
                if (names == null) return Optional.empty();

                // Create the new list and resolve each block
                List<ChunkerBlockIdentifier> identifiers = new ArrayList<>(names.size());
                for (String name : names) {
                    ChunkerBlockIdentifier identifier = resolvers.readItemBlockIdentifier(new Identifier(name)).copyWithout(VanillaBlockStates.WATERLOGGED);
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
                    Optional<Identifier> identifier = resolvers.writeItemBlockIdentifier(chunkerBlockIdentifier, true);
                    if (identifier.isPresent() && !identifier.get().getIdentifier().equals("minecraft:air")) {
                        identifiers.add(identifier.get().getIdentifier());
                    }
                }

                value.put("CanPlaceOn", ListTag.fromValues(TagType.STRING, identifiers));
            }
        });
        registerHandler(ChunkerItemProperty.CAN_DESTROY, new PropertyHandler<>() {
            @Override
            public Optional<List<ChunkerBlockIdentifier>> read(@NotNull CompoundTag value) {
                List<String> names = value.getListValues("CanDestroy", StringTag.class, null);
                if (names == null) return Optional.empty();

                // Create the new list and resolve each block
                List<ChunkerBlockIdentifier> identifiers = new ArrayList<>(names.size());
                for (String name : names) {
                    ChunkerBlockIdentifier identifier = resolvers.readItemBlockIdentifier(new Identifier(name))
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
                    Optional<Identifier> identifier = resolvers.writeItemBlockIdentifier(chunkerBlockIdentifier, true);
                    if (identifier.isPresent() && !identifier.get().getIdentifier().equals("minecraft:air")) {
                        identifiers.add(identifier.get().getIdentifier());
                    }
                }

                value.put("CanDestroy", ListTag.fromValues(TagType.STRING, identifiers));
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
        registerHandler(ChunkerItemProperty.ENCHANTMENTS, new PropertyHandler<>() {

            @Override
            public Optional<Map<ChunkerEnchantmentType, Integer>> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("tag");
                if (tag == null) return Optional.empty();

                // Check for enchantments
                ListTag<CompoundTag, Map<String, Tag<?>>> enchantmentTags = tag.getList("ench", CompoundTag.class, null);
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

                    short level = enchantment.getShort("lvl", (short) 1); // Assume level 1 if the value isn't present

                    // Add the enchantment
                    enchantments.put(enchantmentType.get(), (int) level);
                }
                return Optional.of(enchantments);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Map<ChunkerEnchantmentType, Integer> enchantments) {
                ListTag<CompoundTag, Map<String, Tag<?>>> enchantmentsTag = new ListTag<>(TagType.COMPOUND, new ArrayList<>(value.size()));
                for (Map.Entry<ChunkerEnchantmentType, Integer> enchantment : enchantments.entrySet()) {
                    Optional<Integer> id = resolvers.enchantmentIDResolver().from(enchantment.getKey());
                    if (id.isEmpty()) {
                        resolvers.converter().logMissingMapping(Converter.MissingMappingType.ENCHANTMENT, String.valueOf(enchantment.getKey()));
                        continue; // Don't include not supported enchantments
                    }

                    CompoundTag enchantmentTag = new CompoundTag();
                    enchantmentTag.put("id", id.get().shortValue());
                    enchantmentTag.put("lvl", enchantment.getValue().shortValue());
                    enchantmentsTag.add(enchantmentTag);
                }

                // Finally add the tag
                value.getOrCreateCompound("tag").put("ench", enchantmentsTag);
            }
        });

        // Maps
        registerHandler(ChunkerItemProperty.MAP_INDEX, new PropertyHandler<>() {
            @Override
            public Optional<Integer> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("tag");
                if (tag == null) return Optional.empty();

                // Check if map_uuid is present
                Optional<Long> uuid = tag.getOptionalValue("map_uuid", Long.class);
                if (uuid.isPresent() && resolvers.converter().level().isPresent()) {
                    ChunkerLevel level = resolvers.converter().level().get();
                    return level.findMapIndexByOriginalID(uuid.get());
                }
                return Optional.empty();
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull Integer index) {
                if (resolvers.converter().level().isPresent()) {
                    Optional<ChunkerMap> chunkerMap = resolvers.converter().level().get().getMapByIndex(index);
                    chunkerMap.ifPresent(map -> {
                        CompoundTag tag = value.getOrCreateCompound("tag");
                        tag.put("map_uuid", map.getId());
                        tag.put("map_name_index", index);
                    });
                }
            }
        });

        // Spawn Eggs
        registerContextualHandler(ChunkerItemProperty.SPAWN_EGG_MOB, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerEntityType> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                return state.right().getOptionalValue("ItemIdentifier", String.class).flatMap((identifier) -> {
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
                    state.right().put("ItemIdentifier", type.get());
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
        registerHandler(ChunkerItemProperty.BOOK_TITLE, new PropertyHandler<>() {
            @Override
            public Optional<String> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("tag");
                if (tag == null) return Optional.empty();
                return tag.getOptionalValue("title", String.class);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull String title) {
                value.getOrCreateCompound("tag").put("title", title);
            }
        });
        registerHandler(ChunkerItemProperty.BOOK_AUTHOR, new PropertyHandler<>() {
            @Override
            public Optional<String> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("tag");
                if (tag == null) return Optional.empty();
                return tag.getOptionalValue("author", String.class);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull String author) {
                value.getOrCreateCompound("tag").put("author", author);
            }
        });
        registerHandler(ChunkerItemProperty.BOOK_PAGES, new PropertyHandler<>() {
            @Override
            public Optional<List<JsonElement>> read(@NotNull CompoundTag value) {
                // Check if tag is present
                CompoundTag tag = value.getCompound("tag");
                if (tag == null) return Optional.empty();

                // Grab the pages
                ListTag<CompoundTag, Map<String, Tag<?>>> pages = tag.getList("pages", CompoundTag.class, null);
                if (pages == null) return Optional.empty();

                // Loop through each page and extract the text
                List<JsonElement> pagesJSON = new ArrayList<>(pages.size());
                for (CompoundTag page : pages) {
                    page.getOptionalValue("text", String.class).map(JsonTextUtil::fromText).ifPresent(pagesJSON::add);
                }
                return Optional.of(pagesJSON);
            }

            @Override
            public void write(@NotNull CompoundTag value, @NotNull List<JsonElement> values) {
                ListTag<CompoundTag, Map<String, Tag<?>>> pages = new ListTag<>(TagType.COMPOUND, new ArrayList<>(values.size()));
                for (JsonElement text : values) {
                    CompoundTag page = new CompoundTag();
                    page.put("photoname", "");
                    page.put("text", JsonTextUtil.toLegacy(text, true));

                    // Add the page
                    pages.add(page);
                }

                value.getOrCreateCompound("tag").put("pages", pages);
            }
        });

        // Trim
        registerHandler(ChunkerItemProperty.TRIM, new PropertyHandler<>() {
            @Override
            public Optional<ChunkerTrim> read(@NotNull CompoundTag value) {
                Optional<CompoundTag> trim = value.getOptional("tag", CompoundTag.class)
                        .flatMap(tag -> tag.getOptional("Trim", CompoundTag.class));
                if (trim.isEmpty()) return Optional.empty();
                // Resolve the pattern identifier
                Optional<ChunkerTrimPattern> trimPattern = trim.get().getOptionalValue("Pattern", String.class)
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
                Optional<ChunkerTrimMaterial> trimMaterial = trim.get().getOptionalValue("Material", String.class)
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
                CompoundTag tag = value.getOrCreateCompound("tag").getOrCreateCompound("Trim");
                tag.put("Material", trimMaterial.get());
                tag.put("Pattern", trimPattern.get());
            }
        });

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

        // Bundle contents
        registerHandler(ChunkerItemProperty.BUNDLE_CONTENTS, new PropertyHandler<>() {
            @Override
            public Optional<List<ChunkerItemStack>> read(@NotNull CompoundTag value) {
                Optional<ListTag<CompoundTag, Map<String, Tag<?>>>> component = value.getOptional("tag", CompoundTag.class)
                        .flatMap(tag -> tag.getOptionalList("storage_item_component_content", CompoundTag.class));
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
                byte slot = 0;
                for (ChunkerItemStack item : bundleContents) {
                    // Don't write air to bundles
                    if (item.getIdentifier().isAir()) continue;

                    // Write the item with slot
                    Optional<CompoundTag> itemTag = resolvers.writeItem(item);
                    if (itemTag.isEmpty()) continue;

                    CompoundTag itemTagCompound = itemTag.get();
                    itemTagCompound.put("Slot", slot++);

                    // Add to items
                    items.add(itemTagCompound);
                }
                value.getOrCreateCompound("tag").put("storage_item_component_content", items);
            }
        });

        // Block Entities
        registerContextualHandler(ChunkerItemProperty.BLOCK_ENTITY, new PropertyHandler<>() {
            @Override
            public Optional<BlockEntity> read(@NotNull Pair<ChunkerItemStack, CompoundTag> state) {
                // Check the blockEntity class associated with the chunker block / item
                Optional<Class<? extends BlockEntity>> blockEntityClass = resolvers.blockEntityResolver().getBlockEntityClass(state.key().getIdentifier().getItemStackType());
                if (blockEntityClass.isEmpty()) return Optional.empty();

                // Attempt to get the tag (used for block entities)
                CompoundTag blockEntityTag = state.value().getCompound("tag");
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
                        tag.remove("facing");
                        tag.remove("Rotation");
                        tag.remove("Rot");
                        tag.remove("isMovable");
                        tag.remove("id");

                        // Add the tag
                        state.value().put("tag", tag);
                    }
                }
            }
        });
    }

    @Override
    protected Optional<ChunkerItemStack> createPropertyHolder(CompoundTag input) {
        // First turn the NBT into an identifier
        Identifier identifier;
        CompoundTag blockCompound = input.getCompound("Block");
        if (blockCompound != null) {
            Optional<Identifier> blockIdentifier = resolvers.nbtBlockIdentifierResolver().to(new BedrockBlockCompoundTag(blockCompound, false));
            if (blockIdentifier.isEmpty()) return Optional.empty(); // Unable to read block
            identifier = blockIdentifier.get();
        } else if (input.contains("Name")) {
            String name = input.getString("Name");
            if (name.isEmpty()) {
                name = "minecraft:air"; // In local player inventory air can be written as empty string
            }
            identifier = Identifier.fromData(name, OptionalInt.of(input.getShort("Damage", (short) 0)));
        } else {
            return Optional.empty(); // Unable to read item / block
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
            output.put("Name", itemIdentifier.get().getIdentifier());
            output.put("Damage", (short) itemIdentifier.get().getDataValue().orElse(0));
            return Optional.of(output);
        } else if (input.getIdentifier() instanceof ChunkerBlockIdentifier chunkerBlockIdentifier) {
            // Turn it into a block identifier
            Optional<Identifier> blockIdentifier = resolvers.writeItemBlockIdentifier(chunkerBlockIdentifier, false);
            if (blockIdentifier.isEmpty()) return Optional.empty(); // Cannot resolve item stack

            // Turn the block identifier -> CompoundTag
            Optional<BedrockBlockCompoundTag> blockCompoundTag = resolvers.nbtBlockIdentifierResolver().from(blockIdentifier.get());
            if (blockCompoundTag.isEmpty()) return Optional.empty(); // Unable to write block

            // Grab the block name
            String blockName = blockIdentifier.get().getIdentifier();

            // Add the details to the output tag
            output.put("Block", blockCompoundTag.get().compoundTag());

            // Fix legacy names which use a different name compared with the "Name" tag
            output.put("Name", FIXED_LEGACY_NAMES.getOrDefault(blockName, blockName));

            // Add default damage
            output.put("Damage", (short) 0);
            return Optional.of(output);
        } else {
            return Optional.empty(); // Unable to write item / block
        }
    }
}
