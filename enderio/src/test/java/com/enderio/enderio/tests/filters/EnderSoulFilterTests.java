package com.enderio.enderio.tests.filters;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.filters.soul.EnderSoulFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnderSoulFilterTests {
    @Test
    public void testBasicAllowFilter() {
        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY), Soul.of(EntityTypes.SKELETON)), false, false);

        Assertions.assertTrue(filter.test(Soul.of(EntityTypes.ALLAY)));
        Assertions.assertTrue(filter.test(Soul.of(EntityTypes.SKELETON)));
        Assertions.assertFalse(filter.test(Soul.of(EntityTypes.COD)));
        Assertions.assertFalse(filter.test(Soul.of(EntityTypes.SHEEP)));
    }

    @Test
    public void testBasicDenyFilter() {
        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY), Soul.of(EntityTypes.SKELETON)), true, false);

        Assertions.assertFalse(filter.test(Soul.of(EntityTypes.ALLAY)));
        Assertions.assertFalse(filter.test(Soul.of(EntityTypes.SKELETON)));
        Assertions.assertTrue(filter.test(Soul.of(EntityTypes.COD)));
        Assertions.assertTrue(filter.test(Soul.of(EntityTypes.SHEEP)));
    }

    @Test
    public void testBasicAllowFilterWithComponentComparison() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Health", 20);
        var soulWithHealth = new Soul(EntityTypes.ALLAY, tag);
        var anotherSoulWithHealth = new Soul(EntityTypes.SKELETON, tag);

        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, soulWithHealth, anotherSoulWithHealth), false, true);

        Assertions.assertTrue(filter.test(soulWithHealth));
        Assertions.assertFalse(filter.test(Soul.of(EntityTypes.ALLAY)));
        Assertions.assertTrue(filter.test(anotherSoulWithHealth));
        Assertions.assertFalse(filter.test(Soul.of(EntityTypes.SKELETON)));
        Assertions.assertFalse(filter.test(Soul.of(EntityTypes.COD)));
        Assertions.assertFalse(filter.test(Soul.of(EntityTypes.SHEEP)));
    }

    @Test
    public void testBasicDenyFilterWithComponentComparison() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Health", 20);
        var soulWithHealth = new Soul(EntityTypes.ALLAY, tag);
        var anotherSoulWithHealth = new Soul(EntityTypes.SKELETON, tag);

        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, soulWithHealth, anotherSoulWithHealth), true, true);

        Assertions.assertFalse(filter.test(soulWithHealth));
        Assertions.assertTrue(filter.test(Soul.of(EntityTypes.ALLAY)));
        Assertions.assertFalse(filter.test(anotherSoulWithHealth));
        Assertions.assertTrue(filter.test(Soul.of(EntityTypes.SKELETON)));
        Assertions.assertTrue(filter.test(Soul.of(EntityTypes.COD)));
        Assertions.assertTrue(filter.test(Soul.of(EntityTypes.SHEEP)));
    }

    @Test
    public void testEqualsSameValues() {
        var filter1 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY), Soul.of(EntityTypes.SKELETON)), false, false);
        var filter2 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY), Soul.of(EntityTypes.SKELETON)), false, false);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void testEqualsSameInstance() {
        var filter1 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY)), false, false);

        Assertions.assertEquals(filter1, filter1);
        Assertions.assertEquals(filter1.hashCode(), filter1.hashCode());
    }

    @Test
    public void testNotEqualsDifferentMatches() {
        var filter1 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY)), false, false);
        var filter2 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.SKELETON)), false, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentIsDenyList() {
        var filter1 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY)), false, false);
        var filter2 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY)), true, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentShouldCompareTags() {
        var filter1 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY)), false, false);
        var filter2 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY)), false, true);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentMatchCount() {
        var filter1 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY)), false, false);
        var filter2 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityTypes.ALLAY), Soul.of(EntityTypes.SKELETON)), false, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testEmptyListEquals() {
        var filter1 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY), false, false);
        var filter2 = new EnderSoulFilter(NonNullList.of(Soul.EMPTY), false, false);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }
}
