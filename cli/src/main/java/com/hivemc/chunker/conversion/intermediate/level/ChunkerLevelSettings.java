package com.hivemc.chunker.conversion.intermediate.level;

import com.google.common.primitives.Primitives;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.reader.LevelReader;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.intermediate.level.annotations.*;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Settings which are available in the Level.
 */
// TODO: Rewrite this to not use fields like this and have some sort of reference system for specific fields
@SuppressWarnings("CanBeFinal")
public class ChunkerLevelSettings {
    private static final Gson GSON = new Gson();

    @Bedrock
    @Category(Category.Type.WORLD_SETTINGS)
    public boolean bonusChestEnabled = false;

    @Bedrock
    @Category(Category.Type.WORLD_SETTINGS)
    public boolean bonusChestSpawned = false;

    @Bedrock
    @Category(Category.Type.MISC)
    public boolean CenterMapsToOrigin = false;

    @Bedrock
    @Java("GameRules.commandBlockOutput")
    @Category(Category.Type.GAME_RULES)
    public boolean commandblockoutput = true;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public boolean commandblocksenabled = true;

    @Bedrock
    @Java("allowCommands")
    @Category(Category.Type.GAME_RULES)
    public boolean commandsEnabled = true;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public boolean ConfirmedPlatformLockedContent = false;

    @Bedrock
    @Java(value = "Difficulty", type = byte.class)
    @Category(Category.Type.WORLD_SETTINGS)
    public int Difficulty = 1;

    @Bedrock
    @Java("GameRules.doDaylightCycle")
    @Category(Category.Type.GAME_RULES)
    public boolean dodaylightcycle = true;

    @Bedrock
    @Java("GameRules.doEntityDrops")
    @Category(Category.Type.GAME_RULES)
    public boolean doentitydrops = true;

    @Bedrock
    @Java("GameRules.doFireTick")
    @Category(Category.Type.GAME_RULES)
    public boolean dofiretick = true;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public boolean doimmediaterespawn = false;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public boolean doinsomnia = true;

    @Bedrock
    @Java("GameRules.doMobLoot")
    @Category(Category.Type.GAME_RULES)
    public boolean domobloot = true;

    @Bedrock
    @Java("GameRules.doMobSpawning")
    @Category(Category.Type.GAME_RULES)
    public boolean domobspawning = true;

    @Bedrock
    @Java("GameRules.doTileDrops")
    @Category(Category.Type.GAME_RULES)
    public boolean dotiledrops = true;

    @Bedrock
    @Java("GameRules.doWeatherCycle")
    @Category(Category.Type.GAME_RULES)
    public boolean doweathercycle = true;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public boolean drowningdamage = true;

    @Bedrock
    @Category(Category.Type.MISC)
    public boolean educationFeaturesEnabled = false;

    @Bedrock
    @Category(Category.Type.MISC)
    public byte eduLevel = 0;

    @Bedrock
    @Category(Category.Type.WORLD_SETTINGS)
    public boolean experimentalgameplay = false;

    @CustomType
    @Category(Category.Type.WORLD_SETTINGS)
    @Bedrock
    @Java
    @Hidden
    public int FlatWorldVersion = 1; // 0 indicates pre-1.18, 1 indicates post 1.18

    @CustomType
    @Category(Category.Type.WORLD_SETTINGS)
    @Bedrock
    @Java
    @Hidden
    public boolean CavesAndCliffs = false;

    @CustomType
    @Category(Category.Type.WORLD_SETTINGS)
    @Bedrock
    @Java
    @Hidden
    public boolean R20Support = false;

    @CustomType
    @Category(Category.Type.WORLD_SETTINGS)
    @Bedrock
    @Java
    @Hidden
    public boolean R21Support = false;

    @CustomType
    @Category(Category.Type.WORLD_SETTINGS)
    @Bedrock
    @Java
    @Hidden
    public boolean WinterDrop2024 = false;

