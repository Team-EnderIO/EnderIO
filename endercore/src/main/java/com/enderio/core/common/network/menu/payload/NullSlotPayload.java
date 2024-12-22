package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NullSlotPayload() implements SlotPayload {

    public static StreamCodec<RegistryFriendlyByteBuf, NullSlotPayload> STREAM_CODEC = StreamCodec.of((buf, val) -> {
    }, (buf) -> new NullSlotPayload());

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.NULL;
    }
}
