package com.hivemc.chunker.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Wrapper for ByteBuffer to provide an InputStream.
 */
public class ByteBufferInputStream extends InputStream {
    private final ByteBuffer buffer;

    public ByteBufferInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        if (buffer.hasRemaining()) {
            return buffer.get() & 0xFF; // Turn into an unsigned int
        } else {
            return -1; // No further content;
        }
    }

    @Override
    public int read(byte @NotNull [] b, int off, int len) throws IOException {
        if (!buffer.hasRemaining()) return -1;

        // Maximum number of bytes that can be read
        len = Math.min(len, buffer.remaining());

        // Read the bytes
        buffer.get(b, off, len);

        // Return the number of read bytes
        return len;
    }

    @Override
    public long skip(long n) throws IOException {
        // Can't skip 0 / negative bytes
        if (n <= 0) {
            return 0;
        }

        int skippedBytes = (int) Math.min(n, buffer.remaining());
        buffer.position(buffer.position() + skippedBytes);
        return skippedBytes;
    }

    @Override
    public int available() throws IOException {
        return buffer.remaining();
    }
}
