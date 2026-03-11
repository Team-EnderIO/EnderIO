package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import com.enderio.core.common.network.menu.ClientboundSyncSlotDataPacket;
import com.enderio.core.common.network.menu.ServerboundSetSyncSlotDataPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class CoreNetwork {
    private static int nextId = 0;

    private static final String PROTOCOL_VERSION = "1.0";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        EnderCore.loc("main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    static {
        INSTANCE.registerMessage(nextId++, EmitParticlePacket.class, EmitParticlePacket::encode, EmitParticlePacket::new,
            ClientPayloadHandler.getInstance()::handleEmitParticle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(nextId++, EmitParticlesPacket.class, EmitParticlesPacket::encode, EmitParticlesPacket::new,
            ClientPayloadHandler.getInstance()::handleEmitParticles, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        // TODO: These names are actually flipped lol
        INSTANCE.registerMessage(nextId++, ServerboundCDataSlotUpdate.class, ServerboundCDataSlotUpdate::encode, ServerboundCDataSlotUpdate::new,
            ClientPayloadHandler.getInstance()::handleDataSlotUpdate, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(nextId++, ClientboundDataSlotChange.class, ClientboundDataSlotChange::encode, ClientboundDataSlotChange::new,
            ServerPayloadHandler.getInstance()::handleDataSlotChange, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(nextId++, ClientboundSyncSlotDataPacket.class, ClientboundSyncSlotDataPacket::encode, ClientboundSyncSlotDataPacket::new,
            ClientPayloadHandler.getInstance()::handleSyncSlotDataPacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(nextId++, ServerboundSetSyncSlotDataPacket.class, ServerboundSetSyncSlotDataPacket::encode, ServerboundSetSyncSlotDataPacket::new,
            ServerPayloadHandler.getInstance()::handleSetSyncSlotDataPacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
