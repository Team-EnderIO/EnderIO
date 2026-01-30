package com.enderio.enderio.tests.filters;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.filters.soul.EnderSoulFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnderSoulFilterTests {
    @Test
    public void testBasicAllowFilter() {
        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityType.ALLAY), Soul.of(EntityType.SKELETON)), false, false);

        Assertions.assertTrue(filter.test(Soul.of(EntityType.ALLAY)));
        Assertions.assertTrue(filter.test(Soul.of(EntityType.SKELETON)));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.COD)));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.SHEEP)));
    }

    @Test
    public void testBasicDenyFilter() {
        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityType.ALLAY), Soul.of(EntityType.SKELETON)), true, false);

        Assertions.assertFalse(filter.test(Soul.of(EntityType.ALLAY)));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.SKELETON)));
        Assertions.assertTrue(filter.test(Soul.of(EntityType.COD)));
        Assertions.assertTrue(filter.test(Soul.of(EntityType.SHEEP)));
    }

    @Test
    public void testBasicAllowFilterWithComponentComparison() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Health", 20);
        var soulWithHealth = new Soul(EntityType.ALLAY, tag);
        var anotherSoulWithHealth = new Soul(EntityType.SKELETON, tag);

        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, soulWithHealth, anotherSoulWithHealth), false, true);

        Assertions.assertTrue(filter.test(soulWithHealth));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.ALLAY)));
        Assertions.assertTrue(filter.test(anotherSoulWithHealth));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.SKELETON)));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.COD)));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.SHEEP)));
    }

    @Test
    public void testBasicDenyFilterWithComponentComparison() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Health", 20);
        var soulWithHealth = new Soul(EntityType.ALLAY, tag);
        var anotherSoulWithHealth = new Soul(EntityType.SKELETON, tag);

        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, soulWithHealth, anotherSoulWithHealth), true, true);

        Assertions.assertFalse(filter.test(soulWithHealth));
        Assertions.assertTrue(filter.test(Soul.of(EntityType.ALLAY)));
        Assertions.assertFalse(filter.test(anotherSoulWithHealth));
        Assertions.assertTrue(filter.test(Soul.of(EntityType.SKELETON)));
        Assertions.assertTrue(filter.test(Soul.of(EntityType.COD)));
        Assertions.assertTrue(filter.test(Soul.of(EntityType.SHEEP)));
    }
}
