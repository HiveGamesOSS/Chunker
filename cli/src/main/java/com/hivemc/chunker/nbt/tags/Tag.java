package com.hivemc.chunker.nbt.tags;

import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.ByteBufferInputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.*;

/**
 * Represents any Tag in the Named Binary Tag format used by Minecraft.
 *
 * @param <T> the value type which is enclosed by this tag as a boxed type.
 */
public abstract class Tag<T> {
    /**
     * Write a Bedrock edition based NBT to bytes.
     *
     * @param root the root of the tag containing "data" / "Data" depending on version.
     * @return a byte array of the encoded tag.
     * @throws IOException if it failed to write the file or compound.
     */
    public static byte[] writeBedrockNBT(CompoundTag root) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
                 DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
                // Encode
                Tag.encodeNamed(Writer.toBedrockWriter(writerStream), "", root);
            }

            // Return byte array
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Write a Bedrock edition based NBT file (likely ending with .dat).
     *
     * @param file the input file to write to.
     * @param root the root of the tag containing "data" / "Data" depending on version.
     * @throws IOException if it failed to write the file or compound.
     */
    public static void writeBedrockNBT(File file, int storageVersion, CompoundTag root) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
            Writer writer = Writer.toBedrockWriter(writerStream);

            // Encode bytes
            byte[] bytes = writeBedrockNBT(root);
            writer.writeInt(storageVersion);
            writer.writeInt(bytes.length);
            writer.writeBytes(bytes);
        }
    }

    /**
     * Write a Java edition based NBT file (likely ending with .dat).
     *
     * @param file the input file to write to.
     * @param root the root of the tag containing "data" / "Data" depending on version.
     * @throws IOException if it failed to write the file or compound.
     */
    public static void writeUncompressedJavaNBT(File file, CompoundTag root) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
            // Encode
            Tag.encodeNamed(Writer.toJavaWriter(writerStream), "", root);
        }
    }

    /**
     * Write a Java edition based GZIP NBT file (likely ending with .dat).
     *
     * @param root the root of the tag containing "data" / "Data" depending on version.
     * @return the output bytes which have been written to.
     * @throws IOException if it failed to write the file or compound.
     */
    public static byte[] writeGZipJavaNBT(CompoundTag root) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024)) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gzipOutputStream);
                 DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
                // Encode
                Tag.encodeNamed(Writer.toJavaWriter(writerStream), "", root);
            }

            // Return byte array
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Write a Java edition based GZIP NBT file (likely ending with .dat).
     *
     * @param file the input file to write to.
     * @param root the root of the tag containing "data" / "Data" depending on version.
     * @throws IOException if it failed to write the file or compound.
     */
    public static void writeGZipJavaNBT(File file, CompoundTag root) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gzipOutputStream);
             DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
            // Encode
            Tag.encodeNamed(Writer.toJavaWriter(writerStream), "", root);
            writerStream.flush();
        }
    }

    /**
     * Write a Java edition based NBT byte array.
     *
     * @param root the root of the tag containing "data" / "Data" depending on version.
     * @return the output bytes which have been written to.
     * @throws IOException if it failed to write the file or compound.
     */
    public static byte[] writeUncompressedJavaNBT(CompoundTag root) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
                 DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
                // Encode
                Tag.encodeNamed(Writer.toJavaWriter(writerStream), "", root);
            }
            // Return byte array
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Write a Java edition based ZLIB NBT byte array.
     *
     * @param root the root of the tag containing "data" / "Data" depending on version.
     * @return the output bytes which have been written to.
     * @throws IOException if it failed to write the file or compound.
     */
    public static byte[] writeZLibJavaNBT(CompoundTag root) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024)) {
            try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, new Deflater(Deflater.BEST_SPEED));
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(deflaterOutputStream);
                 DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
                // Encode
                Tag.encodeNamed(Writer.toJavaWriter(writerStream), "", root);
            }

            // Return byte array
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Write a Java edition based LZ4 NBT byte array.
     *
     * @param root the root of the tag containing "data" / "Data" depending on version.
     * @return the output bytes which have been written to.
     * @throws IOException if it failed to write the file or compound.
     */
    public static byte[] writeLZ4JavaNBT(CompoundTag root) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024)) {
            try (LZ4BlockOutputStream lz4OutputStream = new LZ4BlockOutputStream(byteArrayOutputStream);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(lz4OutputStream);
                 DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
                // Encode
                Tag.encodeNamed(Writer.toJavaWriter(writerStream), "", root);
            }

            // Return byte array
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Write a Java edition based ZLIB NBT file (likely ending with .dat).
     *
     * @param file the input file to write to.
     * @param root the root of the tag containing "data" / "Data" depending on version.
     * @throws IOException if it failed to write the file or compound.
     */
    public static void writeZLibJavaNBT(File file, CompoundTag root) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(fileOutputStream, new Deflater(Deflater.BEST_SPEED));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(deflaterOutputStream);
             DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
            // Encode
            Tag.encodeNamed(Writer.toJavaWriter(writerStream), "", root);
        }
    }

    /**
     * Encode a tag with a name to a writer.
     *
     * @param writer the writer to write bytes to.
     * @param name   the name of the tag to write.
     * @param tag    the tag which should be written, if null it will write the end tag.
     * @throws IOException an exception if it failed to write to the output.
     */
    public static void encodeNamed(Writer writer, String name, Tag<?> tag) throws IOException {
        // Write tag id
        writer.writeByte((tag == null ? TagType.END : tag.getType()).getId());

        // Write the name & the contents if it's not the end tag
        if (tag != null) {
            writer.writeString(name == null ? "" : name);
            tag.encodeValue(writer);
        }
    }

    /**
     * Encode a tag to a writer.
     *
     * @param writer the writer to write bytes to.
     * @param tag    the tag which should be written, if null it will write the end tag.
     * @throws IOException an exception if it failed to write to the output.
     */
    public static void encode(Writer writer, @Nullable Tag<?> tag) throws IOException {
        // Write tag id
        writer.writeByte((tag == null ? TagType.END : tag.getType()).getId());

        // Write the contents if it's not the end tag
        if (tag != null) {
            tag.encodeValue(writer);
        }
    }

    /**
     * Read a Bedrock edition based NBT file (likely ending with .dat), skipping past the header and reading the
     * contents.
     *
     * @param file the input file to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the file or compound.
     */
    @Nullable
    public static CompoundTag readBedrockNBT(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             DataInputStream readerStream = new DataInputStream(bufferedInputStream)) {
            // Skip header
            readerStream.skipBytes(8);

            TagWithName<CompoundTag> namedPair = Tag.decodeNamed(Reader.toBedrockReader(readerStream), CompoundTag.class);
            if (namedPair == null) return null;

            // Return the tag
            return namedPair.tag();
        }
    }

    /**
     * Read a Bedrock edition NBT bytes (without any header / size prefix).
     *
     * @param input the input bytes to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the bytes or compound.
     */
    @Nullable
    public static CompoundTag readBedrockNBT(byte[] input) throws IOException {
        try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(input);
             DataInputStream readerStream = new DataInputStream(fileInputStream)) {
            TagWithName<CompoundTag> pair = Tag.decodeNamed(Reader.toBedrockReader(readerStream), CompoundTag.class);
            if (pair == null) return null;

            // Return the tag if it wasn't null
            return pair.tag();
        }
    }

    /**
     * Read a Bedrock edition NBT bytes (without any header / size prefix).
     *
     * @param input the input bytebuffer to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the bytes or compound.
     */
    @Nullable
    public static CompoundTag readBedrockNBT(ByteBuffer input) throws IOException {
        try (ByteBufferInputStream fileInputStream = new ByteBufferInputStream(input);
             DataInputStream readerStream = new DataInputStream(fileInputStream)) {
            TagWithName<CompoundTag> pair = Tag.decodeNamed(Reader.toBedrockReader(readerStream), CompoundTag.class);
            if (pair == null) return null;

            // Return the tag if it wasn't null
            return pair.tag();
        }
    }

    /**
     * Read a Java edition based NBT bytes automatically removing the nested "data" tag if present.
     *
     * @param input the input bytes to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the file or compound.
     */
    @Nullable
    public static CompoundTag readUncompressedJavaNBT(byte[] input) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(byteArrayInputStream);
             DataInputStream readerStream = new DataInputStream(bufferedInputStream)) {
            TagWithName<CompoundTag> pair = Tag.decodeNamed(Reader.toJavaReader(readerStream), CompoundTag.class);

            // Check pair isn't null
            if (pair == null) return null;

            // Grab the value (naming isn't important here)
            CompoundTag root = pair.tag();

            // Try lowercase data
            CompoundTag data = root.getCompound("data");
            if (data != null) return data;

            // Try uppercase data
            data = root.getCompound("Data");
            if (data != null) return data;

            // Otherwise return root
            return root;
        }
    }

    /**
     * Read a Java edition based NBT file (likely ending with .dat) automatically removing the nested "data" tag if
     * present.
     *
     * @param file the input file to read from
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the file or compound.
     */
    @Nullable
    public static CompoundTag readUncompressedJavaNBT(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             DataInputStream readerStream = new DataInputStream(bufferedInputStream)) {
            TagWithName<CompoundTag> pair = Tag.decodeNamed(Reader.toJavaReader(readerStream), CompoundTag.class);

            // Check pair isn't null
            if (pair == null) return null;

            // Grab the value (naming isn't important here)
            CompoundTag root = pair.tag();

            // Try lowercase data
            CompoundTag data = root.getCompound("data");
            if (data != null) return data;

            // Try uppercase data
            data = root.getCompound("Data");
            if (data != null) return data;

            // Otherwise return root
            return root;
        }
    }

    /**
     * Read a Java edition based LZ4 NBT bytes (likely ending with .dat) automatically removing the nested "data" tag if
     * present.
     *
     * @param input the input bytes to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the file or compound.
     */
    @Nullable
    public static CompoundTag readLZ4JavaNBT(byte[] input) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
             LZ4BlockInputStream lz4InputStream = new LZ4BlockInputStream(byteArrayInputStream);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(lz4InputStream);
             DataInputStream readerStream = new DataInputStream(bufferedInputStream)) {
            TagWithName<CompoundTag> pair = Tag.decodeNamed(Reader.toJavaReader(readerStream), CompoundTag.class);

            // Check pair isn't null
            if (pair == null) return null;

            // Grab the value (naming isn't important here)
            CompoundTag root = pair.tag();

            // Try lowercase data
            CompoundTag data = root.getCompound("data");
            if (data != null) return data;

            // Try uppercase data
            data = root.getCompound("Data");
            if (data != null) return data;

            // Otherwise return root
            return root;
        }
    }

    /**
     * Read a Java edition based GZIP NBT bytes (likely ending with .dat) automatically removing the nested "data" tag if
     * present.
     *
     * @param input the input bytes to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the file or compound.
     */
    @Nullable
    public static CompoundTag readGZipJavaNBT(byte[] input) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(gzipInputStream);
             DataInputStream readerStream = new DataInputStream(bufferedInputStream)) {
            TagWithName<CompoundTag> pair = Tag.decodeNamed(Reader.toJavaReader(readerStream), CompoundTag.class);

            // Check pair isn't null
            if (pair == null) return null;

            // Grab the value (naming isn't important here)
            CompoundTag root = pair.tag();

            // Try lowercase data
            CompoundTag data = root.getCompound("data");
            if (data != null) return data;

            // Try uppercase data
            data = root.getCompound("Data");
            if (data != null) return data;

            // Otherwise return root
            return root;
        }
    }

    /**
     * Read a Java edition based GZIP NBT file (likely ending with .dat) automatically removing the nested "data" tag if
     * present.
     *
     * @param file the input file to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the file or compound.
     */
    @Nullable
    public static CompoundTag readGZipJavaNBT(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(gzipInputStream);
             DataInputStream readerStream = new DataInputStream(bufferedInputStream)) {
            TagWithName<CompoundTag> pair = Tag.decodeNamed(Reader.toJavaReader(readerStream), CompoundTag.class);

            // Check pair isn't null
            if (pair == null) return null;

            // Grab the value (naming isn't important here)
            CompoundTag root = pair.tag();

            // Try lowercase data
            CompoundTag data = root.getCompound("data");
            if (data != null) return data;

            // Try uppercase data
            data = root.getCompound("Data");
            if (data != null) return data;

            // Otherwise return root
            return root;
        }
    }

    /**
     * Read a Java edition based NBT file which may use GZip (likely ending with .dat) automatically removing the nested "data" tag if
     * present.
     *
     * @param file the input file to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the file or compound.
     */
    @Nullable
    public static CompoundTag readPossibleGZipJavaNBT(File file) throws IOException {
        // Check the first two bytes of the file
        boolean gzip = false;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] firstBytes = new byte[2];

            // If the bytes were read, check if the header matches the gzip magic
            if (fileInputStream.read(firstBytes) == firstBytes.length) {
                int header = ((firstBytes[1] & 0xFF) << 8) | (firstBytes[0] & 0xFF);
                gzip = header == GZIPInputStream.GZIP_MAGIC;
            }
        }

        // If the header is present, read it as gzipped NBT
        if (gzip) {
            return Tag.readGZipJavaNBT(file);
        } else {
            return Tag.readUncompressedJavaNBT(file);
        }
    }

    /**
     * Read a Java edition based ZLIB NBT bytes automatically removing the nested "data" tag if
     * present.
     *
     * @param input the input bytes to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the file or compound.
     */
    @Nullable
    public static CompoundTag readZLibJavaNBT(byte[] input) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
             InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inflaterInputStream);
             DataInputStream readerStream = new DataInputStream(bufferedInputStream)) {
            TagWithName<CompoundTag> pair = Tag.decodeNamed(Reader.toJavaReader(readerStream), CompoundTag.class);

            // Check pair isn't null
            if (pair == null) return null;

            // Grab the value (naming isn't important here)
            CompoundTag root = pair.tag();

            // Try lowercase data
            CompoundTag data = root.getCompound("data");
            if (data != null) return data;

            // Try uppercase data
            data = root.getCompound("Data");
            if (data != null) return data;

            // Otherwise return root
            return root;
        }
    }

    /**
     * Read a Java edition based GZIP NBT file automatically removing the nested "data" tag if
     * present.
     *
     * @param file the input file to read from.
     * @return the parsed CompoundTag or null if there isn't any data to read.
     * @throws IOException if it failed to read the file or compound.
     */
    @Nullable
    public static CompoundTag readZLibJavaNBT(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             InflaterInputStream inflaterInputStream = new InflaterInputStream(fileInputStream);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inflaterInputStream);
             DataInputStream readerStream = new DataInputStream(bufferedInputStream)) {
            TagWithName<CompoundTag> pair = Tag.decodeNamed(Reader.toJavaReader(readerStream), CompoundTag.class);

            // Check pair isn't null
            if (pair == null) return null;

            // Grab the value (naming isn't important here)
            CompoundTag root = pair.tag();

            // Try lowercase data
            CompoundTag data = root.getCompound("data");
            if (data != null) return data;

            // Try uppercase data
            data = root.getCompound("Data");
            if (data != null) return data;

            // Otherwise return root
            return root;
        }
    }

    /**
     * Decode a named tag from a reader.
     *
     * @param reader the reader which bytes should be read from.
     * @return the tag with name if it was read, null if it was an end tag.
     * @throws IOException an exception if it failed to read the type or construct the class.
     */
    @Nullable
    public static TagWithName<?> decodeNamed(Reader reader) throws IOException {
        return decodeNamed(reader, Tag.class);
    }

    /**
     * Decode a named tag from a reader.
     *
     * @param reader the reader which bytes should be read from.
     * @param clazz  the class which is desired to be read (will cause a ClassCastException if not matched).
     * @param <T>    the tag type which is to be read.
     * @return the tag with name if it was read and the right class, null if it was an end tag.
     * @throws IOException an exception if it failed to read the type or construct the class.
     */
    @Nullable
    public static <T extends Tag<?>> TagWithName<T> decodeNamed(Reader reader, Class<T> clazz) throws IOException {
        T tag = decodeTagClass(reader, clazz);
        String name = null;

        // Decode the tag
        if (tag != null) {
            name = reader.readString(CompoundTag.MAX_NAME_LENGTH);
            tag.decodeValue(reader);
        }

        // Return the tag
        return tag == null ? null : new TagWithName<>(name, tag);
    }

    /**
     * Decode an unnamed tag from a reader.
     *
     * @param reader the reader which bytes should be read from.
     * @return the tag if it was read, null if it was an end tag.
     * @throws IOException an exception if it failed to read the type or construct the class.
     */
    @Nullable
    public static Tag<?> decode(Reader reader) throws IOException {
        return decode(reader, Tag.class);
    }

    /**
     * Decode an unnamed tag from a reader.
     *
     * @param reader the reader which bytes should be read from.
     * @param clazz  the class which is desired to be read (will cause a ClassCastException if not matched).
     * @param <T>    the tag type which is to be read.
     * @return the tag if it was read and the right class, null if it was an end tag.
     * @throws IOException an exception if it failed to read the type or construct the class.
     */
    @Nullable
    public static <T extends Tag<?>> T decode(Reader reader, Class<T> clazz) throws IOException {
        T tag = decodeTagClass(reader, clazz);

        // Decode the tag
        if (tag != null) {
            tag.decodeValue(reader);
        }

        // Return the tag
        return tag;
    }

    /**
     * Decode the tag class to be used from a reader.
     *
     * @param reader the reader which bytes should be read from.
     * @return the tag if it was read, null if it was an end tag (without value read).
     * @throws IOException an exception if it failed to read the type or construct the class.
     */
    @Nullable
    public static Tag<?> decodeTagClass(Reader reader) throws IOException {
        return decodeTagClass(reader, Tag.class);
    }

    /**
     * Decode the tag class to be used from a reader.
     *
     * @param reader the reader which bytes should be read from.
     * @param clazz  the class which is desired to be read (will cause a classcastexception if not matched).
     * @param <T>    the tag type which is to be read.
     * @return the tag if it was read and the right class, null if it was an end tag (without value read).
     * @throws IOException an exception if it failed to read the type or construct the class.
     */
    @Nullable
    public static <T extends Tag<?>> T decodeTagClass(Reader reader, Class<T> clazz) throws IOException {
        // Read tag id
        int tagId = reader.readUnsignedByte();
        TagType<?, ?> tagType = TagType.getById(tagId);

        // Check for END
        if (tagType == TagType.END) return null;

        // Create the tag
        Supplier<? extends Tag<?>> constructor = tagType.getConstructor();
        Tag<?> tag = Objects.requireNonNull(constructor).get();

        // Return the tag
        return clazz.cast(tag);
    }

    /**
     * Get the tag type represented by this class.
     *
     * @return the tag type which is used for encoding & decoding.
     */
    @NotNull
    public abstract TagType<? extends Tag<T>, ? super T> getType();

    @Override
    public final int hashCode() {
        return valueHashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;

        Tag<T> tag = (Tag<T>) obj;

        // Check the values
        return valueEquals(tag);
    }

    /**
     * Calculate a hashcode from the value.
     *
     * @return the hashcode for this value, this should depend on the contents and not object references.
     */
    protected int valueHashCode() {
        return Objects.hashCode(getBoxedValue());
    }

    /**
     * Check if this tags value equals a boxed value.
     *
     * @param boxedValue the value to check against the current value of this tag.
     * @return true if the value of this tag equals the value of the input.
     */
    public boolean valueEquals(T boxedValue) {
        return Objects.equals(getBoxedValue(), boxedValue);
    }

    /**
     * Check if this tag equals another tag.
     *
     * @param tag the tag to compare this tag with.
     * @return true if the value of this tag equals the value of the other.
     */
    public abstract boolean valueEquals(Tag<T> tag);

    /**
     * Creates a copy of this tag with the same class and a cloned value, this is a deep clone so that the value can be
     * modified if needed.
     *
     * @return a copy of the current tag with values cloned.
     */
    public abstract Tag<T> clone();

    /**
     * Get the value of this tag as a boxed value, by default this is used for the default implementation of
     * {@link #valueHashCode()} and {@link #valueEquals(T)}.
     * Note: It is recommended to use `getValue()` instead if you have the specific class as no boxing will occur.
     *
     * @return the value in the boxed form (type T).
     */
    public abstract T getBoxedValue();

    /**
     * Encode the value of this type to a writer (not including the type itself).
     *
     * @param writer the writer which data should be written to.
     * @throws IOException if a fault occurs writing, e.g. buffer issues, invalid size.
     */
    public abstract void encodeValue(Writer writer) throws IOException;

    /**
     * Decode the value from a reader (not including the type).
     *
     * @param Reader the reader which data should be read from.
     * @throws IOException if a fault occurs parsing the data, e.g. mismatched length, malformed data, unexpected end.
     */
    public abstract void decodeValue(Reader Reader) throws IOException;

    /**
     * Get the current tag as the SNBT equivalent.
     *
     * @return SNBT formatted value of this tag.
     */
    public abstract String toSNBT();

    @Override
    public String toString() {
        return toSNBT();
    }
}
