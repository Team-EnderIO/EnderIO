package com.enderio.machines.common.io;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;

/**
 * Sided Fixed IO Config
 * Used when a block only has a non changeable IOMode, but different sides have different Modes
 */
public final class SidedFixedIOConfig implements IIOConfig {
    private final Function<Direction, IOMode> mode;

    public SidedFixedIOConfig(Function<Direction, IOMode> mode) {
        this.mode = mode;
    }

    @Override
    public IOMode getMode(Direction side) {
        return mode.apply(side);
    }

    @Override
    public void setMode(Direction side, IOMode mode) {}

    @Override
    public void cycleMode(Direction side) {}

    @Override
    public boolean supportsMode(Direction side, IOMode mode) {
        return mode == this.mode.apply(side);
    }

    @Override
    public boolean renderOverlay() {
        // Don't render the overlay as all sides act the same.
        return false;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        // Not enabled.
    }
}
