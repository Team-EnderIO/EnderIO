package com.enderio.machines.common.io;

import com.enderio.api.capability.ISideConfig;
import com.enderio.api.io.IOConfigurable;
import com.enderio.api.io.IOMode;
import net.minecraft.core.Direction;

public record SidedIOConfig(
    IOConfigurable ioConfigurable,
    Direction side
) implements ISideConfig {

    @Override
    public IOMode getMode() {
        return ioConfigurable.getIOMode(side);
    }

    @Override
    public void setMode(IOMode mode) {
        if (ioConfigurable.isIOConfigMutable()) {
            ioConfigurable.setIOMode(side, mode);
        }
    }

    @Override
    public void cycleMode() {
        if (ioConfigurable.isIOConfigMutable()) {
            ioConfigurable.cycleIOMode(side);
        }
    }
}
