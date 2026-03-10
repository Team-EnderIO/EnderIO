package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;

public record NullSlotPayload() implements SlotPayload {

    public NullSlotPayload(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.NULL;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }
}
