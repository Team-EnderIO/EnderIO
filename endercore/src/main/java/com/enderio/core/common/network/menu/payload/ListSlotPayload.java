package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public record ListSlotPayload(List<SlotPayload> contents) implements SlotPayload {

    public ListSlotPayload(FriendlyByteBuf buf) {
        this(buf.readList(SlotPayloadType::read));
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.LIST;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(contents, (b, payload) -> payload.write(b));
    }
}
