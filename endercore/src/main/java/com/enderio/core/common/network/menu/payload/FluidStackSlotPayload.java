package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidStackSlotPayload(FluidStack value) implements SlotPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStackSlotPayload> STREAM_CODEC = FluidStack.OPTIONAL_STREAM_CODEC
            .map(FluidStackSlotPayload::new, FluidStackSlotPayload::value);

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.FLUID_STACK;
    }
}
