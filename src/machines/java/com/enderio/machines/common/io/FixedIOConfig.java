package com.enderio.machines.common.io;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

/**
 * Fixed IO Config.
 * Used when a block only has a single mode for all sides (or even wants to disable external IO altogether).
 */
public final class FixedIOConfig implements IIOConfig {

    public static final FixedIOConfig DISABLED = new FixedIOConfig(IOMode.DISABLED);

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
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        // Not enabled.
    }

}