    @CustomType
    @Category(Category.Type.WORLD_SETTINGS)
    @Bedrock
    @Java
    @Hidden
    public boolean SummerDrop2025 = false;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public boolean falldamage = true;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public boolean firedamage = true;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public boolean ForceGameType = false;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public int functioncommandlimit = 10000;

    @Bedrock
    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public int GameType = 0;

    /**
     * GeneratorType encapsulates: generatorName, generatorOptions and generatorVersion on Java
     */
    @Bedrock
    @Java
    @CustomType
    @Category(Category.Type.WORLD_SETTINGS)
    public ChunkerGeneratorType GeneratorType = ChunkerGeneratorType.VOID;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public boolean hasBeenLoadedInCreative = true;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public boolean hasLockedBehaviorPack = false;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public boolean hasLockedResourcePack = false;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public boolean immutableWorld = false;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public boolean isFromLockedTemplate = false;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public boolean isFromWorldTemplate = false;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public boolean isWorldTemplateOptionLocked = false;

    @Bedrock
    @Java("GameRules.keepInventory")
    @Category(Category.Type.GAME_RULES)
    public boolean keepinventory = false;

    @Bedrock
    @Category(Category.Type.MISC)
    public boolean LANBroadcast = true;

    @Bedrock
    @Category(Category.Type.MISC)
    public boolean LANBroadcastIntent = true;

    @Bedrock
    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public String LevelName = "Converted World";

    @Bedrock
    @Category(Category.Type.WEATHER)
    public float lightningLevel = 0;

    @Bedrock
    @Category(Category.Type.WEATHER)
    public int lightningTime = 96000;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public int LimitedWorldOriginX = 0;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public int LimitedWorldOriginY = 32767;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public int LimitedWorldOriginZ = 0;

    @Bedrock
    @Java("GameRules.mobGriefing")
    @Category(Category.Type.GAME_RULES)
    public boolean mobgriefing = true;

    @Bedrock
    @Category(Category.Type.MISC)
    public boolean MultiplayerGame = true;

    @Bedrock
    @Category(Category.Type.MISC)
    public boolean MultiplayerGameIntent = true;

    @Bedrock
    @Java("GameRules.naturalRegeneration")
    @Category(Category.Type.GAME_RULES)
    public boolean naturalgeneration = true;

    @Bedrock
    @Category(Category.Type.WORLD_SETTINGS)
    public int NetherScale = 8;

    @Bedrock
    @Category(Category.Type.WEATHER)
    public float rainLevel = 0;

    @Bedrock
    @Java
    @Category(Category.Type.WEATHER)
    public int rainTime = 48000;

    @Bedrock
    @Java
    @CustomType
    @Category(Category.Type.WORLD_SETTINGS)
    public String RandomSeed = "1234";

    @Bedrock
    @Java("GameRules.randomTickSpeed")
    @Category(Category.Type.GAME_RULES)
    public int randomtickspeed = 1;

    // Removed due to unsafe: requiresCopiedPackRemovalCheck

    @Bedrock
    @Java("GameRules.sendCommandFeedback")
    @Category(Category.Type.GAME_RULES)
    public boolean sendcommandfeedback = true;

    @Bedrock
    @Category(Category.Type.MISC)
    public int serverChunkTickRange = 10;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public boolean showcoordinates = false;

    @Bedrock
    @Java("GameRules.showDeathMessages")
    @Category(Category.Type.GAME_RULES)
    public boolean showdeathmessages = true;

    @Bedrock
    @Category(Category.Type.WORLD_SETTINGS)
    public boolean spawnMobs = true;

    @Bedrock
    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public int SpawnX = 0;

    @Bedrock
    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public int SpawnY = 32767;

    @Bedrock
    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public int SpawnZ = 0;

    @Bedrock
    @Category(Category.Type.MISC)
    public boolean startWithMapEnabled = false;

    @Bedrock
    @Category(Category.Type.RESTRICTIONS)
    public boolean texturePacksRequired = false;

    @Bedrock
    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public long Time = 0;

