package com.enderio.machines.common.io;

import com.enderio.base.api.io.IOMode;
import com.enderio.core.common.network.NetworkDataSlot;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

public record IOConfig(Map<Direction, IOMode> modes) {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<IOConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.unboundedMap(Direction.CODEC, IOMode.CODEC).fieldOf("Modes").forGetter(IOConfig::modes))
            .apply(instance, IOConfig::new));

    public static final StreamCodec<ByteBuf, IOConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(c -> new EnumMap<>(Direction.class), Direction.STREAM_CODEC, IOMode.STREAM_CODEC),
            IOConfig::modes, IOConfig::new);

    public static final NetworkDataSlot.CodecType<IOConfig> DATA_SLOT_TYPE = new NetworkDataSlot.CodecType<>(CODEC,
            STREAM_CODEC.cast());

    public static IOConfig copyOf(IOConfig other) {
        return new IOConfig(new EnumMap<>(other.modes()));
    }

    public static IOConfig empty() {
        return new IOConfig(new EnumMap<>(Direction.class));
    }

    public static IOConfig of(IOMode ioMode) {
        var config = new EnumMap<Direction, IOMode>(Direction.class);
        for (Direction d : Direction.values()) {
            config.put(d, ioMode);
        }

        return new IOConfig(config);
    }

    public static IOConfig of(Function<Direction, IOMode> mode) {
        var config = new EnumMap<Direction, IOMode>(Direction.class);
        for (Direction d : Direction.values()) {
            config.put(d, mode.apply(d));
        }

        return new IOConfig(config);
    }

    public IOMode getMode(Direction side) {
        return modes.getOrDefault(side, IOMode.NONE);
    }

    public IOConfig withMode(Direction side, IOMode mode) {
        Map<Direction, IOMode> newModes = new EnumMap<>(Direction.class);
        newModes.putAll(modes);
        newModes.put(side, mode);
        return new IOConfig(newModes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IOConfig that = (IOConfig) o;
        return Objects.equals(modes, that.modes);
    }

    /**
     * Simplified hashcode impl for EnumMap using ordinal instead of hashcode for enums
     */
    @Override
    public int hashCode() {
        int h = 0;

        for (var entry : modes.entrySet()) {
            h += entry.getKey().ordinal() ^ (31 * entry.getValue().ordinal());
        }

        return h;
    }

    public Tag save(HolderLookup.Provider lookupProvider) {
        return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public static Optional<IOConfig> parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial(error -> LOGGER.error("Tried to load invalid IOConfig: '{}'", error));
    }

    public static IOConfig parseOptional(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        return tag.isEmpty() ? empty() : parse(lookupProvider, tag).orElse(empty());
    }
}
