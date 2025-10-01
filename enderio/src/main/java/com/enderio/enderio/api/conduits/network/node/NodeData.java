package com.enderio.enderio.api.conduits.network.node;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.mojang.serialization.Codec;

/**
 * Data which is stored on each conduit node. This is not synced to the client.
 * If you want to sync data from here to the client, use {@link Conduit#getExtraWorldData(ConduitBundle, ConduitNode)}.
 * For connection-related settings, use {@link ConnectionConfig}.
 */
public interface NodeData {
    Codec<NodeData> GENERIC_CODEC = EnderIORegistries.CONDUIT_NODE_DATA_TYPE.byNameCodec()
            .dispatch(NodeData::type, NodeDataType::codec);

    NodeDataType<?> type();
}
