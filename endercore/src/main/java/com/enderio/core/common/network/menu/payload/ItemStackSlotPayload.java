package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record ItemStackSlotPayload(ItemStack value) implements SlotPayload {

    public ItemStackSlotPayload(FriendlyByteBuf buf) {
        this(buf.readItem());
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.ITEM_STACK;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItemStack(value, false);
    }
}
