package com.enderio.base.common.network;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

/**
 * Custom setblock packet to update light
 */
public class ServerToClientLightUpdateHandler {

    public static void handlePacket(ServerToClientLightUpdate message, NetworkEvent.Context ctx) {
        Minecraft.getInstance().level.setBlock(message.pos, message.state, 3);
    }

}
