package com.enderio.core.common.network.slot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidStackNetworkDataSlot extends NetworkDataSlot<FluidStack> {

    public FluidStackNetworkDataSlot(Supplier<FluidStack> getter, Consumer<FluidStack> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(FluidStack value) {
        return value.writeToNBT(new CompoundTag());
    }

    @Override
    protected FluidStack valueFromNBT(Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            return FluidStack.loadFluidStackFromNBT(compoundTag);
        } else {
            throw new IllegalStateException("Invalid fluidstack/compound tag was passed over the network.");
        }
    }

    @Override
    protected int hashCode(FluidStack value) {
        // Basically just re-adds what was removed in
        // https://github.com/MinecraftForge/MinecraftForge/pull/9602
        return value.hashCode() * 31 + value.getAmount();
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, FluidStack value) {
        value.writeToPacket(buf);
    }

    @Override
    public FluidStack valueFromBuffer(FriendlyByteBuf buf) {
        try {
            return FluidStack.readFromPacket(buf);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid fluidstack buffer was passed over the network.");
        }
    }
}
