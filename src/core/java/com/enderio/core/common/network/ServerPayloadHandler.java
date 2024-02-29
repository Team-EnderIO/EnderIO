package com.enderio.core.common.network;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ServerPayloadHandler {

    private static ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleDataSlotChange(C2SDataSlotChange change, PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                context.level().ifPresent(level -> {
                    BlockEntity be = level.getBlockEntity(change.pos());
                    if (be instanceof EnderBlockEntity enderBlockEntity) {
                        enderBlockEntity.serverHandleBufferChange(new FriendlyByteBuf(Unpooled.wrappedBuffer(change.updateData())));
                    }
                });
            });
    }}
