package com.enderio.enderio.api.conduits;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
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

public record ConduitType<T extends Conduit<T, U>, U extends ConnectionConfig>(
    MapCodec<T> codec,
    ConnectionConfigType<U> connectionConfigType,
    Set<BlockCapability<?, ?>> exposedCapabilities,
    @Nullable
    ConduitTickerBase<T> ticker,
    @Nullable
    Comparator<ConduitBlockConnection> connectionComparator,
    IConnectionComparerFromReference connectionComparerFromReference
) {
    public static Codec<ConduitType<?, ?>> CODEC = Codec.lazyInitialized(EnderIORegistries.CONDUIT_TYPE::byNameCodec);
    public static StreamCodec<RegistryFriendlyByteBuf, ConduitType<?, ?>> STREAM_CODEC = StreamCodec
        .recursive(streamCodec -> ByteBufCodecs.registry(EnderIORegistries.Keys.CONDUIT_TYPE));

    public static <T extends Conduit<T, U>, U extends ConnectionConfig> Builder<T, U> builder(MapCodec<T> codec, ConnectionConfigType<U> connectionConfigType) {
        return new Builder<T, U>(codec, connectionConfigType);
    }

    public static <T extends Conduit<T, U>, U extends ConnectionConfig> Builder<T, U> builder(
        BiFunction<ResourceLocation, Component, T> factory,
        ConnectionConfigType<U> connectionConfigType) {
        return new Builder<T, U>(RecordCodecBuilder.mapCodec(builder -> builder
            .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Conduit::texture),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(Conduit::description))
            .apply(builder, factory)), connectionConfigType);
    }

    public ConduitType(MapCodec<T> codec, ConnectionConfigType<U> connectionConfigType) {
        this(codec, connectionConfigType, Set.of(), null, null, DefaultConnectionComparerFromReference.INSTANCE);
    }

    public ConduitType(MapCodec<T> codec, ConnectionConfigType<U> connectionConfigType, ConduitTickerBase<T> ticker) {
        this(codec, connectionConfigType, Set.of(), ticker, null, DefaultConnectionComparerFromReference.INSTANCE);
    }

    public static class Builder<T extends Conduit<T, U>, U extends ConnectionConfig> {
        private final MapCodec<T> codec;
        private final ConnectionConfigType<U> connectionConfigType;
        private final Set<BlockCapability<?, ?>> exposedCapabilities;
        @Nullable
        private Comparator<ConduitBlockConnection> connectionComparator;
        private IConnectionComparerFromReference connectionComparerFromReference = DefaultConnectionComparerFromReference.INSTANCE;

        @Nullable
        private ConduitTickerBase<T> ticker;

        private Builder(MapCodec<T> codec, ConnectionConfigType<U> connectionConfigType) {
            this.codec = codec;
            this.connectionConfigType = connectionConfigType;
            this.exposedCapabilities = new HashSet<>();
        }

        public <V> Builder<T, U> exposeCapability(BlockCapability<V, ?> capability) {
            exposedCapabilities.add(capability);
            return this;
        }

        public Builder<T, U> ticker(ConduitTickerBase<T> ticker) {
            this.ticker = ticker;
            return this;
        }

        public Builder<T, U> connectionComparator(Comparator<ConduitBlockConnection> connectionComparator) {
            this.connectionComparator = connectionComparator;
            return this;
        }

        public Builder<T, U> connectionComparerFromReference(IConnectionComparerFromReference connectionComparerFromReference) {
            this.connectionComparerFromReference = connectionComparerFromReference;
            return this;
        }

        public ConduitType<T, U> build() {
            return new ConduitType<>(codec, connectionConfigType, exposedCapabilities, ticker, connectionComparator, connectionComparerFromReference);
        }
    }
}
