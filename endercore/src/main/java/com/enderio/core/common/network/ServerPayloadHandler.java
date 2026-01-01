package com.enderio.core.common.network;

import com.enderio.core.common.menu.BaseEnderMenu;
import com.enderio.core.common.network.menu.ServerboundSetSyncSlotDataPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleSetSyncSlotDataPacket(ServerboundSetSyncSlotDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu.containerId == packet.containerId()) {
                if (context.player().containerMenu instanceof BaseEnderMenu enderMenu) {
                    enderMenu.serverHandleIncomingPayload(packet.index(), packet.payload());
                }
            }
        });
    }
}
