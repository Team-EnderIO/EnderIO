package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ResourceLocationSlotPayload(ResourceLocation value) implements SlotPayload {

    public ResourceLocationSlotPayload(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.RESOURCE_LOCATION;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(value);
    }
}
