package com.enderio.core.common.network;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.menu.BaseEnderMenu;
import com.enderio.core.common.network.menu.ClientboundSyncSlotDataPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleEmitParticle(final EmitParticlePacket packet, final Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> clientAddParticle(packet));
    }

    public void handleEmitParticles(final EmitParticlesPacket packet, final Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            for (var particle : packet.particles()) {
                clientAddParticle(particle);
            }
        });
    }

    private void clientAddParticle(EmitParticlePacket packet) {
        Minecraft.getInstance().level.addParticle(packet.particleOptions(), packet.x(), packet.y(), packet.z(),
                packet.xSpeed(), packet.ySpeed(), packet.zSpeed());
    }

    public void handleDataSlotUpdate(ServerboundCDataSlotUpdate update, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            var level = context.get().getSender().level();
            BlockEntity be = level.getBlockEntity(update.pos());
            if (be instanceof EnderBlockEntity enderBlockEntity) {
                var buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(update.slotData()));
                try{
                    enderBlockEntity.clientHandleBufferSync(buf);
                } finally {
                    buf.release(); // release the buffer safely
                }
            }
        });
    }

    public void handleSyncSlotDataPacket(ClientboundSyncSlotDataPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getSender().containerMenu.containerId == packet.containerId()) {
                if (context.get().getSender().containerMenu instanceof BaseEnderMenu enderMenu) {
                    for (var pair : packet.payloads()) {
                        enderMenu.clientHandleIncomingPayload(pair.index(), pair.payload());
                    }
                }
            }

        });
    }
}
