package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;

public record BoolSlotPayload(boolean value) implements SlotPayload {

    public BoolSlotPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.BOOL;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(value);
    }
}
