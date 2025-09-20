package com.enderio.conduits.tests.fluid;

import com.enderio.conduits.tests.ConduitGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidConduitGameTestHelper extends ConduitGameTestHelper {
    public FluidConduitGameTestHelper(GameTestInfo info) {
        super(info);
    }

    public void fillContainer(int x, int y, int z, Fluid fluid, int amount) {
        var fluidHandler = getLevel().getCapability(Capabilities.FluidHandler.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (fluidHandler == null) {
            throw new GameTestAssertException("No fluid handler at " + x + "," + y + "," + z);
        }

        int filled = fluidHandler.fill(new FluidStack(fluid, amount), IFluidHandler.FluidAction.EXECUTE);

        if (filled < amount) {
            throw new GameTestAssertException(
                    "Could not fill tank with all " + amount + " of the fluid into container at " + x + "," + y + "," + z);
        }
    }

    public void assertContainerHasExactly(int x, int y, int z, Fluid fluid, int amount) {
        var fluidHandler = getLevel().getCapability(Capabilities.FluidHandler.BLOCK, absolutePos(new BlockPos(x, y, z)),
                null);
        if (fluidHandler == null) {
            throw new GameTestAssertException("No fluid handler at " + x + "," + y + "," + z);
        }

        int foundAmount = fluidHandler.drain(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE).getAmount();

        if (foundAmount != amount) {
            throw new GameTestAssertException("Expected " + amount + " of " + fluid + " in tank at " + x + "," + y
                    + "," + z + " but found " + foundAmount);
        }
    }
}
