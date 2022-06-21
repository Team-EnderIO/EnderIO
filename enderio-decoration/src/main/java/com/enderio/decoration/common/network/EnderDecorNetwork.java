package com.enderio.decoration.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class EnderDecorNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("enderdecore", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
            );

   
    public static void register() {
        INSTANCE.registerMessage(0, ServerToClientLightUpdate.class, ServerToClientLightUpdate::write, ServerToClientLightUpdate::new, ServerToClientLightUpdate::handle);
    }
    
}
