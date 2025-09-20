package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PairSlotPayload(SlotPayload left, SlotPayload right) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, PairSlotPayload> STREAM_CODEC = StreamCodec.composite(
            SlotPayload.STREAM_CODEC, PairSlotPayload::left, SlotPayload.STREAM_CODEC, PairSlotPayload::right,
            PairSlotPayload::new);

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.PAIR;
    }
}
