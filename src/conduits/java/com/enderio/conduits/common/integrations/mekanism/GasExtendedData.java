package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.conduits.ConduitNBTKeys;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class GasExtendedData implements IExtendedConduitData<GasExtendedData> {

    public final boolean isMultiFluid;

    @Nullable Gas lockedGas = null;
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
                nbt.putString(ConduitNBTKeys.FLUID, lockedGas.getRegistryName().toString());
            } else {
                nbt.putString(ConduitNBTKeys.FLUID, "null");
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
        if (nbt.contains(ConduitNBTKeys.FLUID) && !isMultiFluid) {
            String fluid = nbt.getString(ConduitNBTKeys.FLUID);
            if (fluid.equals("null") || Gas.getFromRegistry(new ResourceLocation(fluid)) == MekanismAPI.EMPTY_GAS) {
                setlockedGas(null);
            } else {
                setlockedGas(Gas.getFromRegistry(new ResourceLocation(fluid)));
            }
        } else {
            setlockedGas(null);
        }
        if (nbt.contains(SHOULD_RESET)) {
            shouldReset = nbt.getBoolean(SHOULD_RESET);
        }
    }

    // endregion

    private void setlockedGas(@Nullable Gas lockedGas) {
        this.lockedGas = lockedGas;
    }
}
