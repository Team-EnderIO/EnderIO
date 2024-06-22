package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitData;
import mekanism.api.chemical.merged.BoxedChemical;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class ChemicalConduitData implements ConduitData<ChemicalConduitData> {

    public final boolean isMultiChemical;

    BoxedChemical lockedChemical = BoxedChemical.EMPTY;
    boolean shouldReset = false;

    public ChemicalConduitData( boolean shouldReset, boolean isMultiChemical, Optional<BoxedChemical> lockedChemical) {
        this.shouldReset = shouldReset;
        this.isMultiChemical = isMultiChemical;
        this.lockedChemical = lockedChemical.orElse(BoxedChemical.EMPTY);
    }

    public ChemicalConduitData(boolean isMultiChemical) {
        this.isMultiChemical = isMultiChemical;
    }

    @Override
    public void onConnectTo(ChemicalConduitData otherData) {
        if (!lockedChemical.isEmpty()) {
            if (!otherData.lockedChemical.isEmpty() && !lockedChemical.equals(otherData.lockedChemical)) {
                EnderIO.LOGGER.warn("incompatible chemical conduits merged");
            }
            otherData.setlockedChemical(lockedChemical);
        } else if (!otherData.lockedChemical.isEmpty()) {
            setlockedChemical(otherData.lockedChemical);
        }
    }

    @Override
    public boolean canConnectTo(ChemicalConduitData otherData) {
        return lockedChemical.isEmpty() || otherData.lockedChemical.isEmpty() || lockedChemical.equals(otherData.lockedChemical);
    }

    private void setlockedChemical(BoxedChemical lockedChemical) {
        this.lockedChemical = lockedChemical;
    }

    // region Serialization

    private static final String KEY_SHOULD_RESET = "ShouldReset";
    private static final String KEY_LOCKED_FLUID = "LockedFluid";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(KEY_SHOULD_RESET, shouldReset);
        if (!lockedChemical.isEmpty()) {
            tag.put(KEY_LOCKED_FLUID, lockedChemical.write(new CompoundTag()));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        if (compoundTag.contains(KEY_SHOULD_RESET)) {
            shouldReset = compoundTag.getBoolean(KEY_SHOULD_RESET);
        }

        if (compoundTag.contains(KEY_LOCKED_FLUID, CompoundTag.TAG_COMPOUND)) {
            lockedChemical = BoxedChemical.read(compoundTag.getCompound(KEY_LOCKED_FLUID));
        }
    }

    // endregion
}
