package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;

public record IntSlotPayload(int value) implements SlotPayload {

    public IntSlotPayload(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.INT;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(value);
    }
}
