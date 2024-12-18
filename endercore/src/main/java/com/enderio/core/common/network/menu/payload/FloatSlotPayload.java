package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class FloatSlotPayload implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, FloatSlotPayload> STREAM_CODEC = ByteBufCodecs.FLOAT
            .map(FloatSlotPayload::new, FloatSlotPayload::value)
            .cast();

    private float value;

    public FloatSlotPayload(float value) {
        this.value = value;
    }

    public float value() {
        return value;
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.FLOAT;
    }
}
