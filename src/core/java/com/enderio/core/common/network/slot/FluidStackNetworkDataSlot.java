package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidStackNetworkDataSlot extends NetworkDataSlot<FluidStack> {

    public FluidStackNetworkDataSlot(Supplier<FluidStack> getter, Consumer<FluidStack> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(HolderLookup.Provider lookupProvider, FluidStack value) {
        return value.save(lookupProvider);
    }

    @Override
    protected FluidStack valueFromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            return FluidStack.CODEC.decode(lookupProvider.createSerializationContext(NbtOps.INSTANCE), nbt).result().get().getFirst();
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
    public void toBuffer(RegistryFriendlyByteBuf buf, FluidStack value) {
        FluidStack.STREAM_CODEC.encode(buf, value);
    }

    @Override
    public FluidStack valueFromBuffer(RegistryFriendlyByteBuf buf) {
        try {
            return FluidStack.STREAM_CODEC.decode(buf);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid fluidstack buffer was passed over the network.");
        }
    }
}
