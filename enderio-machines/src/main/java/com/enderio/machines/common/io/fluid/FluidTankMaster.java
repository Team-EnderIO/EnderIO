package com.enderio.machines.common.io.fluid;

import com.enderio.base.common.blockentity.IOConfig;
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

    public SidedFluidHandlerAccess getAccess(Direction direction) {
        return access.computeIfAbsent(direction,
            dir -> new SidedFluidHandlerAccess(this, dir));
    }

    public IOConfig getConfig() {
        return config;
    }
}
