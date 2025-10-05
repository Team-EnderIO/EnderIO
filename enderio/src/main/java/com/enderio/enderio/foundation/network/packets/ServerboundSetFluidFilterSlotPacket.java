package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidStack;

public record ServerboundSetFluidFilterSlotPacket(int containerId, int slotIndex, FluidStack fluidStack)
        implements CustomPacketPayload {

    public static final Type<ServerboundSetFluidFilterSlotPacket> TYPE = new Type<>(EnderIO.rl("set_fluid_filter_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetFluidFilterSlotPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundSetFluidFilterSlotPacket::containerId, ByteBufCodecs.INT, ServerboundSetFluidFilterSlotPacket::slotIndex,
            FluidStack.STREAM_CODEC, ServerboundSetFluidFilterSlotPacket::fluidStack, ServerboundSetFluidFilterSlotPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
