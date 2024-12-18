package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record IntSlotPayload(int value) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, IntSlotPayload> STREAM_CODEC = ByteBufCodecs.INT
            .map(IntSlotPayload::new, IntSlotPayload::value)
            .cast();

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.INT;
    }
}
