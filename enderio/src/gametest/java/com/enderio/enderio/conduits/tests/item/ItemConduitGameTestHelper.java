package com.enderio.enderio.conduits.tests.item;

import com.enderio.enderio.conduits.tests.ConduitGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ItemConduitGameTestHelper extends ConduitGameTestHelper {
    public ItemConduitGameTestHelper(GameTestInfo info) {
        super(info);
    }

    public void insertIntoContainer(int x, int y, int z, Item item, int count) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)),
                null);
        if (itemHandler == null) {
            throw helper.assertionException("No item handler at " + x + "," + y + "," + z);
        }

        try (Transaction transaction = Transaction.openRoot()) {
            var itemResource = ItemResource.of(item);
            for (int i = 0; i < itemHandler.size(); i++) {
                int toInsert = Math.min(count, itemHandler.getCapacityAsInt(i, itemResource));
                int inserted = itemHandler.insert(i, itemResource, toInsert, transaction);
                count -= inserted;
                if (count <= 0) {
                    return;
                }
            }

            if (count > 0) {
                throw helper.assertionException("Could not insert " + count + " items into container at " + x + "," + y + "," + z);
            }

            transaction.commit();
        }
    }

    public void assertContainerHasExactly(int x, int y, int z, Item item, int count) {
        var itemHandler = getLevel().getCapability(Capabilities.Item.BLOCK, absolutePos(new BlockPos(x, y, z)),
                null);
        if (itemHandler == null) {
            throw helper.assertionException("No item handler at " + x + "," + y + "," + z);
        }

        long foundCount = 0;
        for (int i = 0; i < itemHandler.size(); i++) {
            if (itemHandler.getResource(i).is(item)) {
                foundCount += itemHandler.getAmountAsLong(i);
            }
        }

        if (foundCount != count) {
            throw helper.assertionException("Expected " + count + " of " + item + " in container at " + x + "," + y
                    + "," + z + " but found " + foundCount);
        }
    }
}
