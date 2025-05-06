package com.enderio.modconduits.common.modules.mekanism.chemical_filter;

import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.LongSlotPayload;
import com.enderio.core.common.network.menu.payload.PairSlotPayload;
import com.enderio.core.common.network.menu.payload.ResourceLocationSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ChemicalStackSyncSlot implements SyncSlot {

    public static ChemicalStackSyncSlot standalone() {
        return new ChemicalStackSyncSlot() {
            private ChemicalStack value = ChemicalStack.EMPTY;

            @Override
            public ChemicalStack get() {
                return value;
            }

            @Override
            public void set(ChemicalStack value) {
                this.value = value;
            }
        };
    }

    public static ChemicalStackSyncSlot simple(Supplier<ChemicalStack> getter, Consumer<ChemicalStack> setter) {
        return new ChemicalStackSyncSlot() {

            @Override
            public ChemicalStack get() {
                return getter.get();
            }

            @Override
            public void set(ChemicalStack value) {
                setter.accept(value);
            }
        };
    }

    public static ChemicalStackSyncSlot readOnly(Supplier<ChemicalStack> getter) {
        return new ChemicalStackSyncSlot() {

            @Override
            public ChemicalStack get() {
                return getter.get();
            }

            @Override
            public void set(ChemicalStack value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private ChemicalStack lastValue = ChemicalStack.EMPTY;

    public abstract ChemicalStack get();

    public abstract void set(ChemicalStack value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        if (Objects.equals(currentValue, lastValue)) {
            return ChangeType.NONE;
        }

        var changeType = currentValue.getChemical().equals(lastValue.getChemical()) ? ChangeType.PARTIAL : ChangeType.FULL;
        lastValue = currentValue.copy();
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        if (changeType == ChangeType.PARTIAL) {
            return new LongSlotPayload(get().getAmount());
        }

        return new PairSlotPayload(new ResourceLocationSlotPayload(get().getChemical().getRegistryName()), new LongSlotPayload(get().getAmount()));
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof LongSlotPayload(long amount)) {
            set(get().copyWithAmount(amount));
        } else if (payload instanceof PairSlotPayload(SlotPayload left, SlotPayload right)) {
            if (left instanceof ResourceLocationSlotPayload(ResourceLocation chemicalId)
                && right instanceof LongSlotPayload(long amount)) {
                var stack = new ChemicalStack(MekanismAPI.CHEMICAL_REGISTRY.get(chemicalId), amount);
                set(stack);
            }
        }
    }
}
