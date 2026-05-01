package com.enderio.enderio.gametests.util;

import com.enderio.core.common.storage.ExternalResourceStorageView;
import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.foundation.energy.MachineEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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
            throw assertionException("No IOConfigurable at " + x + "," + y + "," + z);
        }
    }

    // region Item Storage

    public void insertIntoContainer(int x, int y, int z, Item item, int count) {
        insertIntoContainer(x, y, z, ItemResource.of(item), count);
    }

    public void insertIntoContainer(int x, int y, int z, ItemStack itemStack) {
        insertIntoContainer(x, y, z, ItemResource.of(itemStack), itemStack.getCount());
    }

    public void insertIntoContainer(int x, int y, int z, ItemResource item, int count) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (itemHandler == null) {
            throw assertionException("No item handler at " + x + "," + y + "," + z);
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = itemHandler.insert(item, count, transaction);
            if (inserted != count) {
                throw assertionException("Could not insert " + count + " " + item + " into container at " + x + "," + y + "," + z);
            }

            transaction.commit();
        }
    }

    public void assertContainerHasExactly(int x, int y, int z, Item item, int count) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (itemHandler == null) {
            throw assertionException("No item handler at " + x + "," + y + "," + z);
        }

        long foundCount = 0;
        for (int i = 0; i < itemHandler.size(); i++) {
            if (itemHandler.getResource(i).is(item)) {
                foundCount += itemHandler.getAmountAsLong(i);
            }
        }

        if (foundCount != count) {
            throw assertionException("Expected " + count + " of " + item + " in container at " + x + "," + y
                + "," + z + " but found " + foundCount);
        }
    }

    public void assertContainerHasAtleast(int x, int y, int z, Item item, int count) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (itemHandler == null) {
            throw assertionException("No item handler at " + x + "," + y + "," + z);
        }

        long foundCount = 0;
        for (int i = 0; i < itemHandler.size(); i++) {
            if (itemHandler.getResource(i).is(item)) {
                foundCount += itemHandler.getAmountAsLong(i);
            }
        }

        if (foundCount < count) {
            throw assertionException("Expected at least " + count + " of " + item + " in container at " + x + "," + y
                + "," + z + " but found " + foundCount);
        }
    }

    /**
     * Insert an item into a specific slot of a container.
     */
    public void insertItemIntoSlot(int x, int y, int z, int slot, ItemStack itemStack) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (itemHandler == null) {
            throw assertionException("No item handler at " + x + "," + y + "," + z);
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = itemHandler.insert(slot, ItemResource.of(itemStack), itemStack.getCount(), transaction);
            if (inserted != itemStack.getCount()) {
                throw assertionException("Could not insert " + itemStack + " into container at " + x + "," + y + "," + z);
            }

            transaction.commit();
        }
    }

    /**
     * Insert an item into a specific slot of a container.
     */
    public void insertItemIntoSlot(int x, int y, int z, ResourceSlotId<ItemResource> slot, ItemStack itemStack) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (itemHandler == null) {
            throw assertionException("No item handler at " + x + "," + y + "," + z);
        }

        // TODO: This is a tiny bit horrible.
        ResourceStorage<ItemResource> itemStorage = null;
        if (itemHandler instanceof ResourceStorage<ItemResource>) {
            itemStorage = (ResourceStorage<ItemResource>) itemHandler;
        } else if (itemHandler instanceof ExternalResourceStorageView<ItemResource> externalItemStorage) {
            itemStorage = externalItemStorage.getDelegate();
        }

        if (itemStorage == null) {
            throw assertionException("No item storage at " + x + "," + y + "," + z);
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = itemStorage.insert(slot, ItemResource.of(itemStack), itemStack.getCount(), transaction);
            if (inserted != itemStack.getCount()) {
                throw assertionException("Could not insert " + itemStack + " into container at " + x + "," + y + "," + z);
            }

            transaction.commit();
        }
    }

    /**
     * Get the item stack in a specific slot of a container.
     */
    public ItemStack getItemInSlot(int x, int y, int z, int slot) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (itemHandler == null) {
            throw assertionException("No item handler at " + x + "," + y + "," + z);
        }

        return itemHandler.getResource(slot).toStack(itemHandler.getAmountAsInt(slot));
    }

    /**
     * Get the item stack in a specific slot of a container.
     */
    public ItemStack getItemInSlot(int x, int y, int z, ResourceSlotId<ItemResource> slot) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (itemHandler == null) {
            throw assertionException("No item handler at " + x + "," + y + "," + z);
        }

        // TODO: This is a tiny bit horrible.
        ResourceStorage<ItemResource> itemStorage = null;
        if (itemHandler instanceof ResourceStorage<ItemResource>) {
            itemStorage = (ResourceStorage<ItemResource>) itemHandler;
        } else if (itemHandler instanceof ExternalResourceStorageView<ItemResource> externalItemStorage) {
            itemStorage = externalItemStorage.getDelegate();
        }

        if (itemStorage == null) {
            throw assertionException("No item storage at " + x + "," + y + "," + z);
        }

        return itemStorage.getResource(slot).toStack(itemStorage.getAmountAsInt(slot));
    }

    /**
     * Assert that a specific slot contains a specific item.
     */
    public void assertSlotHasItem(int x, int y, int z, int slot, Item expectedItem) {
        ItemStack stack = getItemInSlot(x, y, z, slot);
        if (!stack.is(expectedItem)) {
            throw assertionException(
                "Expected " + expectedItem + " in slot " + slot + " at " + x + "," + y + "," + z +
                    ", but found: " + (stack.isEmpty() ? "empty" : stack.getItem()));
        }
    }

    /**
     * Assert that a specific slot contains a specific item.
     */
    public void assertSlotHasItem(int x, int y, int z, ResourceSlotId<ItemResource> slot, Item expectedItem) {
        ItemStack stack = getItemInSlot(x, y, z, slot);
        if (!stack.is(expectedItem)) {
            throw assertionException(
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
            throw assertionException(
                "Expected empty slot " + slot + " at " + x + "," + y + "," + z +
                    ", but found: " + stack.getItem());
        }
    }

    /**
     * Assert that a specific slot is empty.
     */
    public void assertSlotHasNoItem(int x, int y, int z, ResourceSlotId<ItemResource> slot) {
        ItemStack stack = getItemInSlot(x, y, z, slot);
        if (!stack.isEmpty()) {
            throw assertionException(
                "Expected empty slot " + slot + " at " + x + "," + y + "," + z +
                    ", but found: " + stack.getItem());
        }
    }

    // endregion

    public void fillContainer(int x, int y, int z, Fluid fluid, int amount) {
        var fluidHandler = getLevel().getCapability(Capabilities.Fluid.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (fluidHandler == null) {
            throw assertionException("No fluid handler at " + x + "," + y + "," + z);
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int filled = fluidHandler.insert(FluidResource.of(fluid), amount, transaction);
            if (filled != amount) {
                throw assertionException(
                    "Could not fill tank with all " + amount + " of the fluid into container at " + x + "," + y + "," + z);
            }

            transaction.commit();
        }
    }

    public long getAmountInHandler(int x, int y, int z, Fluid fluid) {
        var fluidHandler = getLevel().getCapability(Capabilities.Fluid.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (fluidHandler == null) {
            throw assertionException("No fluid handler at " + x + "," + y + "," + z);
        }

        long totalAmount = 0;
        for (int i = 0; i < fluidHandler.size(); i++) {
            if (fluidHandler.getResource(i).is(fluid)) {
                totalAmount += fluidHandler.getAmountAsLong(i);
            }
        }

        return totalAmount;
    }

    public void assertContainerHasExactly(int x, int y, int z, Fluid fluid, int amount) {
        var fluidHandler = getLevel().getCapability(Capabilities.Fluid.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (fluidHandler == null) {
            throw assertionException("No fluid handler at " + x + "," + y + "," + z);
        }

        long totalAmount = 0;
        for (int i = 0; i < fluidHandler.size(); i++) {
            if (fluidHandler.getResource(i).is(fluid)) {
                totalAmount += fluidHandler.getAmountAsLong(i);
            }
        }

        if (totalAmount != amount) {
            throw assertionException("Expected " + amount + " of " + fluid + " in tank at " + x + "," + y
                + "," + z + " but found " + totalAmount);
        }
    }

    /**
     * Provide energy to a block entity by directly accessing its energy capability.
     */
    public void provideEnergy(int x, int y, int z, int amount) {
        var energyHandler = getLevel().getCapability(Capabilities.Energy.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (energyHandler == null) {
            throw assertionException("No energy handler at " + x + "," + y + "," + z);
        }

        // Short path for MachineEnergyHandler to avoid insertion limits.
        if (energyHandler instanceof MachineEnergyHandler machineEnergyHandler) {
            int inserted = machineEnergyHandler.add(amount, null);
            if (inserted != amount) {
                throw assertionException("Could not insert all " + amount + " energy into block at " + x + "," + y + "," + z + ", only inserted " + inserted);
            }

            return;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = energyHandler.insert(amount, transaction);
            if (inserted != amount) {
                throw assertionException("Could not insert all " + amount + " energy into block at " + x + "," + y + "," + z + ", only inserted " + inserted);
            }

            transaction.commit();
        }
    }

    /**
     * Assert that a block has exactly the expected amount of energy stored.
     */
    public void assertEnergyStored(int x, int y, int z, int expectedAmount) {
        var energyHandler = getLevel().getCapability(Capabilities.Energy.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (energyHandler == null) {
            throw assertionException("No energy handler at " + x + "," + y + "," + z);
        }

        int stored = energyHandler.getAmountAsInt();
        if (stored != expectedAmount) {
            throw assertionException("Expected " + expectedAmount + " energy in block at " + x + "," + y + "," + z + ", but found " + stored);
        }
    }

    /**
     * Assert that a block has at least the expected minimum amount of energy stored.
     */
    public void assertEnergyStoredAtLeast(int x, int y, int z, int minAmount) {
        var energyHandler = getLevel().getCapability(Capabilities.Energy.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (energyHandler == null) {
            throw assertionException("No energy handler at " + x + "," + y + "," + z);
        }

        int stored = energyHandler.getAmountAsInt();
        if (stored < minAmount) {
            throw assertionException("Expected at least " + minAmount + " energy in block at " + x + "," + y + "," + z + ", but found " + stored);
        }
    }

    /**
     * Assert that a block has at most the expected maximum amount of energy stored.
     */
    public void assertEnergyStoredAtMost(int x, int y, int z, int maxAmount) {
        var energyHandler = getLevel().getCapability(Capabilities.Energy.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (energyHandler == null) {
            throw assertionException("No energy handler at " + x + "," + y + "," + z);
        }

        int stored = energyHandler.getAmountAsInt();
        if (stored > maxAmount) {
            throw assertionException("Expected at most " + maxAmount + " energy in block at " + x + "," + y + "," + z + ", but found " + stored);
        }
    }

    /**
     * Get the amount of energy stored in a block.
     */
    public int getEnergyStored(int x, int y, int z) {
        var energyHandler = getLevel().getCapability(Capabilities.Energy.BLOCK, absolutePos(new BlockPos(x, y, z)), null);
        if (energyHandler == null) {
            throw assertionException("No energy handler at " + x + "," + y + "," + z);
        }

        return energyHandler.getAmountAsInt();
    }
}
