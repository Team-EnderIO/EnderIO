package com.enderio.enderio.tests.foundation.io;

import com.enderio.enderio.foundation.io.TransferUtil;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransferUtilTests {

    // region Items

    @Test
    public void distributeItems_Push() {
        // Arrange.
        ItemStacksResourceHandler from = new ItemStacksResourceHandler(1);
        ItemStacksResourceHandler to = new ItemStacksResourceHandler(1);

        from.set(0, ItemResource.of(Items.GRASS_BLOCK), 3);

        // Act.
        TransferUtil.distributeItems(true, false, from, to);

        // Assert.
        Assertions.assertEquals(0, from.getAmountAsInt(0));
        Assertions.assertEquals(Items.GRASS_BLOCK, to.getResource(0).getItem());
        Assertions.assertEquals(3, to.getAmountAsInt(0));
    }

    @Test
    public void distributeItems_PushMultipleSlotsOverflows() {
        // Arrange.
        ItemStacksResourceHandler from = new ItemStacksResourceHandler(1);
        ItemStacksResourceHandler to = new ItemStacksResourceHandler(2);

        from.set(0, ItemResource.of(Items.GRASS_BLOCK), 3);
        to.set(0, ItemResource.of(Items.GRASS_BLOCK), 63);

        // Act.
        TransferUtil.distributeItems(true, false, from, to);

        // Assert.
        Assertions.assertEquals(0, from.getAmountAsInt(0));
        Assertions.assertEquals(Items.GRASS_BLOCK, to.getResource(0).getItem());
        Assertions.assertEquals(64, to.getAmountAsInt(0));
        Assertions.assertEquals(Items.GRASS_BLOCK, to.getResource(1).getItem());
        Assertions.assertEquals(2, to.getAmountAsInt(1));
    }

    @Test
    public void distributeItems_Pull() {
        // Arrange.
        ItemStacksResourceHandler from = new ItemStacksResourceHandler(1);
        ItemStacksResourceHandler to = new ItemStacksResourceHandler(1);

        to.set(0, ItemResource.of(Items.GRASS_BLOCK), 3);

        // Act.
        TransferUtil.distributeItems(false, true, from, to);

        // Assert.
        Assertions.assertEquals(0, to.getAmountAsInt(0));
        Assertions.assertEquals(Items.GRASS_BLOCK, from.getResource(0).getItem());
        Assertions.assertEquals(3, from.getAmountAsInt(0));
    }

    @Test
    public void distributeItems_PullMultipleSlotsOverflows() {
        // Arrange.
        ItemStacksResourceHandler from = new ItemStacksResourceHandler(2);
        ItemStacksResourceHandler to = new ItemStacksResourceHandler(1);

        from.set(0, ItemResource.of(Items.GRASS_BLOCK), 63);
        to.set(0, ItemResource.of(Items.GRASS_BLOCK), 3);

        // Act.
        TransferUtil.distributeItems(false, true, from, to);

        // Assert.
        Assertions.assertEquals(0, to.getAmountAsInt(0));
        Assertions.assertEquals(Items.GRASS_BLOCK, from.getResource(0).getItem());
        Assertions.assertEquals(64, from.getAmountAsInt(0));
        Assertions.assertEquals(Items.GRASS_BLOCK, from.getResource(1).getItem());
        Assertions.assertEquals(2, from.getAmountAsInt(1));
    }

    // endregion
}
