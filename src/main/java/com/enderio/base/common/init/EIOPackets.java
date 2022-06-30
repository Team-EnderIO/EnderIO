package com.enderio.base.common.init;

import com.enderio.core.common.network.ClientToServerMenuPacket;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.base.common.network.UpdateCoordinateSelectionNameMenuPacket;

public class EIOPackets {
    /**
     * Register base packets.
     */
    public static void register() {
        CoreNetwork.registerPacket(new ClientToServerMenuPacket.Handler<>(UpdateCoordinateSelectionNameMenuPacket::new), UpdateCoordinateSelectionNameMenuPacket.class);
    }
}
