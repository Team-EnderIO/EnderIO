package com.enderio.enderio.tests.filters;

import com.enderio.enderio.content.filters.item.mod_id.ModIdItemFilter;
import com.enderio.enderio.init.EIOItems;
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

    @Test
    public void testEqualsSameValues() {
        var filter1 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false);
        var filter2 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void testEqualsSameInstance() {
        var filter1 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false);

        Assertions.assertEquals(filter1, filter1);
        Assertions.assertEquals(filter1.hashCode(), filter1.hashCode());
    }

    @Test
    public void testNotEqualsDifferentExamples() {
        var filter1 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false);
        var filter2 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.COBBLESTONE, 1)), false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentIsDenyList() {
        var filter1 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false);
        var filter2 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), true);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentExampleCount() {
        var filter1 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false);
        var filter2 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1), new ItemStack(Items.DIRT, 1)), false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testEmptyListEquals() {
        var filter1 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY), false);
        var filter2 = new ModIdItemFilter(NonNullList.of(ItemStack.EMPTY), false);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }
}
