package com.enderio.enderio.tests.filters;

import com.enderio.enderio.content.filters.item.general.DamageFilterMode;
import com.enderio.enderio.content.filters.item.general.EnderItemFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnderItemFilterTests {
    @Test
    public void testBasicAllowFilter() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IGNORE);

        Assertions.assertFalse(filter.test(null, new ItemStack(Items.SAND, 1)).isEmpty());
        Assertions.assertTrue(filter.test(null, new ItemStack(Items.COBBLESTONE, 1)).isEmpty());
        Assertions.assertTrue(filter.test(null, new ItemStack(Items.GRASS_BLOCK, 1)).isEmpty());
    }

    @Test
    public void testBasicDenyFilter() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), true, false, DamageFilterMode.IGNORE);

        Assertions.assertTrue(filter.test(null, new ItemStack(Items.SAND, 1)).isEmpty());
        Assertions.assertFalse(filter.test(null, new ItemStack(Items.COBBLESTONE, 1)).isEmpty());
        Assertions.assertFalse(filter.test(null, new ItemStack(Items.GRASS_BLOCK, 1)).isEmpty());
    }

    @Test
    public void testBasicAllowFilterWithComponentComparison() {
        var filterStack = new ItemStack(Items.SAND, 1);
        filterStack.set(DataComponents.RARITY, Rarity.UNCOMMON);
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, filterStack), false, true, DamageFilterMode.IGNORE);

        var testStack1 = new ItemStack(Items.SAND, 1000);
        testStack1.set(DataComponents.RARITY, Rarity.UNCOMMON);
        Assertions.assertFalse(filter.test(null, testStack1).isEmpty());

        var testStack2 = new ItemStack(Items.SAND, 1000);
        testStack2.set(DataComponents.RARITY, Rarity.COMMON);
        Assertions.assertTrue(filter.test(null, testStack2).isEmpty());

        var testStack3 = new ItemStack(Items.SAND, 1000);
        Assertions.assertTrue(filter.test(null, testStack3).isEmpty());

        var testStack4 = new ItemStack(Items.COBBLESTONE, 1000);
        testStack4.set(DataComponents.RARITY, Rarity.COMMON);
        Assertions.assertTrue(filter.test(null, testStack4).isEmpty());
    }

    @Test
    public void testDamageFilterIsDamageable() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), true, true, DamageFilterMode.IS_DAMAGEABLE);

        Assertions.assertFalse(filter.test(null, new ItemStack(Items.STONE_SWORD, 1)).isEmpty());
        Assertions.assertFalse(filter.test(null, new ItemStack(Items.DIAMOND_PICKAXE, 1)).isEmpty());
        Assertions.assertTrue(filter.test(null, new ItemStack(Items.SAND, 1)).isEmpty());
        Assertions.assertTrue(filter.test(null, new ItemStack(Items.CLAY_BALL, 1)).isEmpty());
    }

    @Test
    public void testDamageFilterNotDamageable() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), true, true, DamageFilterMode.NOT_DAMAGEABLE);

        Assertions.assertTrue(filter.test(null, new ItemStack(Items.STONE_SWORD, 1)).isEmpty());
        Assertions.assertTrue(filter.test(null, new ItemStack(Items.DIAMOND_PICKAXE, 1)).isEmpty());
        Assertions.assertFalse(filter.test(null, new ItemStack(Items.SAND, 1)).isEmpty());
        Assertions.assertFalse(filter.test(null, new ItemStack(Items.CLAY_BALL, 1)).isEmpty());
    }

    @Test
    public void testDamageFilterUpTo25() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), true, true, DamageFilterMode.UP_TO_25);

        var swordNoDamage = new ItemStack(Items.DIAMOND_SWORD, 1);
        var sword1Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword1Damage.setDamageValue(sword1Damage.getMaxDamage() / 100);
        var sword25Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword25Damage.setDamageValue(sword25Damage.getMaxDamage() / 100 * 25);
        var sword50Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword50Damage.setDamageValue(sword50Damage.getMaxDamage() / 100 * 50);
        var sword75Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword75Damage.setDamageValue(sword75Damage.getMaxDamage() / 100 * 75);
        var sword99Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword99Damage.setDamageValue(sword99Damage.getMaxDamage() / 100 * 99);

        Assertions.assertFalse(filter.test(null, swordNoDamage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword1Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword25Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword50Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword75Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword99Damage).isEmpty());
    }

    @Test
    public void testDamageFilterMoreThan25() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), true, true, DamageFilterMode.MORE_THAN_25);

        var swordNoDamage = new ItemStack(Items.DIAMOND_SWORD, 1);
        var sword1Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword1Damage.setDamageValue(sword1Damage.getMaxDamage() / 100);
        var sword25Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword25Damage.setDamageValue(sword25Damage.getMaxDamage() / 100 * 25);
        var sword50Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword50Damage.setDamageValue(sword50Damage.getMaxDamage() / 100 * 50);
        var sword75Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword75Damage.setDamageValue(sword75Damage.getMaxDamage() / 100 * 75);
        var sword99Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword99Damage.setDamageValue(sword99Damage.getMaxDamage() / 100 * 99);

        Assertions.assertTrue(filter.test(null, swordNoDamage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword1Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword25Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword50Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword75Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword99Damage).isEmpty());
    }

    @Test
    public void testDamageFilterUpTo250() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), true, true, DamageFilterMode.UP_TO_50);

        var swordNoDamage = new ItemStack(Items.DIAMOND_SWORD, 1);
        var sword1Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword1Damage.setDamageValue(sword1Damage.getMaxDamage() / 100);
        var sword25Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword25Damage.setDamageValue(sword25Damage.getMaxDamage() / 100 * 25);
        var sword50Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword50Damage.setDamageValue(sword50Damage.getMaxDamage() / 100 * 50);
        var sword75Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword75Damage.setDamageValue(sword75Damage.getMaxDamage() / 100 * 75);
        var sword99Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword99Damage.setDamageValue(sword99Damage.getMaxDamage() / 100 * 99);

        Assertions.assertFalse(filter.test(null, swordNoDamage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword1Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword25Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword50Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword75Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword99Damage).isEmpty());
    }

    @Test
    public void testDamageFilterMoreThan50() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), true, true, DamageFilterMode.MORE_THAN_50);

        var swordNoDamage = new ItemStack(Items.DIAMOND_SWORD, 1);
        var sword1Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword1Damage.setDamageValue(sword1Damage.getMaxDamage() / 100);
        var sword25Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword25Damage.setDamageValue(sword25Damage.getMaxDamage() / 100 * 25);
        var sword50Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword50Damage.setDamageValue(sword50Damage.getMaxDamage() / 100 * 50);
        var sword75Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword75Damage.setDamageValue(sword75Damage.getMaxDamage() / 100 * 75);
        var sword99Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword99Damage.setDamageValue(sword99Damage.getMaxDamage() / 100 * 99);

        Assertions.assertTrue(filter.test(null, swordNoDamage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword1Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword25Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword50Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword75Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword99Damage).isEmpty());
    }

    @Test
    public void testDamageFilterUpTo75() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), true, true, DamageFilterMode.UP_TO_75);

        var swordNoDamage = new ItemStack(Items.DIAMOND_SWORD, 1);
        var sword1Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword1Damage.setDamageValue(sword1Damage.getMaxDamage() / 100);
        var sword25Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword25Damage.setDamageValue(sword25Damage.getMaxDamage() / 100 * 25);
        var sword50Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword50Damage.setDamageValue(sword50Damage.getMaxDamage() / 100 * 50);
        var sword75Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword75Damage.setDamageValue(sword75Damage.getMaxDamage() / 100 * 75);
        var sword99Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword99Damage.setDamageValue(sword99Damage.getMaxDamage() / 100 * 99);

        Assertions.assertFalse(filter.test(null, swordNoDamage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword1Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword25Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword50Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword75Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword99Damage).isEmpty());
    }

    @Test
    public void testDamageFilterMoreThan75() {
        var filter = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), true, true, DamageFilterMode.MORE_THAN_75);

        var swordNoDamage = new ItemStack(Items.DIAMOND_SWORD, 1);
        var sword1Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword1Damage.setDamageValue(sword1Damage.getMaxDamage() / 100);
        var sword25Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword25Damage.setDamageValue(sword25Damage.getMaxDamage() / 100 * 25);
        var sword50Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword50Damage.setDamageValue(sword50Damage.getMaxDamage() / 100 * 50);
        var sword75Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword75Damage.setDamageValue(sword75Damage.getMaxDamage() / 100 * 75);
        var sword99Damage = new ItemStack(Items.DIAMOND_SWORD, 1);
        sword99Damage.setDamageValue(sword99Damage.getMaxDamage() / 100 * 99);

        Assertions.assertTrue(filter.test(null, swordNoDamage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword1Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword25Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword50Damage).isEmpty());
        Assertions.assertTrue(filter.test(null, sword75Damage).isEmpty());
        Assertions.assertFalse(filter.test(null, sword99Damage).isEmpty());
    }

    @Test
    public void testEqualsSameValues() {
        var filter1 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IGNORE);
        var filter2 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IGNORE);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void testEqualsSameInstance() {
        var filter1 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IGNORE);

        Assertions.assertEquals(filter1, filter1);
        Assertions.assertEquals(filter1.hashCode(), filter1.hashCode());
    }

    @Test
    public void testNotEqualsDifferentMatches() {
        var filter1 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IGNORE);
        var filter2 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.COBBLESTONE, 1)), false, false, DamageFilterMode.IGNORE);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentIsDenyList() {
        var filter1 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IGNORE);
        var filter2 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), true, false, DamageFilterMode.IGNORE);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentShouldCompareComponents() {
        var filter1 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IGNORE);
        var filter2 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, true, DamageFilterMode.IGNORE);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentDamageFilterMode() {
        var filter1 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IGNORE);
        var filter2 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IS_DAMAGEABLE);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentMatchCount() {
        var filter1 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1)), false, false, DamageFilterMode.IGNORE);
        var filter2 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.SAND, 1), new ItemStack(Items.DIRT, 1)), false, false, DamageFilterMode.IGNORE);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testEmptyListEquals() {
        var filter1 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), false, false, DamageFilterMode.IGNORE);
        var filter2 = new EnderItemFilter(NonNullList.of(ItemStack.EMPTY), false, false, DamageFilterMode.IGNORE);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }
}
