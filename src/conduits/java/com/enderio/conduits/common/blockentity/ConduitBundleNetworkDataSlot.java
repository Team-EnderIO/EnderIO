package com.enderio.conduits.common.blockentity;

import com.enderio.core.common.network.slot.NetworkDataSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

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
            throw new IllegalStateException("Invalid conduit/compound tag was passed over the network.");
        }
    }

    @Override
    protected ConduitBundle valueFromNBT(Tag nbt) {
        return null;
    }

    @Override
    protected int hashCode(ConduitBundle value) {
        return value.getDataVersion();
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, ConduitBundle value) {
        buf.writeNbt(value.serializeNBT());
    }

    @Override
    public ConduitBundle valueFromBuffer(FriendlyByteBuf buf) {
        try {
            ConduitBundle conduitBundle = getter.get();
            conduitBundle.deserializeNBT(buf.readNbt());
            return conduitBundle;
        } catch (Exception e) {
            throw new IllegalStateException("Invalid conduit/compound tag buffer was passed over the network.");
        }
    }
}
