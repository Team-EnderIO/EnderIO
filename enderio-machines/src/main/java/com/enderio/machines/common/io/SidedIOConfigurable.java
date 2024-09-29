package com.enderio.machines.common.io;

import com.enderio.base.api.capability.SideConfig;
import com.enderio.base.api.io.IOConfigurable;
import com.enderio.base.api.io.IOMode;
import net.minecraft.core.Direction;

public record SidedIOConfigurable(
    IOConfigurable ioConfigurable,
    Direction side
) implements SideConfig {

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
