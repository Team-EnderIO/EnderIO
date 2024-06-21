package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitData;
import com.enderio.conduits.ConduitNBTKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class FluidConduitData implements ConduitData<FluidConduitData> {

    public final boolean isMultiFluid;

    @Nullable
    private Fluid lockedFluid = null;
    private boolean shouldReset = false;

    public FluidConduitData(boolean isMultiFluid) {
        this.isMultiFluid = isMultiFluid;
    }

    @Nullable
    public Fluid lockedFluid() {
        return lockedFluid;
    }

    public void setLockedFluid(@Nullable Fluid lockedFluid) {
        this.lockedFluid = lockedFluid;
    }

    public boolean shouldReset() {
        return shouldReset;
    }

    public void setShouldReset(boolean shouldReset) {
        this.shouldReset = shouldReset;
    }

    @Override
    public void onConnectTo(FluidConduitData otherData) {
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
    public boolean canConnectTo(FluidConduitData otherData) {
        return lockedFluid == null || otherData.lockedFluid == null || lockedFluid == otherData.lockedFluid;
    }

    // region Serialization

    private static final String SHOULD_RESET = "ShouldReset";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (!isMultiFluid) {
            if (lockedFluid != null) {
                nbt.putString(ConduitNBTKeys.FLUID, ForgeRegistries.FLUIDS.getKey(lockedFluid).toString());
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
            if (fluid.equals("null") || ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluid)) == Fluids.EMPTY) {
                setLockedFluid(null);
            } else {
                setLockedFluid(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluid)));
            }
        } else {
            setLockedFluid(null);
        }
        if (nbt.contains(SHOULD_RESET)) {
            shouldReset = nbt.getBoolean(SHOULD_RESET);
        }
    }

    // endregion
}
