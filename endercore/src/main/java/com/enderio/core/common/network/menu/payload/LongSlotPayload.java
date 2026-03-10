package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;

public record LongSlotPayload(long value) implements SlotPayload {

    public LongSlotPayload(FriendlyByteBuf buf) {
        this(buf.readLong());
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.LONG;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeLong(value);
    }
}
