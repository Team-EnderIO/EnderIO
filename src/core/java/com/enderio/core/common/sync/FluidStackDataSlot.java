package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidStackDataSlot extends EnderDataSlot<FluidStack> {

    public FluidStackDataSlot(Supplier<FluidStack> getter, Consumer<FluidStack> setter, SyncMode syncMode) {
        super(getter, setter, syncMode);
    }

    @Override
    public CompoundTag toFullNBT() {
        return getter().get().writeToNBT(new CompoundTag());
    }

    @Override
    public FluidStack fromNBT(CompoundTag nbt) {
        return FluidStack.loadFluidStackFromNBT(nbt);
    }
}