    @Bedrock
    @Category(Category.Type.GAME_RULES)
    public boolean tntexplodes = true;

    @Bedrock
    @Category(Category.Type.MISC)
    public boolean useMsaGamertagsOnly = false;

    @Bedrock
    @Category(Category.Type.MISC)
    public long worldStartCount = 0;

    @Bedrock
    @Category(Category.Type.MISC)
    public int XBLBroadcastIntent = 3;

    @Java("GameRules.announceAdvancements")
    @Category(Category.Type.GAME_RULES)
    public boolean announceAdvancements = true;

    @Java("GameRules.disableElytraMovementCheck")
    @Category(Category.Type.GAME_RULES)
    public boolean disableElytraMovementCheck = false;

    @Java("GameRules.doLimitedCrafting")
    @Category(Category.Type.GAME_RULES)
    public boolean doLimitedCrafting = false;

    @Java("GameRules.logAdminCommands")
    @Category(Category.Type.GAME_RULES)
    public boolean logAdminCommands = true;

    @Java("GameRules.reducedDebugInfo")
    @Category(Category.Type.GAME_RULES)
    public boolean reducedDebugInfo = false;

    @Java("GameRules.spectatorsGenerateChunks")
    @Category(Category.Type.GAME_RULES)
    public boolean spectatorsGenerateChunks = true;

    @Bedrock
    @Java("GameRules.spawnRadius")
    @Category(Category.Type.GAME_RULES)
    public int spawnradius = 10;

    @Java("GameRules.maxEntityCramming")
    @Category(Category.Type.GAME_RULES)
    public int maxEntityCramming = 24;

    @Java("GameRules.maxCommandChainLength")
    @Bedrock("maxcommandchainlength")
    @Category(Category.Type.GAME_RULES)
    public int maxCommandChainLength = 65535;

    @Java("GameRules.gameLoopFunction")
    @Category(Category.Type.GAME_RULES)
    public String gameLoopFunction = "-";

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public double BorderCenterX = 0;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public double BorderCenterZ = 0;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public double BorderDamagePerBlock = 0.2;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public double BorderSafeZone = 5;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public double BorderSize = 60000000;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public double BorderSizeLerpTarget = 60000000;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public long BorderSizeLerpTime = 0;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public double BorderWarningBlocks = 5;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public double BorderWarningTime = 15;

    @Java
    @Bedrock("IsHardcore")
    @Category(Category.Type.WORLD_SETTINGS)
    public boolean hardcore = false;

    @Java
    @Category(Category.Type.RESTRICTIONS)
    public boolean DifficultyLocked = false;

    @Java
    @Category(Category.Type.WEATHER)
    public boolean raining = false;

    @Java
    @Category(Category.Type.WEATHER)
    public boolean thundering = false;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public boolean MapFeatures = true;

    @Java
    @Category(Category.Type.WORLD_SETTINGS)
    public boolean initialized = true;

    @Java
    @Category(Category.Type.WEATHER)
    public int thunderTime = 96256;

    @Java
    @Category(Category.Type.WEATHER)
    public int clearWeatherTime = 0;

    @Java
    @Category(Category.Type.WEATHER)
    public long DayTime = 315;

    /**
     * Get the field type (as a String) which the UI can understand.
     *
     * @param type the Java type.
     * @return a type as a string.
     */
    protected static String getType(Class<?> type) {
        if (type == String.class) return "String";
        if (type == float.class || type == Float.class) return "Single";
        if (type == double.class || type == Double.class) return "Double";
        if (type == byte.class || type == Byte.class) return "Byte";
        if (type == short.class || type == Short.class) return "Int16";
        if (type == int.class || type == Integer.class) return "Int32";
        if (type == long.class || type == Long.class) return "Int64";
        if (type == boolean.class || type == Boolean.class) return "Boolean";
        return type.getName();
    }

