package com.enderio.core.common.network;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientPayloadHandler {
    private static ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleEmitParticle(final EmitParticlePacket packet, final PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> clientAddParticle(packet));
    }

    public void handleEmitParticles(final EmitParticlesPacket packet, final PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                for (var particle : packet.particles()) {
                    clientAddParticle(particle);
                }
            });
    }

    private void clientAddParticle(EmitParticlePacket packet) {
        Minecraft.getInstance().level.addParticle(
            packet.particleOptions(),
            packet.x(),
            packet.y(),
            packet.z(),
            packet.xSpeed(),
            packet.ySpeed(),
            packet.zSpeed());
    }

    public void handleDataSlotUpdate(S2CDataSlotUpdate update, PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                context.level().ifPresent(level -> {
                    BlockEntity be = level.getBlockEntity(update.pos());
                    if (be instanceof EnderBlockEntity enderBlockEntity) {
                        // TODO: Handle nullability
                        enderBlockEntity.clientHandleBufferSync(update.slotData());
                    }
                });
            });
    }
}
