package com.enderio.machines.common.io;

import com.enderio.api.capability.ISideConfig;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

/**
 * Fixed IO Config.
 * Used when a block only has a single mode for all sides (or even wants to disable external IO altogether).
 */
public final class FixedIOConfig implements IIOConfig {
    private final IOMode mode;

    public FixedIOConfig(IOMode mode) {
        this.mode = mode;
    }

    @Override
    public IOMode getMode(Direction side) {
        return mode;
    }

    @Override
    public void setMode(Direction side, IOMode mode) {}

    @Override
    public void cycleMode(Direction side) {}

    @Override
    public boolean supportsMode(Direction side, IOMode mode) {
        return mode == this.mode;
    }

    @Override
    public boolean renderOverlay() {
        // Don't render the overlay as all sides act the same.
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Mode", mode.ordinal());
        return tag;
    }

    // region Stubs

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // Not enabled.
    }

    @Override
    public Capability<ISideConfig> getCapabilityType() {
        return EIOCapabilities.SIDE_CONFIG;
    }

    @Override
    public LazyOptional<ISideConfig> getCapability(@Nullable Direction side) {
        return LazyOptional.empty();
    }

    @Override
    public void invalidateSide(@Nullable Direction side) {}

    @Override
    public void invalidateCaps() {}

    // endregion
}
