package com.hivemc.chunker.conversion.encoding.java.util;

import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hivemc.chunker.util.JsonTextUtil;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to get the width of a String of characters for the default Minecraft font.
 * The measurements used in this class are based on Java edition.
 */
public class FontUtil {
    private static final Int2ByteMap CODE_POINT_TO_WIDTH = new Int2ByteOpenHashMap();

    static {
        // Load the code point to width map
        try (InputStream stream = Resources.getResource("java//default_font_width_codepoints.json").openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
            JsonArray nestedCodePoints = JsonParser.parseReader(inputStreamReader).getAsJsonArray();

            // Loop through each width array and add the codepoints to our lookup
            for (byte width = 0; width < nestedCodePoints.size(); width++) {
                JsonArray codePoints = nestedCodePoints.get(width).getAsJsonArray();
                for (JsonElement element : codePoints) {
                    CODE_POINT_TO_WIDTH.put(element.getAsInt(), width);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the width of a code point.
     *
     * @param codePoint the input code point.
     * @return the width or 0 if it wasn't found.
     */
    public static int getWidth(int codePoint) {
        return CODE_POINT_TO_WIDTH.getOrDefault(codePoint, (byte) 0);
    }

    /**
     * Get the width of an input string.
     *
     * @param input the input.
     * @return the total width of all the characters.
     */
    public static int getWidth(String input) {
        int width = 0;
        for (int i = 0; i < input.length(); ) {
            // Get the code point
            int codePoint = input.codePointAt(i);

            // Add the width to the width
            width += getWidth(codePoint);

            // Move the index by the size of the codePoint
            i += Character.charCount(codePoint);
        }
        return width;
    }

    /**
     * Split a line based on a maximum width.
     *
     * @param maxWidth the maximum width of the line.
     * @param input    the input line which has been split of new line characters.
     * @return a list of lines which the input was split to.
     */
    public static List<String> splitLine(int maxWidth, String input) {
        List<String> lines = new ArrayList<>();
        int lineWidth = 0;
        int wordWidth = 0;
        boolean skipNext = false;

        StringBuilder currentLine = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();
        for (int i = 0; i < input.length(); ) {
            // Get the code point
            int codePoint = input.codePointAt(i);

            // Get the width (using 0 for chat color or if it should be skipped)
            int width = skipNext || codePoint == JsonTextUtil.CHAT_COLOR ? 0 : getWidth(codePoint);

            // Turn off skipping if it was enabled
            if (skipNext) {
                skipNext = false;
            } else if (codePoint == JsonTextUtil.CHAT_COLOR) {
                // Skip the next character
                skipNext = true;
            }

            // Check if this is a space (end of a word)
            if (Character.isSpaceChar(codePoint)) {
                // Create a new line if it'll go over the width
                int spaceWidth = getWidth(" ");
                if (lineWidth + wordWidth + spaceWidth > maxWidth) {
                    // New line
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    lineWidth = 0;
                } else if (!currentLine.isEmpty()) {
                    // Append the whitespace to the current line if it's not empty
                    currentLine.append(" ");
                    lineWidth += spaceWidth;
                }

                // Append the current word to the current line
                currentLine.append(currentWord);
                lineWidth += wordWidth;

                // Reset word
                currentWord = new StringBuilder();
                wordWidth = 0;
            } else {
                // Add to the current word
                currentWord.appendCodePoint(codePoint);

                // Add the width to the word width
                wordWidth += width;
            }

            // Move the index by the size of the codePoint
            i += Character.charCount(codePoint);
        }

        // Add the current word
        if (!currentWord.isEmpty()) {
            // Create a new line if it'll go over the width
            int whitespaceWidth = getWidth(" ");
            if (lineWidth + wordWidth + whitespaceWidth > maxWidth) {
                // New line
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            } else if (!currentLine.isEmpty()) {
                // Append the whitespace to the current line if it's not empty
                currentLine.append(" ");
            }

            // Append the current word to the current line
            currentLine.append(currentWord);
        }

        // Add the current line
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    /**
     * Split input text based on a max width and max number of lines.
     *
     * @param maxWidth the maximum width for each line.
     * @param maxLines the maximum number of lines.
     * @param original the original input text.
     * @return an array (may be the original) of split text across the lines.
     */
    public static String[] split(int maxWidth, int maxLines, String[] original) {
        // Don't split if there's already 4 lines
        if (original.length >= maxLines || original.length == 0) return original;

        List<String> newLines = new ArrayList<>();
        for (String line : original) {
            List<String> splitLines = splitLine(maxWidth, line);
            newLines.addAll(splitLines);
        }

        // Return the input (ensuring it is within the max lines threshold)
        return newLines.subList(0, Math.min(newLines.size(), maxLines)).toArray(new String[0]);
    }
}
