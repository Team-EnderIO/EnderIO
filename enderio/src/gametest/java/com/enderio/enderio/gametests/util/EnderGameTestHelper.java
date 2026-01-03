package com.enderio.enderio.gametests.util;

import com.enderio.enderio.api.io.IOConfigurable;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

import java.util.function.Consumer;

public class EnderGameTestHelper extends ExtendedGameTestHelper {
    public EnderGameTestHelper(GameTestInfo info) {
        super(info);
    }

    public void changeIoConfig(int x, int y, int z, Consumer<IOConfigurable> configurer) {
        var blockEntity = getLevel().getBlockEntity(absolutePos(new BlockPos(x, y, z)));
        if (blockEntity instanceof IOConfigurable ioConfigurable) {
            configurer.accept(ioConfigurable);
        } else {
            throw new GameTestAssertException("No IOConfigurable at " + x + "," + y + "," + z);
        }
    }

    public void insertIntoContainer(int x, int y, int z, Item item, int count) {
        var itemHandler = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (itemHandler == null) {
            throw new GameTestAssertException("No item handler at " + x + "," + y + "," + z);
        }

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            int toInsert = Math.min(count, itemHandler.getSlotLimit(i));
            var remainder = itemHandler.insertItem(i, new ItemStack(item, toInsert), false);
            count -= toInsert - remainder.getCount();
            if (count <= 0) {
                return;
            }
        }

        if (count > 0) {
            throw new GameTestAssertException(
                "Could not insert " + count + " items into container at " + x + "," + y + "," + z);
        }
    }

    public void assertContainerHasExactly(int x, int y, int z, Item item, int count) {
        var itemHandler = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (itemHandler == null) {
            throw new GameTestAssertException("No item handler at " + x + "," + y + "," + z);
        }

        int foundCount = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            var available = itemHandler.extractItem(i, Math.min(count, itemHandler.getSlotLimit(i)), true);
            foundCount += available.getCount();
        }

        if (foundCount != count) {
            throw new GameTestAssertException("Expected " + count + " of " + item + " in container at " + x + "," + y
                + "," + z + " but found " + foundCount);
        }
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

    public long getAmountInHandler(int x, int y, int z, Fluid fluid) {
        var fluidHandler = getLevel().getCapability(Capabilities.FluidHandler.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (fluidHandler == null) {
            throw new GameTestAssertException("No fluid handler at " + x + "," + y + "," + z);
        }

        long totalAmount = 0;
        for (int i = 0; i < fluidHandler.getTanks(); i++) {
            if (fluidHandler.getFluidInTank(i).is(fluid)) {
                totalAmount += fluidHandler.getFluidInTank(i).getAmount();
            }
        }

        return totalAmount;
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
