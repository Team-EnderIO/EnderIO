package com.enderio.decoration.common.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * Custom setblock packet to update light
 */
public class ServerToClientLightUpdateHandler {

    public static void handlePacket(ServerToClientLightUpdate message, Supplier<Context> ctx) {
        Minecraft.getInstance().level.setBlock(message.pos, message.state, 3);
    }

}
