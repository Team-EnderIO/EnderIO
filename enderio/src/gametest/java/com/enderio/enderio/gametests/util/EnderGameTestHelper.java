package com.enderio.enderio.gametests.util;

import com.enderio.enderio.api.io.IOConfigurable;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.item.Item;
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

    public void insertIntoContainer(int x, int y, int z, Item item, int count) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)),
            null);
        if (itemHandler == null) {
            throw assertionException("No item handler at " + x + "," + y + "," + z);
        }

        try (Transaction transaction = Transaction.openRoot()) {
            var itemResource = ItemResource.of(item);
            for (int i = 0; i < itemHandler.size(); i++) {
                int toInsert = Math.min(count, itemHandler.getCapacityAsInt(i, itemResource));
                int inserted = itemHandler.insert(i, itemResource, toInsert, transaction);
                count -= inserted;
                if (count <= 0) {
                    break;
                }
            }

            if (count > 0) {
                throw assertionException("Could not insert " + count + " items into container at " + x + "," + y + "," + z);
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
}
