package com.enderio.core.common.network;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.menu.BaseEnderMenu;
import com.enderio.core.common.network.menu.ServerboundSetSyncSlotDataPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleDataSlotChange(ClientboundDataSlotChange change, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            var level = context.get().getSender().level();
            BlockEntity be = level.getBlockEntity(change.pos());
            if (be instanceof EnderBlockEntity enderBlockEntity) {
                var buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(change.updateData()));
                try{
                    enderBlockEntity.serverHandleBufferChange(buf);
                }finally {
                    buf.release(); // release the buffer safely
                }
            }
        });
    }

    public void handleSetSyncSlotDataPacket(ServerboundSetSyncSlotDataPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getSender().containerMenu.containerId == packet.containerId()) {
                if (context.get().getSender().containerMenu instanceof BaseEnderMenu enderMenu) {
                    enderMenu.serverHandleIncomingPayload(packet.index(), packet.payload());
                }
            }
        });
    }
}
