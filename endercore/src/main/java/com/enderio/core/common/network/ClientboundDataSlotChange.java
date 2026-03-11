package com.enderio.core.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record ClientboundDataSlotChange(BlockPos pos, byte[] updateData) {

    public ClientboundDataSlotChange(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readByteArray());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeByteArray(updateData);
    }
}
