package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LongSlotPayload(long value) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, LongSlotPayload> STREAM_CODEC = ByteBufCodecs.VAR_LONG
            .map(LongSlotPayload::new, LongSlotPayload::value)
            .cast();

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.LONG;
    }
}