    /**
     * Create a ChunkerLevelSettings from JSON.
     *
     * @param jsonObject the input JSON object.
     * @return the parsed ChunkerLevelSettings.
     */
    public static ChunkerLevelSettings fromJSON(JsonObject jsonObject) {
        return GSON.fromJson(jsonObject, ChunkerLevelSettings.class);
    }

    /**
     * Parse the level settings from NBT.
     *
     * @param root        the root level.dat tag.
     * @param levelReader the level reader to use for custom types.
     * @param converter   a copy of the world converter to report errors.
     * @return the parsed level settings.
     */
    @NotNull
    public static ChunkerLevelSettings fromNBT(@NotNull CompoundTag root, @NotNull LevelReader levelReader, @NotNull Converter converter) {
        // Create a new ChunkerLevelSettings for the output
        ChunkerLevelSettings chunkerLevelSettings = new ChunkerLevelSettings();

        Class<? extends Annotation> formatAnnotation = levelReader.getEncodingType() == EncodingType.BEDROCK ? Bedrock.class : Java.class;

        // Get properties which are public
        for (Field field : chunkerLevelSettings.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(formatAnnotation)) {
                continue; // Not valid for the levelType
            }

            // Get type + name
            String annotationValue = levelReader.getEncodingType() == EncodingType.BEDROCK ? field.getAnnotation(Bedrock.class).value() : field.getAnnotation(Java.class).value();
            String targetName = annotationValue.isEmpty() ? field.getName() : annotationValue;

            // Handle custom attributes
            if (field.isAnnotationPresent(CustomType.class)) {
                Object value = levelReader.readCustomLevelSetting(root, targetName, field.getType());

                // Only write if not null (default value otherwise)
                if (value != null) {
                    try {
                        // Update from nbt tag
                        field.set(chunkerLevelSettings, value);
                    } catch (Exception e) {
                        converter.logNonFatalException(new Exception("Could not set property " + field.getName() + " Value: " + value, e));
                    }
                }

                continue;
            }

            // Handle X.Y formatted names
            CompoundTag nbtCompound = root;
            int position;
            while ((position = targetName.indexOf(".")) != -1) {
                String tagName = targetName.substring(0, position);
                nbtCompound = nbtCompound.getCompound(tagName, null);
                if (nbtCompound == null) {
                    break; // Skip if not found
                }

                targetName = targetName.substring(position + 1);
            }

            // Ignore if compound not found
            if (nbtCompound == null) {
                continue;
            }

            try {
                // Update from nbt tag
                Tag<?> nbt = nbtCompound.get(targetName);
                if (nbt == null) continue; // Ignore if not found

                field.set(chunkerLevelSettings, convertType(nbt.getBoxedValue(), field.getType()));
            } catch (Exception e) {
                converter.logNonFatalException(new Exception("Could not set property " + field.getName() + " Tag: " + nbtCompound.get(targetName), e));
            }
        }

