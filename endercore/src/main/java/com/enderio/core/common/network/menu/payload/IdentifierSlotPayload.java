package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record IdentifierSlotPayload(Identifier value) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, IdentifierSlotPayload> STREAM_CODEC = Identifier.STREAM_CODEC
            .map(IdentifierSlotPayload::new, IdentifierSlotPayload::value)
            .cast();

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.RESOURCE_LOCATION;
    }
}
