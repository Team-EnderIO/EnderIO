package com.enderio.conduits.api.connection.config;

import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;

/**
 * Replacement for ConduitData that is purely focussed on sided connection context.
 * Any data for an entire node should use {@link com.enderio.conduits.api.network.node.NodeData}.
 * Any data for an entire network should use {@link com.enderio.conduits.api.network.ConduitNetworkContext}.
 */
@ApiStatus.Experimental
public interface ConnectionConfig {

    Codec<ConnectionConfig> GENERIC_CODEC = EnderIOConduitsRegistries.CONDUIT_CONNECTION_CONFIG_TYPE.byNameCodec()
            .dispatch(ConnectionConfig::type, ConnectionConfigType::codec);

    StreamCodec<RegistryFriendlyByteBuf, ConnectionConfig> STREAM_CODEC = ByteBufCodecs
            .registry(EnderIOConduitsRegistries.Keys.CONDUIT_CONNECTION_CONFIG_TYPE)
            .dispatch(ConnectionConfig::type, ConnectionConfigType::streamCodec);

    /**
     * @return the connection type, containing serialization information.
     */
    ConnectionConfigType<?> type();

    /**
     * @return whether the conduit should still be connected with this configuration.
     */
    boolean isConnected();

    /**
     * @return a copy of this config, but with {@link #isConnected()} returning true.
     */
    ConnectionConfig reconnected();

    /**
     * @return a copy of this config, but with {@link #isConnected()} returning false.
     */
    ConnectionConfig disconnected();
}
