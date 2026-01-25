package com.enderio.enderio.gametests.util;

import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.foundation.io.energy.IMachineEnergyStorage;
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

    // region Item Storage

    public void insertIntoContainer(int x, int y, int z, Item item, int count) {
        insertIntoContainer(x, y, z, new ItemStack(item, count));
    }

    public void insertIntoContainer(int x, int y, int z, ItemStack itemStack) {
        var itemHandler = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (itemHandler == null) {
            throw new GameTestAssertException("No item handler at " + x + "," + y + "," + z);
        }

        int count = itemStack.getCount();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            int toInsert = Math.min(count, itemHandler.getSlotLimit(i));
            var stackToInsert = itemStack.copyWithCount(toInsert);
            var remainder = itemHandler.insertItem(i, stackToInsert, false);
            count -= toInsert - remainder.getCount();
            if (count <= 0) {
                return;
            }
        }

        if (count > 0) {
            throw new GameTestAssertException(
                "Could not insert " + itemStack.getCount() + " " + itemStack.getItem() + " into container at " + x + "," + y + "," + z);
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
            var stack = itemHandler.getStackInSlot(i);
            if (stack.is(item)) {
                foundCount += stack.getCount();
            }
        }

        if (foundCount != count) {
            throw new GameTestAssertException("Expected " + count + " of " + item + " in container at " + x + "," + y
                + "," + z + " but found " + foundCount);
        }
    }

    /**
     * Insert an item into a specific slot of a container.
     */
    public void insertItemIntoSlot(int x, int y, int z, int slot, ItemStack itemStack) {
        var itemHandler = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (itemHandler == null) {
            throw new GameTestAssertException("No item handler at " + x + "," + y + "," + z);
        }

        var remainder = itemHandler.insertItem(slot, itemStack, false);
        if (!remainder.isEmpty()) {
            throw new GameTestAssertException(
                "Could not insert " + itemStack.getCount() + " " + itemStack.getItem() + " into slot " + slot + " at " + x + "," + y + "," + z +
                    ", " + remainder.getCount() + " items remained");
        }
    }

    /**
     * Get the item stack in a specific slot of a container.
     */
    public ItemStack getItemInSlot(int x, int y, int z, int slot) {
        var itemHandler = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (itemHandler == null) {
            throw new GameTestAssertException("No item handler at " + x + "," + y + "," + z);
        }

        return itemHandler.getStackInSlot(slot);
    }

    /**
     * Assert that a specific slot contains a specific item.
     */
    public void assertSlotHasItem(int x, int y, int z, int slot, Item expectedItem) {
        ItemStack stack = getItemInSlot(x, y, z, slot);
        if (!stack.is(expectedItem)) {
            throw new GameTestAssertException(
                "Expected " + expectedItem + " in slot " + slot + " at " + x + "," + y + "," + z +
                    ", but found: " + (stack.isEmpty() ? "empty" : stack.getItem()));
        }
    }

    /**
     * Assert that a specific slot is empty.
     */
    public void assertSlotHasNoItem(int x, int y, int z, int slot) {
        ItemStack stack = getItemInSlot(x, y, z, slot);
        if (!stack.isEmpty()) {
            throw new GameTestAssertException(
                "Expected empty slot " + slot + " at " + x + "," + y + "," + z +
                    ", but found: " + stack.getItem());
        }
    }

    // endregion

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

    /**
     * Provide energy to a block entity by directly accessing its energy capability.
     */
    public void provideEnergy(int x, int y, int z, int amount) {
        var energyHandler = getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (energyHandler == null) {
            throw new GameTestAssertException("No energy handler at " + x + "," + y + "," + z);
        }

        // Short path for MachineEnergyHandler to avoid insertion limits.
        if (energyHandler instanceof IMachineEnergyStorage machineEnergyStorage) {
            int inserted = machineEnergyStorage.addEnergy(amount);
            if (inserted != amount) {
                throw new GameTestAssertException("Could not insert all " + amount + " energy into block at " + x + "," + y + "," + z + ", only inserted " + inserted);
            }

            return;
        }

        int inserted = energyHandler.receiveEnergy(amount, true);
        if (inserted != amount) {
            throw new GameTestAssertException("Could not insert all " + amount + " energy into block at " + x + "," + y + "," + z + ", only inserted " + inserted);
        }
    }

    public void assertEnergyStored(int x, int y, int z, int expectedAmount) {
        var energyHandler = getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (energyHandler == null) {
            throw new GameTestAssertException("No energy handler at " + x + "," + y + "," + z);
        }

        int stored = energyHandler.getEnergyStored();
        if (stored != expectedAmount) {
            throw new GameTestAssertException("Expected " + expectedAmount + " energy in block at " + x + "," + y + "," + z + ", but found " + stored);
        }
    }
}
