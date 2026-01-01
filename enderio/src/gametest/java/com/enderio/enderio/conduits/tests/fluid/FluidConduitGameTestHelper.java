package com.enderio.enderio.conduits.tests.fluid;

import com.enderio.enderio.conduits.tests.ConduitGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class FluidConduitGameTestHelper extends ConduitGameTestHelper {
    public FluidConduitGameTestHelper(GameTestInfo info) {
        super(info);
    }

    public void fillContainer(int x, int y, int z, Fluid fluid, int amount) {
        var fluidHandler = getLevel().getCapability(Capabilities.Fluid.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (fluidHandler == null) {
            throw helper.assertionException("No fluid handler at " + x + "," + y + "," + z);
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int filled = fluidHandler.insert(FluidResource.of(fluid), amount, transaction);
            if (filled != amount) {
                throw helper.assertionException(
                    "Could not fill tank with all " + amount + " of the fluid into container at " + x + "," + y + "," + z);
            }

            transaction.commit();
        }
    }

    public void assertContainerHasExactly(int x, int y, int z, Fluid fluid, int amount) {
        var fluidHandler = getLevel().getCapability(Capabilities.Fluid.BLOCK, absolutePos(new BlockPos(x, y, z)),
                null);
        if (fluidHandler == null) {
            throw helper.assertionException("No fluid handler at " + x + "," + y + "," + z);
        }

        long totalAmount = 0;
        for (int i = 0; i < fluidHandler.size(); i++) {
            if (fluidHandler.getResource(i).is(fluid)) {
                totalAmount += fluidHandler.getAmountAsLong(i);
            }
        }

        if (totalAmount != amount) {
            throw helper.assertionException("Expected " + amount + " of " + fluid + " in tank at " + x + "," + y
                    + "," + z + " but found " + totalAmount);
        }
    }
}
