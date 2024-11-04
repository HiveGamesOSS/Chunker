package com.hivemc.chunker.cli;

import com.hivemc.chunker.cli.messenger.Messenger;
import com.hivemc.chunker.conversion.WorldConverter;
import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.encoding.base.Version;
import picocli.CommandLine;

import java.util.*;

/**
 * Validator which can validate EncodingType used by the CLI in the format, EDITION_VERSION, e.g.
 * JAVA_1_20_5, BEDROCK_R20_50
 * <p>
 * This uses the Messenger.findWriter method to parse the input value.
 */
public class EncodingTypeValidator implements CommandLine.ITypeConverter<String> {
    /**
     * Get a list of valid writer types.
     *
     * @return a list of valid writer IDs including internal types like PREVIEW / SETTINGS.
     */
    public static List<String> getWriterIDs() {
        List<String> writers = new ArrayList<>();
        for (EncodingType encodingType : EncodingType.getWriteableTypes()) {
            Collection<Version> versions = encodingType.getSupportedVersions();

            // If the type doesn't have any versions, write just the type
            if (versions == null || versions.isEmpty()) {
                writers.add(encodingType.getName().toUpperCase(Locale.ROOT));
            } else {
                // Write each version
                for (Version version : versions) {
                    writers.add(Messenger.toEncodedString(encodingType, version));
                }
            }
        }
        return writers;
    }

    @Override
    public String convert(String value) throws Exception {
        WorldConverter dummy = new WorldConverter(null);
        Optional<?> writer = Messenger.findWriter(value, dummy, null);
        if (writer.isEmpty()) {
            List<String> writers = getWriterIDs();
            throw new CommandLine.TypeConversionException("Invalid value '" + value + "', should be one of the following values: " + String.join(", ", writers));
        }
        return value;
    }
}
