package com.enderio.api.conduit;

import com.enderio.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public interface ConduitType<T extends Conduit<T, ?, ?>> {
    Codec<ConduitType<?>> CODEC = Codec.lazyInitialized(EnderIORegistries.CONDUIT_TYPE::byNameCodec);
    StreamCodec<RegistryFriendlyByteBuf, ConduitType<?>> STREAM_CODEC = StreamCodec.recursive(
        streamCodec -> ByteBufCodecs.registry(EnderIORegistries.Keys.CONDUIT_TYPE)
    );

    /**
     * @return The codec used for datapack read and sync.
     */
    MapCodec<T> codec();

    /**
     * @return The list of block capabilities that should be exposed and passed to the conduit proxy.
     */
    Set<BlockCapability<?, Direction>> exposedCapabilities();

    static <T extends Conduit<T, ?, ?>> ConduitType<T> of(MapCodec<T> codec) {
        return builder(codec).build();
    }

    static <T extends Conduit<T, ?, ?>> ConduitType.Builder<T> builder(MapCodec<T> codec) {
        return new ConduitType.Builder<>(codec);
    }

    static <T extends Conduit<T, ?, ?>> ConduitType<T> of(BiFunction<ResourceLocation, Component, T> factory) {
        return builder(factory).build();
    }

    static <T extends Conduit<T, ?, ?>> ConduitType.Builder<T> builder(BiFunction<ResourceLocation, Component, T> factory) {
        return new ConduitType.Builder<T>(RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Conduit::texture),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(Conduit::description)
            ).apply(builder, factory)
        ));
    }

    class Builder<T extends Conduit<T, ?, ?>> {
        private final MapCodec<T> codec;
        private final Set<BlockCapability<?, Direction>> exposedCapabilities;

        private Builder(MapCodec<T> codec) {
            this.codec = codec;
            this.exposedCapabilities = new HashSet<>();
        }

        public <U> Builder<T> exposeCapability(BlockCapability<U, Direction> capability) {
            exposedCapabilities.add(capability);
            return this;
        }

        public ConduitType<T> build() {
            return new SimpleType<>(codec, exposedCapabilities);
        }

        record SimpleType<T extends Conduit<T, ?, ?>>(
            MapCodec<T> codec,
            Set<BlockCapability<?, Direction>> exposedCapabilities
        ) implements ConduitType<T> {}
    }
}
