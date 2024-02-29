package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IExtendedConduitData;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class GasExtendedData implements IExtendedConduitData<GasExtendedData> {

    public final boolean isMultiFluid;

    @Nullable Chemical<?> lockedGas = null;
    boolean shouldReset = false;

    public GasExtendedData(boolean isMultiFluid) {this.isMultiFluid = isMultiFluid;}

    @Override
    public void onConnectTo(GasExtendedData otherData) {
        if (lockedGas != null) {
            if (otherData.lockedGas != null && lockedGas != otherData.lockedGas) {
                EnderIO.LOGGER.warn("incompatible fluid conduits merged");
            }
            otherData.setlockedGas(lockedGas);
        } else if (otherData.lockedGas != null) {
            setlockedGas(otherData.lockedGas);
        }
    }

    @Override
    public boolean canConnectTo(GasExtendedData otherData) {
        return lockedGas == null || otherData.lockedGas == null || lockedGas == otherData.lockedGas;
    }

    // region Serialization

    private static final String SHOULD_RESET = "ShouldReset";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (!isMultiFluid) {
            if (lockedGas != null) {
                ChemicalType.getTypeFor(lockedGas).write(nbt);
                lockedGas.write(nbt);
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
        ChemicalType chemicalType = ChemicalType.fromNBT(nbt);
        if (chemicalType != null) {
            Chemical<?> chem = switch (chemicalType) {
                case GAS -> Gas.readFromNBT(nbt);
                case INFUSION -> InfuseType.readFromNBT(nbt);
                case PIGMENT -> Pigment.readFromNBT(nbt);
                case SLURRY -> Slurry.readFromNBT(nbt);
            };
            if (chem != MekanismAPI.EMPTY_INFUSE_TYPE && chem != MekanismAPI.EMPTY_GAS && chem != MekanismAPI.EMPTY_PIGMENT && chem != MekanismAPI.EMPTY_SLURRY) {
                setlockedGas(chem);
            }
            else {
                setlockedGas(null);
            }
        } else {
            setlockedGas(null);
        }
        if (nbt.contains(SHOULD_RESET)) {
            shouldReset = nbt.getBoolean(SHOULD_RESET);
        }
    }

    // endregion

    private void setlockedGas(@Nullable Chemical<?> lockedGas) {
        this.lockedGas = lockedGas;
    }
}
