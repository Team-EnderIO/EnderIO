package com.enderio.machines.common.io;

import com.enderio.api.io.IIOConfigurable;
import com.enderio.api.io.IOMode;
import net.minecraft.core.Direction;

public record DumbIOConfigurable(IOConfig config) implements IIOConfigurable {

    public static DumbIOConfigurable DISABLED = new DumbIOConfigurable(IOConfig.of(IOMode.DISABLED));

    @Override
    public IOMode getIOMode(Direction side) {
        return config.getMode(side);
    }

    @Override
    public boolean isIOConfigMutable() {
        return false;
    }

    @Override
    public boolean shouldRenderIOConfigOverlay() {
        return false;
    }

    @Override
    public void setIOMode(Direction side, IOMode state) {
        throw new IllegalStateException("Cannot edit dummy configurable.");
    }

    @Override
    public boolean supportsIOMode(Direction side, IOMode state) {
        return false;
    }
}
