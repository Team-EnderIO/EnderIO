package com.enderio.machines.common.io.fluid;

import com.enderio.machines.common.io.IOConfig;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.EnumMap;

public class FluidTankMaster extends FluidTank {

    private final EnumMap<Direction, SidedFluidHandlerAccess> access = new EnumMap(Direction.class);
    private final IOConfig config;
    public FluidTankMaster(int capacity, IOConfig config) {
        super(capacity);
        this.config = config;
    }

    // TODO: Its come to my attention nullable Direction needs to be supported.
    public SidedFluidHandlerAccess getAccess(Direction direction) {
        return access.computeIfAbsent(direction,
            dir -> new SidedFluidHandlerAccess(this, dir));
    }

    public IOConfig getConfig() {
        return config;
    }
}
