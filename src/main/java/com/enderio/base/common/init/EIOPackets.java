package com.enderio.base.common.init;

import com.enderio.base.common.network.AddTravelTargetPacket;
import com.enderio.base.common.network.C2SSetFluidFilterSlot;
import com.enderio.base.common.network.C2SSetItemFilterSlot;
import com.enderio.base.common.network.FilterUpdatePacket;
import com.enderio.base.common.network.RemoveTravelTargetPacket;
import com.enderio.base.common.network.RequestTravelPacket;
import com.enderio.base.common.network.SyncTravelDataPacket;
import com.enderio.base.common.network.UpdateCoordinateSelectionNameMenuPacket;
import com.enderio.core.common.network.ClientToServerMenuPacket;
import com.enderio.core.common.network.CoreNetwork;

public class EIOPackets {
    /**
     * Register base packets.
     */
    public static void register() {
        CoreNetwork.registerPacket(new ClientToServerMenuPacket.Handler<>(UpdateCoordinateSelectionNameMenuPacket::new), UpdateCoordinateSelectionNameMenuPacket.class);
        CoreNetwork.registerPacket(new SyncTravelDataPacket.Handler(), SyncTravelDataPacket.class);
        CoreNetwork.registerPacket(new AddTravelTargetPacket.Handler(), AddTravelTargetPacket.class);
        CoreNetwork.registerPacket(new RemoveTravelTargetPacket.Handler(), RemoveTravelTargetPacket.class);
        CoreNetwork.registerPacket(new RequestTravelPacket.Handler(), RequestTravelPacket.class);
        CoreNetwork.registerPacket(new FilterUpdatePacket.Handler(), FilterUpdatePacket.class);
        CoreNetwork.registerPacket(new C2SSetItemFilterSlot.Handler(), C2SSetItemFilterSlot.class);
        CoreNetwork.registerPacket(new C2SSetFluidFilterSlot.Handler(), C2SSetFluidFilterSlot.class);

    }
}
