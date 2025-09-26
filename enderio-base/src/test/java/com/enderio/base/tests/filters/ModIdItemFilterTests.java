package com.enderio.base.tests.filters;

import com.enderio.base.common.filter.item.mod_id.ModIdItemFilter;
import com.enderio.base.common.init.EIOItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModIdItemFilterTests {
    @Test
    public void testBasicAllowFilter() {
        var filter = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false);

        Assertions.assertFalse(filter.test(null, new ItemStack(Items.SAND, 1)).isEmpty());
        Assertions.assertFalse(filter.test(null, new ItemStack(Items.GRASS_BLOCK, 1)).isEmpty());
        Assertions.assertTrue(filter.test(null, new ItemStack(EIOItems.BASIC_CAPACITOR.get(), 1)).isEmpty());
        Assertions.assertTrue(filter.test(null, new ItemStack(EIOItems.GRAINS_OF_INFINITY.get(), 1)).isEmpty());
    }

    @Test
    public void testBasicDenyFilter() {
        var filter = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), true);

        Assertions.assertTrue(filter.test(null, new ItemStack(Items.SAND, 1)).isEmpty());
        Assertions.assertTrue(filter.test(null, new ItemStack(Items.GRASS_BLOCK, 1)).isEmpty());
        Assertions.assertFalse(filter.test(null, new ItemStack(EIOItems.BASIC_CAPACITOR.get(), 1)).isEmpty());
        Assertions.assertFalse(filter.test(null, new ItemStack(EIOItems.GRAINS_OF_INFINITY.get(), 1)).isEmpty());
    }
}
