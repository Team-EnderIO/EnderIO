package com.enderio.enderio.api.conduits;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import com.enderio.enderio.api.conduits.connection.path.DefaultConnectionPathComparator;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
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
import java.util.function.Function;

/**
 * Declares a type of Conduit.
 * @param codec the codec for reading an instance of this conduit type.
 * @param connectionConfigType the type of connection configuration for this conduit type.
 * @param exposedCapabilities the capabilities that are exposed through the bundle.
 * @param doesRequireNetworkCaches whether this conduit requires use of {@link com.enderio.enderio.api.conduits.network.ConduitNetwork}'s caching features
 * @param ticker optional ticker for this conduit type.
 * @param connectionComparator
 * @param connectionPathComparator
 * @param <T> the conduit type
 * @param <U> the connection config type
 */
public record ConduitType<T extends Conduit<T, U>, U extends ConnectionConfig>(
    MapCodec<T> codec,
    ConnectionConfigType<U> connectionConfigType,
    Set<BlockCapability<?, ?>> exposedCapabilities,
    boolean doesRequireNetworkCaches,
    @Nullable
    ConduitTickerBase<T> ticker,
    @Nullable
    Function<T, Integer> tickRateGetter,
    @Nullable
    Comparator<ConduitBlockConnection> connectionComparator,
    Comparator<ConduitConnectionPath> connectionPathComparator
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

    public ConduitType {
        Preconditions.checkArgument((ticker != null) == (tickRateGetter != null), "Must provide both ticker and tick rate getter or neither");
    }

    public ConduitType(MapCodec<T> codec, ConnectionConfigType<U> connectionConfigType) {
        this(codec, connectionConfigType, Set.of(), false, null, null, null, DefaultConnectionPathComparator.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    public <V extends Conduit<?, ?>> int getTickRate(Holder<V> conduit) {
        Preconditions.checkArgument(conduit.value().type() == this, "Conduit is not of this type");
        return getTickRate((T)conduit.value());
    }

    /**
     * Get the tick rate for the given conduit.
     * @param conduit the conduit to get the tick rate for
     * @return the tick rate
     * @throws IllegalStateException if the conduit type does not have a tick rate getter
     */
    public int getTickRate(T conduit) {
        Preconditions.checkState(tickRateGetter != null, "Conduit type does not have a tick rate getter");
        return tickRateGetter.apply(conduit);
    }

    public static class Builder<T extends Conduit<T, U>, U extends ConnectionConfig> {
        private final MapCodec<T> codec;
        private final ConnectionConfigType<U> connectionConfigType;
        private final Set<BlockCapability<?, ?>> exposedCapabilities;
        private boolean doesRequireNetworkCaches;
        @Nullable
        private ConduitTickerBase<T> ticker;
        @Nullable
        private Function<T, Integer> tickRateGetter;
        @Nullable
        private Comparator<ConduitBlockConnection> connectionComparator;
        private Comparator<ConduitConnectionPath> conduitConnectionPath = DefaultConnectionPathComparator.INSTANCE;

        private Builder(MapCodec<T> codec, ConnectionConfigType<U> connectionConfigType) {
            this.codec = codec;
            this.connectionConfigType = connectionConfigType;
            this.exposedCapabilities = new HashSet<>();
        }

        public <V> Builder<T, U> exposeCapability(BlockCapability<V, ?> capability) {
            exposedCapabilities.add(capability);
            return this;
        }

        public Builder<T, U> doesRequireNetworkCaches() {
            this.doesRequireNetworkCaches = true;
            return this;
        }

        public Builder<T, U> ticker(ConduitTickerBase<T> ticker, int tickRate) {
            return ticker(ticker, c -> tickRate);
        }

        public Builder<T, U> ticker(ConduitTickerBase<T> ticker, Function<T, Integer> tickRateGetter) {
            this.ticker = ticker;
            this.tickRateGetter = tickRateGetter;
            return this;
        }

        public Builder<T, U> connectionComparator(Comparator<ConduitBlockConnection> connectionComparator) {
            this.connectionComparator = connectionComparator;
            return this;
        }

        public Builder<T, U> connectionComparerFromReference(Comparator<ConduitConnectionPath> conduitConnectionPath) {
            this.conduitConnectionPath = conduitConnectionPath;
            return this;
        }

        public ConduitType<T, U> build() {
            return new ConduitType<>(codec, connectionConfigType, exposedCapabilities, doesRequireNetworkCaches, ticker, tickRateGetter, connectionComparator,
                conduitConnectionPath);
        }
    }
}
