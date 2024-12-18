package com.enderio.core.common.network.menu.payload;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ListSlotPayload(List<SlotPayload> contents) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ListSlotPayload> STREAM_CODEC = SlotPayload.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(ListSlotPayload::new, ListSlotPayload::contents);

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.LIST;
    }
}
