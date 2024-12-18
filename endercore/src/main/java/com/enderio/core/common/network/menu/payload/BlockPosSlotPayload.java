package com.enderio.core.common.network.menu.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record BlockPosSlotPayload(BlockPos value) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPosSlotPayload> STREAM_CODEC = BlockPos.STREAM_CODEC
            .map(BlockPosSlotPayload::new, BlockPosSlotPayload::value)
            .cast();

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.BLOCK_POS;
    }
}
