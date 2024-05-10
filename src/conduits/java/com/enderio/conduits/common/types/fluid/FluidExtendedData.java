package com.enderio.conduits.common.types.fluid;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.conduits.ConduitNBTKeys;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class FluidExtendedData implements ExtendedConduitData<FluidExtendedData> {

    public final boolean isMultiFluid;

    @Nullable
    Fluid lockedFluid = null;
    boolean shouldReset = false;

    public FluidExtendedData(boolean isMultiFluid) {
        this.isMultiFluid = isMultiFluid;
    }

    @Override
    public void onConnectTo(FluidExtendedData otherData) {
        if (lockedFluid != null) {
            if (otherData.lockedFluid != null && lockedFluid != otherData.lockedFluid) {
                EnderIO.LOGGER.warn("incompatible fluid conduits merged");
            }
            otherData.setLockedFluid(lockedFluid);
        } else if (otherData.lockedFluid != null) {
            setLockedFluid(otherData.lockedFluid);
        }
    }

    @Override
    public boolean canConnectTo(FluidExtendedData otherData) {
        return lockedFluid == null || otherData.lockedFluid == null || lockedFluid == otherData.lockedFluid;
    }

    // region Serialization

    private static final String SHOULD_RESET = "ShouldReset";

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = new CompoundTag();
        if (!isMultiFluid) {
            if (lockedFluid != null) {
                nbt.putString(ConduitNBTKeys.FLUID, BuiltInRegistries.FLUID.getKey(lockedFluid).toString());
            } else {
                nbt.putString(ConduitNBTKeys.FLUID, "null");
            }
        }
        return nbt;
    }

    @Override
    public CompoundTag serializeRenderNBT(HolderLookup.Provider lookupProvider) {
        return serializeNBT(lookupProvider);
    }

    @Override
    public CompoundTag serializeGuiNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = serializeNBT(lookupProvider);
        nbt.putBoolean(SHOULD_RESET, shouldReset);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        if (nbt.contains(ConduitNBTKeys.FLUID) && !isMultiFluid) {
            String fluid = nbt.getString(ConduitNBTKeys.FLUID);
            if (fluid.equals("null") || BuiltInRegistries.FLUID.get(new ResourceLocation(fluid)) == Fluids.EMPTY) {
                setLockedFluid(null);
            } else {
                setLockedFluid(BuiltInRegistries.FLUID.get(new ResourceLocation(fluid)));
            }
        } else {
            setLockedFluid(null);
        }
        if (nbt.contains(SHOULD_RESET)) {
            shouldReset = nbt.getBoolean(SHOULD_RESET);
        }
    }

    // endregion

    private void setLockedFluid(@Nullable Fluid lockedFluid) {
        this.lockedFluid = lockedFluid;
    }
}
