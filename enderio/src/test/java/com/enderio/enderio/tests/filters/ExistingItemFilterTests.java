package com.enderio.enderio.tests.filters;

import com.enderio.enderio.content.filters.item.existing.ExistingItemFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExistingItemFilterTests {
    @Test
    public void testEqualsSameValues() {
        var filter1 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false);
        var filter2 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void testEqualsSameInstance() {
        var filter1 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false);

        Assertions.assertEquals(filter1, filter1);
        Assertions.assertEquals(filter1.hashCode(), filter1.hashCode());
    }

    @Test
    public void testNotEqualsDifferentHasSnapshot() {
        var filter1 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false);
        var filter2 = new ExistingItemFilter(false, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentSnapshot() {
        var filter1 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false);
        var filter2 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.COBBLESTONE, 1)), false, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentShouldCompareComponents() {
        var filter1 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false);
        var filter2 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), true, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentIsInverted() {
        var filter1 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false);
        var filter2 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, true);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentSnapshotCount() {
        var filter1 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false);
        var filter2 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1), new ItemStack(Items.DIRT, 1)), false, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testEmptyListEquals() {
        var filter1 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, ItemStack.EMPTY), false, false);
        var filter2 = new ExistingItemFilter(true, NonNullList.of(ItemStack.EMPTY, ItemStack.EMPTY), false, false);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }
}
