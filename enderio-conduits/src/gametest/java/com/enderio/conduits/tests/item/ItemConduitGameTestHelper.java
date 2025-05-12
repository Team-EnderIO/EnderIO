package com.enderio.conduits.tests.item;

import com.enderio.conduits.tests.ConduitGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

public class ItemConduitGameTestHelper extends ConduitGameTestHelper {
    public ItemConduitGameTestHelper(GameTestInfo info) {
        super(info);
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
}
