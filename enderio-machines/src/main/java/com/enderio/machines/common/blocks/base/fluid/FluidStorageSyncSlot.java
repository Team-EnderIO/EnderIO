package com.enderio.machines.common.blocks.base.fluid;

import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.payload.FluidStackSlotPayload;
import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.PairSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayloadType;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class FluidStorageSyncSlot implements SyncSlot {

    public static FluidStorageSyncSlot standalone() {
        return new FluidStorageSyncSlot() {
            private FluidStorageInfo value = new FluidStorageInfo(FluidStack.EMPTY, 0);

            @Override
            public FluidStorageInfo get() {
                return value;
            }

            @Override
            public void set(FluidStorageInfo value) {
                this.value = value;
            }
        };
    }

    public static FluidStorageSyncSlot simple(Supplier<FluidStorageInfo> getter, Consumer<FluidStorageInfo> setter) {
        return new FluidStorageSyncSlot() {

            @Override
            public FluidStorageInfo get() {
                return getter.get();
            }

            @Override
            public void set(FluidStorageInfo value) {
                setter.accept(value);
            }
        };
    }

    public static FluidStorageSyncSlot readOnly(Supplier<FluidStorageInfo> getter) {
        return new FluidStorageSyncSlot() {

            @Override
            public FluidStorageInfo get() {
                return getter.get();
            }

            @Override
            public void set(FluidStorageInfo value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private FluidStorageInfo lastValue;

    public abstract FluidStorageInfo get();

    public abstract void set(FluidStorageInfo value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        if (Objects.equals(lastValue, currentValue)) {
            return ChangeType.NONE;
        }

        boolean isSameCapacity = lastValue != null && currentValue.capacity() == lastValue.capacity();
        boolean isSameFluid = lastValue != null
                && FluidStack.isSameFluid(currentValue.contents(), lastValue.contents());

        var changeType = isSameFluid && isSameCapacity ? ChangeType.PARTIAL : ChangeType.FULL;

        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        var value = get();
        if (changeType == ChangeType.PARTIAL) {
            return new IntSlotPayload(value.contents().getAmount());
        }

        return new PairSlotPayload(new FluidStackSlotPayload(value.contents()), new IntSlotPayload(value.capacity()));
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            set(get().withContents(get().contents().copyWithAmount(intSlotPayload.value())));
        } else if (payload instanceof PairSlotPayload pair) {
            if (pair.left().type() != SlotPayloadType.FLUID_STACK || pair.right().type() != SlotPayloadType.INT) {
                return;
            }

            set(new FluidStorageInfo(((FluidStackSlotPayload) pair.left()).value(),
                    ((IntSlotPayload) pair.right()).value()));
        }
    }
}
