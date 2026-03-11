package com.enderio.enderio.api.conduits.connection.config;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.network.ConduitNetworkContext;
import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;

/**
 * Replacement for ConduitData that is purely focussed on sided connection context.
 * Any data for an entire node should use {@link NodeData}.
 * Any data for an entire network should use {@link ConduitNetworkContext}.
 */
@ApiStatus.Experimental
public interface ConnectionConfig {

    Codec<ConnectionConfig> GENERIC_CODEC = EnderIORegistries.CONDUIT_CONNECTION_CONFIG_TYPE.byNameCodec()
            .dispatch(ConnectionConfig::type, ConnectionConfigType::codec);

    StreamCodec<FriendlyByteBuf, ConnectionConfig> STREAM_CODEC = ByteBufCodecs
            .registry(EnderIORegistries.Keys.CONDUIT_CONNECTION_CONFIG_TYPE)
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
