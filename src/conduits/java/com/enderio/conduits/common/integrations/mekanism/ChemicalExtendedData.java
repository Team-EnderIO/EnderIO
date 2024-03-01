package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IExtendedConduitData;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.merged.BoxedChemical;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class ChemicalExtendedData implements IExtendedConduitData<ChemicalExtendedData> {

    public final boolean isMultiChemical;

    @Nullable BoxedChemical lockedChemical = null;
    boolean shouldReset = false;

    public ChemicalExtendedData(boolean isMultiChemical) {this.isMultiChemical = isMultiChemical;}

    @Override
    public void onConnectTo(ChemicalExtendedData otherData) {
        if (lockedChemical != null) {
            if (otherData.lockedChemical != null && lockedChemical != otherData.lockedChemical) {
                EnderIO.LOGGER.warn("incompatible chemical conduits merged");
            }
            otherData.setlockedChemical(lockedChemical);
        } else if (otherData.lockedChemical != null) {
            setlockedChemical(otherData.lockedChemical);
        }
    }

    @Override
    public boolean canConnectTo(ChemicalExtendedData otherData) {
        return lockedChemical == null || otherData.lockedChemical == null || lockedChemical == otherData.lockedChemical;
    }

    // region Serialization

    private static final String SHOULD_RESET = "ShouldReset";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (!isMultiChemical) {
            if (lockedChemical != null) {
                lockedChemical.write(nbt);
            }
        }
        return nbt;
    }
    @Override
    public CompoundTag serializeRenderNBT() {
        return serializeNBT();
    }

    @Override
    public CompoundTag serializeGuiNBT() {
        CompoundTag nbt = serializeNBT();
        nbt.putBoolean(SHOULD_RESET, shouldReset);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        BoxedChemical chemical = BoxedChemical.read(nbt);
        if (!chemical.isEmpty()) {
            setlockedChemical(chemical);
        } else {
            setlockedChemical(null);
        }
        if (nbt.contains(SHOULD_RESET)) {
            shouldReset = nbt.getBoolean(SHOULD_RESET);
        }
    }

    // endregion

    private void setlockedChemical(@Nullable BoxedChemical lockedChemical) {
        this.lockedChemical = lockedChemical;
    }
}
