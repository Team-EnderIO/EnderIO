package com.enderio.enderio.foundation.io;

import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.api.io.SideConfig;
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
