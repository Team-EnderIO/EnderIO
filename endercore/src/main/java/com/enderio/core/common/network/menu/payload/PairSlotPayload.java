package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;

public record PairSlotPayload(SlotPayload left, SlotPayload right) implements SlotPayload {

    public PairSlotPayload(FriendlyByteBuf buf) {
        this(SlotPayloadType.read(buf), SlotPayloadType.read(buf));
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.PAIR;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        left.write(buf);
        right.write(buf);
    }
}
