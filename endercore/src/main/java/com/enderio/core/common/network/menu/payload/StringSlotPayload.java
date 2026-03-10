package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;

public record StringSlotPayload(String value) implements SlotPayload {

    public StringSlotPayload(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.STRING;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(value);
    }
}
