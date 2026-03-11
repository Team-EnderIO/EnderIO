package com.enderio.core.common.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The (3rd?) iteration of the data slot.
 * Each data type should have a corresponding instance of a {@link Type} (generally {@link CodecType}).
 * This type declares how each type is serialized.
 * This type is then used to create an instance of a {@link NetworkDataSlot} which performs change detection.
 * The slot does not contain the data, instead using method references to get and set the value, making it incredibly versatile.
 * @param <T> The type held by the slot.
 */
public final class NetworkDataSlot<T> {
    private final Type<T> type;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private int lastHash;

    public static final CodecType<String> STRING = new CodecType<>(Codec.STRING, FriendlyByteBuf::readUtf, FriendlyByteBuf::writeUtf);
    public static final CodecType<Boolean> BOOL = new CodecType<>(Codec.BOOL, FriendlyByteBuf::readBoolean, FriendlyByteBuf::writeBoolean);
    public static final CodecType<Integer> INT = new CodecType<>(Codec.INT, FriendlyByteBuf::readInt, FriendlyByteBuf::writeInt);
    public static final CodecType<Long> LONG = new CodecType<>(Codec.LONG, FriendlyByteBuf::readVarLong, FriendlyByteBuf::writeVarLong);
    public static final CodecType<Float> FLOAT = new CodecType<>(Codec.FLOAT, FriendlyByteBuf::readFloat, FriendlyByteBuf::writeFloat);
    public static final CodecType<ResourceLocation> RESOURCE_LOCATION = new CodecType<>(ResourceLocation.CODEC, FriendlyByteBuf::readResourceLocation,
        FriendlyByteBuf::writeResourceLocation);

    public NetworkDataSlot(Type<T> type, Supplier<T> getter, Consumer<T> setter) {

        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    @Nullable
    public Tag save(boolean fullUpdate) {
        if (doesNeedUpdate() && !fullUpdate) {
            return null;
        }

        T value = getter.get();
        lastHash = type.hash(value);
        return type.save(value);
    }

    public void parse(Tag tag) {
        setter.accept(type.parse(tag, getter));
    }

    public void write(FriendlyByteBuf buf) {
        T value = getter.get();
        lastHash = type.hash(value);
        type.write(buf, value);
    }

    public void write(FriendlyByteBuf buf, T value) {
        type.write(buf, value);
    }

    public void read(FriendlyByteBuf buf) {
        setter.accept(type.read(buf, getter));
    }

    public boolean doesNeedUpdate() {
        T value = getter.get();
        int hash = type.hash(value);
        return lastHash != hash;
    }

    // This is designed to allow custom network data slot behaviours.
    public interface Type<T> {
        /***
         * @param value The value to be hashed.
         * @return The value of the hash function, used for change detection.
         */
        // TODO: 20.6: Ensure all change trackers work correctly (i.e. all hashCodes
        // work as expected).
        int hash(T value);

        /**
         * @param lookupProvider Holder lookup provider.
         * @param value The value to save.
         * @return The value saved to NBT.
         */
        Tag save(T value);

        /***
         * @param lookupProvider Holder lookup provider.
         * @param tag The tag to parse.
         * @return The parsed value.
         */
        T parse(Tag tag, Supplier<T> currentValueSupplier);

        /***
         * @param buf The buffer to write to.
         * @param value The value to write.
         */
        void write(FriendlyByteBuf buf, T value);

        /***
         * @param buf The buffer to read from.
         * @return The value read from the buffer.
         */
        T read(FriendlyByteBuf buf, Supplier<T> currentValueSupplier);
    }

    /**
     * A data slot that serializes using {@link Codec}'s and functions that serialize with {@link FriendlyByteBuf}.
     * @param codec The codec for NBT serialization.
     * @param reader The function to read from a buffer.
     * @param writer The function to write to a buffer.
     * @param hashFunction Optional custom hash function.
     * @param <T> The type to be serialized.
     */
    public record CodecType<T>(Codec<T> codec, FriendlyByteBuf.Reader<T> reader, FriendlyByteBuf.Writer<T> writer,
            Function<T, Integer> hashFunction) implements Type<T> {

        public CodecType(Codec<T> codec, FriendlyByteBuf.Reader<T> reader, FriendlyByteBuf.Writer<T> writer) {
            this(codec, reader, writer, Object::hashCode);
        }

        public NetworkDataSlot<T> create(Supplier<T> getter, Consumer<T> setter) {
            return new NetworkDataSlot<>(this, getter, setter);
        }

        public static <T> CodecType<Set<T>> createSet(Codec<T> itemCodec, FriendlyByteBuf.Reader<T> itemReader, FriendlyByteBuf.Writer<T> itemWriter) {
            return new CodecType<>(itemCodec.listOf().xmap(ImmutableSet::copyOf, ImmutableList::copyOf),
                buf -> buf.readCollection(HashSet::new, itemReader),
                (buf, value) -> buf.writeCollection(value, itemWriter));
        }

        public static <T> CodecType<List<T>> createList(Codec<T> itemCodec, FriendlyByteBuf.Reader<T> itemReader, FriendlyByteBuf.Writer<T> itemWriter) {
            return new CodecType<>(itemCodec.listOf().xmap(ImmutableList::copyOf, ImmutableList::copyOf),
                buf -> buf.readCollection(ArrayList::new, itemReader),
                (buf, value) -> buf.writeCollection(value, itemWriter));
        }

        public static <T, U> CodecType<Map<T, U>> createMap(Codec<T> keyCodec, Codec<U> valueCodec,
            FriendlyByteBuf.Reader<T> keyReader, FriendlyByteBuf.Writer<T> keyWriter,
            FriendlyByteBuf.Reader<U> itemReader, FriendlyByteBuf.Writer<U> itemWriter) {

            return new CodecType<>(Codec.unboundedMap(keyCodec, valueCodec),
                buf -> buf.readMap(keyReader, itemReader),
                (buf, value) -> buf.writeMap(value, keyWriter, itemWriter));
        }

        public int hash(T value) {
            return hashFunction.apply(value);
        }

        public Tag save(T value) {
            return codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow(false, str -> {});
        }

        public T parse(Tag tag, Supplier<T> currentValueSupplier) {
            return codec.parse(NbtOps.INSTANCE, tag).getOrThrow(false, str -> {});
        }

        public void write(FriendlyByteBuf buf, T value) {
            writer.accept(buf, value);
        }

        public T read(FriendlyByteBuf buf, Supplier<T> currentValueSupplier) {
            return reader.apply(buf);
        }
    }
}
