package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BoolSlotPayload(boolean value) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, BoolSlotPayload> STREAM_CODEC = ByteBufCodecs.BOOL
            .map(BoolSlotPayload::new, BoolSlotPayload::value)
            .cast();

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.BOOL;
    }
}
