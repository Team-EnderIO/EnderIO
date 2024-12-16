package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ListSlotPayload(List<SlotPayload> contents) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ListSlotPayload> STREAM_CODEC =
        SlotPayload.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ListSlotPayload::new, ListSlotPayload::contents);

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.LIST;
    }
}
