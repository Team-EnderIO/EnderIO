package com.enderio.conduits.common.blockentity;

import com.enderio.core.common.network.slot.NetworkDataSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.function.Supplier;

public class ConduitBundleNetworkDataSlot extends NetworkDataSlot<ConduitBundle> {
    public ConduitBundleNetworkDataSlot(Supplier<ConduitBundle> getter) {
        super(getter, (v) -> {});
    }

    @Override
    public Tag serializeValueNBT(ConduitBundle value) {
        return value.serializeNBT();
    }

    @Override
    public void fromNBT(Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            getter.get().deserializeNBT(compoundTag);
        } else {
            throw new IllegalStateException("Invalid compound tag was passed over the network.");
        }
    }

    @Override
    protected ConduitBundle valueFromNBT(Tag nbt) {
        return null;
    }

    @Override
    protected int hashCode(ConduitBundle value) {
        // TODO: This is slow as shit
        int code = value.serializeNBT().hashCode();
        return code;
    }
}
