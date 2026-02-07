package com.enderio.enderio.api.conduits;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.network.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.DefaultConnectionComparerFromReference;
import com.enderio.enderio.api.conduits.network.IConnectionComparerFromReference;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public record ConduitType<T extends Conduit<T, ?>>(
    MapCodec<T> codec,
    Set<BlockCapability<?, ?>> exposedCapabilities,
    @Nullable
    ConduitTickerBase<T> ticker,
    @Nullable
    Comparator<ConduitBlockConnection> connectionComparator,
    IConnectionComparerFromReference connectionComparerFromReference
) {
    public static Codec<ConduitType<?>> CODEC = Codec.lazyInitialized(EnderIORegistries.CONDUIT_TYPE::byNameCodec);
    public static StreamCodec<RegistryFriendlyByteBuf, ConduitType<?>> STREAM_CODEC = StreamCodec
        .recursive(streamCodec -> ByteBufCodecs.registry(EnderIORegistries.Keys.CONDUIT_TYPE));

    public static <T extends Conduit<T, ?>> ConduitType<T> of(MapCodec<T> codec) {
        return builder(codec).build();
    }

    public static <T extends Conduit<T, ?>> ConduitType<T> of(MapCodec<T> codec, ConduitTickerBase<T> ticker) {
        return builder(codec).ticker(ticker).build();
    }

    public static <T extends Conduit<T, ?>> ConduitType<T> of(BiFunction<ResourceLocation, Component, T> factory) {
        return builder(factory).build();
    }

    public static <T extends Conduit<T, ?>> Builder<T> builder(MapCodec<T> codec) {
        return new Builder<>(codec);
    }

    public static <T extends Conduit<T, ?>> Builder<T> builder(BiFunction<ResourceLocation, Component, T> factory) {
        return new Builder<T>(RecordCodecBuilder.mapCodec(builder -> builder
            .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Conduit::texture),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(Conduit::description))
            .apply(builder, factory)));
    }

    public static class Builder<T extends Conduit<T, ?>> {
        private final MapCodec<T> codec;
        private final Set<BlockCapability<?, ?>> exposedCapabilities;
        @Nullable
        private Comparator<ConduitBlockConnection> connectionComparator;
        private IConnectionComparerFromReference connectionComparerFromReference = DefaultConnectionComparerFromReference.INSTANCE;

        @Nullable
        private ConduitTickerBase<T> ticker;

        private Builder(MapCodec<T> codec) {
            this.codec = codec;
            this.exposedCapabilities = new HashSet<>();
        }

        public <U> Builder<T> exposeCapability(BlockCapability<U, ?> capability) {
            exposedCapabilities.add(capability);
            return this;
        }

        public Builder<T> ticker(ConduitTickerBase<T> ticker) {
            this.ticker = ticker;
            return this;
        }

        public Builder<T> connectionComparator(Comparator<ConduitBlockConnection> connectionComparator) {
            this.connectionComparator = connectionComparator;
            return this;
        }

        public Builder<T> connectionComparerFromReference(IConnectionComparerFromReference connectionComparerFromReference) {
            this.connectionComparerFromReference = connectionComparerFromReference;
            return this;
        }

        public ConduitType<T> build() {
            return new ConduitType<>(codec, exposedCapabilities, ticker, connectionComparator, connectionComparerFromReference);
        }
    }
}
