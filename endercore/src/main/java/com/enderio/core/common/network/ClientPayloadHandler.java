package com.enderio.core.common.network;

import com.enderio.core.common.menu.BaseEnderMenu;
import com.enderio.core.common.network.menu.ClientboundSyncSlotDataPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleEmitParticle(final EmitParticlePacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> clientAddParticle(packet));
    }

    public void handleEmitParticles(final EmitParticlesPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            for (var particle : packet.particles()) {
                clientAddParticle(particle);
            }
        });
    }

    private void clientAddParticle(EmitParticlePacket packet) {
        Minecraft.getInstance().level.addParticle(packet.particleOptions(), packet.x(), packet.y(), packet.z(),
                packet.xSpeed(), packet.ySpeed(), packet.zSpeed());
    }

    public void handleSyncSlotDataPacket(ClientboundSyncSlotDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu.containerId == packet.containerId()) {
                if (context.player().containerMenu instanceof BaseEnderMenu enderMenu) {
                    for (var pair : packet.payloads()) {
                        enderMenu.clientHandleIncomingPayload(pair.index(), pair.payload());
                    }
                }
            }

        });
    }
}
