package com.enderio.core.common.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

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

    public static final CodecType<String> STRING = new CodecType<>(Codec.STRING, ByteBufCodecs.STRING_UTF8.cast());
    public static final CodecType<Boolean> BOOL = new CodecType<>(Codec.BOOL, ByteBufCodecs.BOOL.cast());
    public static final CodecType<Integer> INT = new CodecType<>(Codec.INT, ByteBufCodecs.INT.cast());
    public static final CodecType<Long> LONG = new CodecType<>(Codec.LONG, ByteBufCodecs.VAR_LONG.cast());
    public static final CodecType<Float> FLOAT = new CodecType<>(Codec.FLOAT, ByteBufCodecs.FLOAT.cast());
    public static final CodecType<ResourceLocation> RESOURCE_LOCATION = new CodecType<>(ResourceLocation.CODEC,
            ResourceLocation.STREAM_CODEC.cast());
    public static final CodecType<FluidStack> FLUID_STACK = new CodecType<>(FluidStack.OPTIONAL_CODEC,
            FluidStack.OPTIONAL_STREAM_CODEC, stack -> stack.hashCode() * 31 + stack.getAmount());

    public NetworkDataSlot(Type<T> type, Supplier<T> getter, Consumer<T> setter) {

        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    @Nullable
    public Tag save(HolderLookup.Provider lookupProvider, boolean fullUpdate) {
        if (doesNeedUpdate() && !fullUpdate) {
            return null;
        }

        T value = getter.get();
        lastHash = type.hash(value);
        return type.save(lookupProvider, value);
    }

    public void parse(HolderLookup.Provider lookupProvider, Tag tag) {
        setter.accept(type.parse(lookupProvider, tag, getter));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        T value = getter.get();
        lastHash = type.hash(value);
        type.write(buf, value);
    }

    public void write(RegistryFriendlyByteBuf buf, T value) {
        type.write(buf, value);
    }

    public void read(RegistryFriendlyByteBuf buf) {
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
        Tag save(HolderLookup.Provider lookupProvider, T value);

        /***
         * @param lookupProvider Holder lookup provider.
         * @param tag The tag to parse.
         * @return The parsed value.
         */
        T parse(HolderLookup.Provider lookupProvider, Tag tag, Supplier<T> currentValueSupplier);

        /***
         * @param buf The buffer to write to.
         * @param value The value to write.
         */
        void write(RegistryFriendlyByteBuf buf, T value);

        /***
         * @param buf The buffer to read from.
         * @return The value read from the buffer.
         */
        T read(RegistryFriendlyByteBuf buf, Supplier<T> currentValueSupplier);
    }

    /**
     * A data slot that serializes using {@link Codec}'s and {@link StreamCodec}'s.
     * @param codec The codec for NBT serialization.
     * @param streamCodec The stream codec for network serialization.
     * @param hashFunction Optional custom hash function.
     * @param <T> The type to be serialized.
     */
    public record CodecType<T>(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec,
            Function<T, Integer> hashFunction) implements Type<T> {

        public CodecType(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
            this(codec, streamCodec, Object::hashCode);
        }

        public NetworkDataSlot<T> create(Supplier<T> getter, Consumer<T> setter) {
            return new NetworkDataSlot<>(this, getter, setter);
        }

        public static <T> CodecType<Set<T>> createSet(Codec<T> itemCodec,
                StreamCodec<RegistryFriendlyByteBuf, T> itemStreamCodec) {
            return new CodecType<>(itemCodec.listOf().xmap(ImmutableSet::copyOf, ImmutableList::copyOf),
                    itemStreamCodec.apply(ByteBufCodecs.list()).map(ImmutableSet::copyOf, ImmutableList::copyOf));
        }

        public static <T> CodecType<List<T>> createList(Codec<T> itemCodec,
                StreamCodec<RegistryFriendlyByteBuf, T> itemStreamCodec) {
            return new CodecType<>(itemCodec.listOf(), itemStreamCodec.apply(ByteBufCodecs.list()));
        }

        public static <T, U> CodecType<Map<T, U>> createMap(Codec<T> keyCodec, Codec<U> valueCodec,
                StreamCodec<RegistryFriendlyByteBuf, T> keyStreamCodec,
                StreamCodec<RegistryFriendlyByteBuf, U> valueStreamCodec) {
            return new CodecType<>(Codec.unboundedMap(keyCodec, valueCodec),
                    ByteBufCodecs.map(HashMap::new, keyStreamCodec, valueStreamCodec));
        }

        public int hash(T value) {
            return hashFunction.apply(value);
        }

        public Tag save(HolderLookup.Provider lookupProvider, T value) {
            return codec.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), value).getOrThrow();
        }

        public T parse(HolderLookup.Provider lookupProvider, Tag tag, Supplier<T> currentValueSupplier) {
            return codec.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
        }

        public void write(RegistryFriendlyByteBuf buf, T value) {
            streamCodec.encode(buf, value);
        }

        public T read(RegistryFriendlyByteBuf buf, Supplier<T> currentValueSupplier) {
            return streamCodec.decode(buf);
        }
    }
}
