package com.enderio.base.common.network;

import com.enderio.base.api.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidStack;

public record C2SSetFluidFilterSlot(int containerId, int slotIndex, FluidStack fluidStack)
        implements CustomPacketPayload {

    public static final Type<C2SSetFluidFilterSlot> TYPE = new Type<>(EnderIO.loc("set_fluid_filter_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSetFluidFilterSlot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, C2SSetFluidFilterSlot::containerId, ByteBufCodecs.INT, C2SSetFluidFilterSlot::slotIndex,
            FluidStack.STREAM_CODEC, C2SSetFluidFilterSlot::fluidStack, C2SSetFluidFilterSlot::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
