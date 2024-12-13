package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class IntSlotPayload implements SlotPayload{

    public static final StreamCodec<RegistryFriendlyByteBuf, IntSlotPayload> STREAM_CODEC =
        ByteBufCodecs.INT.map(IntSlotPayload::new, IntSlotPayload::value).cast();

    private int value;

    public IntSlotPayload(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.INT;
    }
}
