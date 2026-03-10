package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

public record FluidStackSlotPayload(FluidStack value) implements SlotPayload {

    public FluidStackSlotPayload(FriendlyByteBuf buf) {
        this(FluidStack.readFromPacket(buf));
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.FLUID_STACK;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        value.writeToPacket(buf);
    }
}
