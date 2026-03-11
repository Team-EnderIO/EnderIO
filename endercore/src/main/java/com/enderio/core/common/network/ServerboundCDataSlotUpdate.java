package com.enderio.core.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record ServerboundCDataSlotUpdate(BlockPos pos, byte[] slotData) {

    public ServerboundCDataSlotUpdate(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readByteArray());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeByteArray(slotData);
    }
}
