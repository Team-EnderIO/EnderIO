package com.enderio.machines.common.io.fluid;

import com.enderio.api.io.IIOConfig;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.EnumMap;

public class FluidTankMaster extends FluidTank {

    private final EnumMap<Direction, SidedFluidHandlerAccess> access = new EnumMap(Direction.class);
    private final IIOConfig config;
    public FluidTankMaster(int capacity, IIOConfig config) {
        super(capacity);
        this.config = config;
    }

    public SidedFluidHandlerAccess getAccess(Direction direction) {
        return access.computeIfAbsent(direction,
            dir -> new SidedFluidHandlerAccess(this, dir));
    }

    public IIOConfig getConfig() {
        return config;
    }
}
