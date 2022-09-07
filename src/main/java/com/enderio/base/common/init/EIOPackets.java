package com.enderio.base.common.init;

import com.enderio.base.common.network.UpdateCoordinateSelectionNameMenuPacket;
import com.enderio.core.common.network.ClientToServerMenuPacket;
import com.enderio.core.common.network.CoreNetwork;

public class EIOPackets {
    /**
     * Register base packets.
     */
    public static void register() {
        CoreNetwork.registerPacket(new ClientToServerMenuPacket.Handler<>(UpdateCoordinateSelectionNameMenuPacket::new), UpdateCoordinateSelectionNameMenuPacket.class);
    }
}
