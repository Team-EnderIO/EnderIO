package com.enderio.base.common.network;

import com.enderio.EnderIO;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class EIONetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(EnderIO.loc("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
            );

   
    public static void register() {
        INSTANCE.registerMessage(0, ServerToClientLightUpdate.class, ServerToClientLightUpdate::write, ServerToClientLightUpdate::new, ServerToClientLightUpdate::handle);
    }
    
}
