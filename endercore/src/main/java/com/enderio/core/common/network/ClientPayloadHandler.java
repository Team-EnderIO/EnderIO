package com.enderio.core.common.network;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.menu.BaseEnderMenu;
import com.enderio.core.common.network.menu.ClientboundSyncSlotDataPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    public void handleDataSlotUpdate(ServerboundCDataSlotUpdate update, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            BlockEntity be = level.getBlockEntity(update.pos());
            if (be instanceof EnderBlockEntity enderBlockEntity) {
                var buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(update.slotData()),
                        level.registryAccess());
                enderBlockEntity.clientHandleBufferSync(buf);
                buf.release();
            }
        });
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
