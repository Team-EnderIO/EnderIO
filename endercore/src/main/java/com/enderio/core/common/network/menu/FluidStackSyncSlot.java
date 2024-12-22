package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.FluidStackSlotPayload;
import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class FluidStackSyncSlot implements SyncSlot {

    public static FluidStackSyncSlot standalone() {
        return new FluidStackSyncSlot() {
            private FluidStack value = FluidStack.EMPTY;

            @Override
            public FluidStack get() {
                return value;
            }

            @Override
            public void set(FluidStack value) {
                this.value = value;
            }
        };
    }

    public static FluidStackSyncSlot simple(Supplier<FluidStack> getter, Consumer<FluidStack> setter) {
        return new FluidStackSyncSlot() {

            @Override
            public FluidStack get() {
                return getter.get();
            }

            @Override
            public void set(FluidStack value) {
                setter.accept(value);
            }
        };
    }

    public static FluidStackSyncSlot readOnly(Supplier<FluidStack> getter) {
        return new FluidStackSyncSlot() {

            @Override
            public FluidStack get() {
                return getter.get();
            }

            @Override
            public void set(FluidStack value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private FluidStack lastValue = FluidStack.EMPTY;

    public abstract FluidStack get();

    public abstract void set(FluidStack value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        if (Objects.equals(currentValue, lastValue)) {
            return ChangeType.NONE;
        }

        var changeType = currentValue.getFluid().isSame(lastValue.getFluid()) ? ChangeType.PARTIAL : ChangeType.FULL;
        lastValue = currentValue.copy();
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        if (changeType == ChangeType.PARTIAL) {
            return new IntSlotPayload(get().getAmount());
        }

        return new FluidStackSlotPayload(get());
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            set(get().copyWithAmount(intSlotPayload.value()));
        } else if (payload instanceof FluidStackSlotPayload fluidStackSlotPayload) {
            set(fluidStackSlotPayload.value());
        }
    }
}