        return chunkerLevelSettings;
    }

    /**
     * Convert an object to a target type.
     *
     * @param value  the input value.
     * @param target the target type.
     * @return the value as the target type.
     * @throws IllegalArgumentException if it fails to convert the type.
     */
    protected static Object convertType(Object value, Class<?> target) {
        // Unwrap any primitives
        if (Primitives.isWrapperType(target)) {
            target = Primitives.unwrap(target);
        }

        // If it's an instance of a class no type conversion needed
        if (target.isInstance(value) || Primitives.unwrap(value.getClass()).equals(target)) {
            return value; // No type conversion needed
        }
        if (value instanceof Number number && target == boolean.class) {
            return number.byteValue() != (byte) 0;
        }
        if (value instanceof Number number && target == int.class) {
            return number.intValue();
        }
        if (value instanceof Number number && target == byte.class) {
            return number.byteValue();
        }
        if (value instanceof Number number && target == long.class) {
            return number.longValue();
        }
        if (value instanceof Number number && target == double.class) {
            return number.doubleValue();
        }
        if (value instanceof Number number && target == float.class) {
            return number.floatValue();
        }
        if (value instanceof Boolean bool && target == byte.class) {
            return bool ? (byte) 1 : (byte) 0;
        }
        if (value instanceof String string && target == boolean.class) {
            return Boolean.valueOf(string);
        }
        if (value instanceof String string && target == int.class) {
            if (value.equals("true")) return 1;
            if (value.equals("false")) return 0;
            return Integer.valueOf(string);
        }
        if (value instanceof String string && target == double.class) {
            return Double.valueOf(string);
        }
        if (value instanceof String string && target == byte.class) {
            return Byte.valueOf(string);
        }
        if (value instanceof String string && target == long.class) {
            return Long.valueOf(string);
        }
        if (value instanceof String string && target == float.class) {
            return Float.valueOf(string);
        }
        if (target == String.class) {
            return value.toString();
        }
        throw new IllegalArgumentException("No valid conversion method for " + value.getClass() + " to " + target);
    }

    /**
     * Convert this instance to JSON.
     *
     * @return JSON representation of these settings.
     */
    public JsonObject toJSON() {
        return GSON.toJsonTree(this).getAsJsonObject();
    }

    /**
     * Convert to a category based JSON Object with fields indicating the format and the type used.
     *
     * @return encoded JSON object.
     * @throws IllegalAccessException if it fails to access fields.
     */
    public JsonObject toDescriptiveJSON() throws IllegalAccessException {
        // Create properties and order by categories
        TreeMap<Category.Type, List<JsonObject>> properties = new TreeMap<>(Comparator.comparing(Enum::ordinal));

        // Get properties which are public
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Hidden.class)) continue; // Skip hidden

            // Fetch the tags
            Bedrock bedrock = field.getAnnotation(Bedrock.class);
            Java java = field.getAnnotation(Java.class);
            if (bedrock == null && java == null) continue; // Skip if no tag

            // Find the category
            Category category = field.getAnnotation(Category.class);
            Category.Type type;
            if (category == null) {
                type = Category.Type.UNKNOWN;
            } else {
                type = category.value();
            }

            // Get category list
            List<JsonObject> list = properties.computeIfAbsent(type, (ignored) -> new ArrayList<>());

            // Add setting
            JsonObject setting = new JsonObject();
            setting.addProperty("name", field.getName());
            setting.addProperty("bedrock", bedrock != null);
            setting.addProperty("java", java != null);
            setting.addProperty("type", getType(field.getType()));
            setting.add("value", GSON.toJsonTree(field.get(this)));
            list.add(setting);
        }

        // Return named properties
        LinkedHashMap<String, List<JsonObject>> namedProperties = new LinkedHashMap<>(properties.size());
        for (Map.Entry<Category.Type, List<JsonObject>> entry : properties.entrySet()) {
            namedProperties.put(entry.getKey().getName(), entry.getValue());
        }
        return GSON.toJsonTree(namedProperties).getAsJsonObject();
    }

    /**
     * Create an NBT output from the level settings.
     *
     * @param output      the tag to fill with the settings.
     * @param levelWriter the level writer to use for custom types.
     * @param converter   the converter to report errors to.
     */
    public void toNBT(@NotNull CompoundTag output, @NotNull LevelWriter levelWriter, @NotNull Converter converter) {
        Class<? extends Annotation> formatAnnotation = levelWriter.getEncodingType() == EncodingType.BEDROCK ? Bedrock.class : Java.class;

        // Get properties which are public
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(formatAnnotation)) {
                continue; // Not valid for the levelType
            }

            // Get name
            String annotationValue = levelWriter.getEncodingType() == EncodingType.BEDROCK ? field.getAnnotation(Bedrock.class).value() : field.getAnnotation(Java.class).value();
            String targetName = annotationValue.isEmpty() ? field.getName() : annotationValue;

            // Get type
            Class<?> targetType = levelWriter.getEncodingType() == EncodingType.BEDROCK ? field.getAnnotation(Bedrock.class).type() : field.getAnnotation(Java.class).type();
            if (targetType == Object.class) {
                targetType = field.getType(); // Use the original type if the type isn't specified
            }

            // Special case for GameRules
            if (targetName.startsWith("GameRules.")) {
                targetType = String.class;
            }

            // Grab the value

            Object value;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                converter.logNonFatalException(new Exception("Could not get property " + field.getName(), e));
                continue;
            }

            // Handle custom attributes
            if (field.isAnnotationPresent(CustomType.class)) {
                levelWriter.writeCustomLevelSetting(this, output, targetName, value);
                continue;
            }

            // Handle X.Y formatted names
            CompoundTag nbtCompound = output;
            int position;
            while ((position = targetName.indexOf(".")) != -1) {
                String tagName = targetName.substring(0, position);
                nbtCompound = nbtCompound.getOrCreateCompound(tagName);
                targetName = targetName.substring(position + 1);
            }

            // Get type for this variable
            if (targetType == boolean.class) {
                // Boolean -> Byte
                nbtCompound.put(targetName, (byte) convertType(value, byte.class));
                continue;
            }

            if (targetType == byte.class) {
                // Byte
                nbtCompound.put(targetName, (byte) convertType(value, byte.class));
                continue;
            }

            if (targetType == long.class) {
                // Long
                nbtCompound.put(targetName, (long) convertType(value, long.class));
                continue;
            }

            if (targetType == float.class) {
                // Float
                nbtCompound.put(targetName, (float) convertType(value, float.class));
                continue;
            }

            if (targetType == int.class) {
                // Int
                nbtCompound.put(targetName, (int) convertType(value, int.class));
                continue;
            }

            if (targetType == double.class) {
                // Double
                nbtCompound.put(targetName, (double) convertType(value, double.class));
                continue;
            }

            if (targetType == String.class) {
                // String
                nbtCompound.put(targetName, (String) convertType(value, String.class));
                continue;
            }

            converter.logNonFatalException(new Exception("Unable to write type " + targetName + " with type " + targetType));
        }
    }

    @Override
    public String toString() {
        return "ChunkerLevelSettings{" +
                "bonusChestEnabled=" + bonusChestEnabled +
                ", bonusChestSpawned=" + bonusChestSpawned +
                ", CenterMapsToOrigin=" + CenterMapsToOrigin +
                ", commandblockoutput=" + commandblockoutput +
                ", commandblocksenabled=" + commandblocksenabled +
                ", commandsEnabled=" + commandsEnabled +
                ", ConfirmedPlatformLockedContent=" + ConfirmedPlatformLockedContent +
                ", Difficulty=" + Difficulty +
                ", dodaylightcycle=" + dodaylightcycle +
                ", doentitydrops=" + doentitydrops +
                ", dofiretick=" + dofiretick +
                ", doimmediaterespawn=" + doimmediaterespawn +
                ", doinsomnia=" + doinsomnia +
                ", domobloot=" + domobloot +
                ", domobspawning=" + domobspawning +
                ", dotiledrops=" + dotiledrops +
                ", doweathercycle=" + doweathercycle +
                ", drowningdamage=" + drowningdamage +
                ", educationFeaturesEnabled=" + educationFeaturesEnabled +
                ", eduLevel=" + eduLevel +
                ", experimentalgameplay=" + experimentalgameplay +
                ", FlatWorldVersion=" + FlatWorldVersion +
                ", CavesAndCliffs=" + CavesAndCliffs +
                ", R20Support=" + R20Support +
                ", R21Support=" + R21Support +
                ", WinterDrop2024=" + WinterDrop2024 +
                ", falldamage=" + falldamage +
                ", firedamage=" + firedamage +
                ", ForceGameType=" + ForceGameType +
                ", functioncommandlimit=" + functioncommandlimit +
                ", GameType=" + GameType +
                ", GeneratorType=" + GeneratorType +
                ", hasBeenLoadedInCreative=" + hasBeenLoadedInCreative +
                ", hasLockedBehaviorPack=" + hasLockedBehaviorPack +
                ", hasLockedResourcePack=" + hasLockedResourcePack +
                ", immutableWorld=" + immutableWorld +
                ", isFromLockedTemplate=" + isFromLockedTemplate +
                ", isFromWorldTemplate=" + isFromWorldTemplate +
                ", isWorldTemplateOptionLocked=" + isWorldTemplateOptionLocked +
                ", keepinventory=" + keepinventory +
                ", LANBroadcast=" + LANBroadcast +
                ", LANBroadcastIntent=" + LANBroadcastIntent +
                ", LevelName='" + LevelName + '\'' +
                ", lightningLevel=" + lightningLevel +
                ", lightningTime=" + lightningTime +
                ", LimitedWorldOriginX=" + LimitedWorldOriginX +
                ", LimitedWorldOriginY=" + LimitedWorldOriginY +
                ", LimitedWorldOriginZ=" + LimitedWorldOriginZ +
                ", mobgriefing=" + mobgriefing +
                ", MultiplayerGame=" + MultiplayerGame +
                ", MultiplayerGameIntent=" + MultiplayerGameIntent +
                ", naturalgeneration=" + naturalgeneration +
                ", NetherScale=" + NetherScale +
                ", rainLevel=" + rainLevel +
                ", rainTime=" + rainTime +
                ", RandomSeed='" + RandomSeed + '\'' +
                ", randomtickspeed=" + randomtickspeed +
                ", sendcommandfeedback=" + sendcommandfeedback +
                ", serverChunkTickRange=" + serverChunkTickRange +
                ", showcoordinates=" + showcoordinates +
                ", showdeathmessages=" + showdeathmessages +
                ", spawnMobs=" + spawnMobs +
                ", SpawnX=" + SpawnX +
                ", SpawnY=" + SpawnY +
                ", SpawnZ=" + SpawnZ +
                ", startWithMapEnabled=" + startWithMapEnabled +
                ", texturePacksRequired=" + texturePacksRequired +
                ", Time=" + Time +
                ", tntexplodes=" + tntexplodes +
                ", useMsaGamertagsOnly=" + useMsaGamertagsOnly +
                ", worldStartCount=" + worldStartCount +
                ", XBLBroadcastIntent=" + XBLBroadcastIntent +
                ", announceAdvancements=" + announceAdvancements +
                ", disableElytraMovementCheck=" + disableElytraMovementCheck +
                ", doLimitedCrafting=" + doLimitedCrafting +
                ", logAdminCommands=" + logAdminCommands +
                ", reducedDebugInfo=" + reducedDebugInfo +
                ", spectatorsGenerateChunks=" + spectatorsGenerateChunks +
                ", spawnradius=" + spawnradius +
                ", maxEntityCramming=" + maxEntityCramming +
                ", maxCommandChainLength=" + maxCommandChainLength +
                ", gameLoopFunction='" + gameLoopFunction + '\'' +
                ", BorderCenterX=" + BorderCenterX +
                ", BorderCenterZ=" + BorderCenterZ +
                ", BorderDamagePerBlock=" + BorderDamagePerBlock +
                ", BorderSafeZone=" + BorderSafeZone +
                ", BorderSize=" + BorderSize +
                ", BorderSizeLerpTarget=" + BorderSizeLerpTarget +
                ", BorderSizeLerpTime=" + BorderSizeLerpTime +
                ", BorderWarningBlocks=" + BorderWarningBlocks +
                ", BorderWarningTime=" + BorderWarningTime +
                ", hardcore=" + hardcore +
                ", DifficultyLocked=" + DifficultyLocked +
                ", raining=" + raining +
                ", thundering=" + thundering +
                ", MapFeatures=" + MapFeatures +
                ", initialized=" + initialized +
                ", thunderTime=" + thunderTime +
                ", clearWeatherTime=" + clearWeatherTime +
                ", DayTime=" + DayTime +
                '}';
    }
}
