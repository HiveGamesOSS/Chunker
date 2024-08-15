package com.hivemc.chunker.util;

import com.google.common.escape.CharEscaper;

/**
 * Escaper for escaping control characters in unicode. Useful for debugging strings that contain characters which may
 * cause malformed console output.
 */
public class ControlCodeEscaper extends CharEscaper {
    @Override
    protected char[] escape(char c) {
        // If the character is in the first block of control characters or if it's the delete character
        if (c <= '\u001f' || c == '\u007f') {
            // Format as hexadecimal with 4 digits
            return String.format("\\u%04X", (int) c).toCharArray();
        }

        // No escape needed
        return null;
    }
}
