package com.enderio.core.common.network.menu.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record BlockPosSlotPayload(BlockPos value) implements SlotPayload {

    public BlockPosSlotPayload(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.BLOCK_POS;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(value);
    }
}
