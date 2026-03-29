package com.enderio.enderio.tests.filters;

import com.enderio.enderio.content.filters.item.general.DamageFilterMode;
import com.enderio.enderio.content.filters.item.limited.LimitedItemFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LimitedItemFilterTests {
    @Test
    public void testEqualsSameValues() {
        var filter1 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, DamageFilterMode.IGNORE);
        var filter2 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, DamageFilterMode.IGNORE);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void testEqualsSameInstance() {
        var filter1 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, DamageFilterMode.IGNORE);

        Assertions.assertEquals(filter1, filter1);
        Assertions.assertEquals(filter1.hashCode(), filter1.hashCode());
    }

    @Test
    public void testNotEqualsDifferentMatches() {
        var filter1 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, DamageFilterMode.IGNORE);
        var filter2 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.COBBLESTONE, 1)), false, DamageFilterMode.IGNORE);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentShouldCompareComponents() {
        var filter1 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, DamageFilterMode.IGNORE);
        var filter2 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), true, DamageFilterMode.IGNORE);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentDamageFilterMode() {
        var filter1 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, DamageFilterMode.IGNORE);
        var filter2 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, DamageFilterMode.IS_DAMAGEABLE);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentMatchCount() {
        var filter1 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, DamageFilterMode.IGNORE);
        var filter2 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1), new ItemStack(Items.DIRT, 1)), false, DamageFilterMode.IGNORE);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testEmptyListEquals() {
        var filter1 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY), false, DamageFilterMode.IGNORE);
        var filter2 = new LimitedItemFilter(NonNullList.of(ItemStack.EMPTY), false, DamageFilterMode.IGNORE);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }
}
