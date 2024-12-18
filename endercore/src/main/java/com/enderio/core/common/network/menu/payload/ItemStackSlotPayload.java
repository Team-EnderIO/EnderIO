package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ItemStackSlotPayload(ItemStack value) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackSlotPayload> STREAM_CODEC = ItemStack.STREAM_CODEC
            .map(ItemStackSlotPayload::new, ItemStackSlotPayload::value);

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.ITEM_STACK;
    }
}
