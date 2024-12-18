package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface SlotPayload {

    StreamCodec<RegistryFriendlyByteBuf, SlotPayload> STREAM_CODEC = SlotPayloadType.STREAM_CODEC
            .<RegistryFriendlyByteBuf>cast()
            .dispatch(SlotPayload::type, SlotPayloadType::streamCodec);

    SlotPayloadType type();
}
