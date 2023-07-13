package com.enderio.conduits.common.network;

import com.enderio.core.common.network.CoreNetwork;

public class ConduitNetwork {
    public static void register() {
        CoreNetwork.registerPacket(new C2SSetConduitConnectionState.Handler(), C2SSetConduitConnectionState.class);
        CoreNetwork.registerPacket(new C2SSetConduitExtendedData.Handler(), C2SSetConduitExtendedData.class);
    }

}
