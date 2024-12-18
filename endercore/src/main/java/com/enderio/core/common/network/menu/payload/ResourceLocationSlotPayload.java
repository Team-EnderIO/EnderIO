package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ResourceLocationSlotPayload(ResourceLocation value) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceLocationSlotPayload> STREAM_CODEC = ResourceLocation.STREAM_CODEC
            .map(ResourceLocationSlotPayload::new, ResourceLocationSlotPayload::value)
            .cast();

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.RESOURCE_LOCATION;
    }
}
