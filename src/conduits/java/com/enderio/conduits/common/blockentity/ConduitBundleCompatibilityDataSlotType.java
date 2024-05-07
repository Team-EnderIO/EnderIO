package com.enderio.conduits.common.blockentity;

import com.enderio.core.common.network.NetworkDataSlot;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Supplier;

// TODO: This is a temporary compatibility layer to fit the old serialization method into the new data slots.
//       The aim is to rewrite ConduitBundle to not need this.
public class ConduitBundleCompatibilityDataSlotType implements NetworkDataSlot.Type<ConduitBundle> {

    public static ConduitBundleCompatibilityDataSlotType DATA_SLOT_TYPE = new ConduitBundleCompatibilityDataSlotType();

    public NetworkDataSlot<ConduitBundle> create(Supplier<ConduitBundle> getter) {
        // Setter is no-op as everything happens in-place.
        return new NetworkDataSlot<>(this, getter, v -> {});
    }

    private ConduitBundleCompatibilityDataSlotType() {
    }

    @Override
    public int hash(ConduitBundle value) {
        return value.getDataVersion();
    }

    @Override
    public Tag save(HolderLookup.Provider lookupProvider, ConduitBundle value) {
        return value.serializeGuiNBT(lookupProvider);
    }

    @Override
    public ConduitBundle parse(HolderLookup.Provider lookupProvider, Tag tag, Supplier<ConduitBundle> currentValueSupplier) {
        var value = currentValueSupplier.get();
        if (tag instanceof CompoundTag compoundTag) {
            value.deserializeNBT(lookupProvider, compoundTag);
        }

        return value;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf, ConduitBundle value) {
        buf.writeNbt(save(buf.registryAccess(), value));
    }

    @Override
    public ConduitBundle read(RegistryFriendlyByteBuf buf, Supplier<ConduitBundle> currentValueSupplier) {
        return parse(buf.registryAccess(), buf.readNbt(), currentValueSupplier);
    }
}
