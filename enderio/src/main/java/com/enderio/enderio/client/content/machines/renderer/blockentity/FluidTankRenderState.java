package com.enderio.enderio.client.content.machines.renderer.blockentity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTankRenderState extends BlockEntityRenderState {
    public FluidStack fluidStack;
    public int tankCapacity;
}
