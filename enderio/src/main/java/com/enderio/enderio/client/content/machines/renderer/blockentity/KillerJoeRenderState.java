package com.enderio.enderio.client.content.machines.renderer.blockentity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;

public class KillerJoeRenderState extends BlockEntityRenderState {
    public FluidStack fluidStack = FluidStack.EMPTY;
    public int tankCapacity;
    public final ItemStackRenderState weapon = new ItemStackRenderState();
    public Direction facing = Direction.NORTH;
    public float swingProgress;
}
