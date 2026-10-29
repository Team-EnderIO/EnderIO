package com.enderio.core.common.network;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.menu.BaseEnderMenu;
import com.enderio.core.common.network.menu.ServerboundSetSyncSlotDataPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("removal")
    public void handleDataSlotChange(ClientboundDataSlotChange change, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            var level = player.level();

            // The player must be within interaction range of the block
            if (!level.hasChunkAt(change.pos()) || !player.canInteractWithBlock(change.pos(), player.blockInteractionRange())) {
                return;
            }

            BlockEntity be = level.getBlockEntity(change.pos());
            if (be instanceof EnderBlockEntity enderBlockEntity) {
                var buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(change.updateData()),
                        level.registryAccess());
                try{
                    enderBlockEntity.serverHandleBufferChange(buf);
                }finally {
                    buf.release(); // release the buffer safely
                }
            }
        });
    }

    public void handleSetSyncSlotDataPacket(ServerboundSetSyncSlotDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu.containerId == packet.containerId()) {
                if (context.player().containerMenu instanceof BaseEnderMenu enderMenu
                        && enderMenu.stillValid(context.player())) {
                    enderMenu.serverHandleIncomingPayload(packet.index(), packet.payload());
                }
            }
        });
    }
}
