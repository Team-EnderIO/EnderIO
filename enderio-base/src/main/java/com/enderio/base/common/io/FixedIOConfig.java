package com.enderio.base.common.io;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class FixedIOConfig implements IIOConfig {
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

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // Not enabled.
    }
}
