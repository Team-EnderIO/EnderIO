package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record StringSlotPayload(String value) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, StringSlotPayload> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
            .map(StringSlotPayload::new, StringSlotPayload::value)
            .cast();

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.STRING;
    }
}
