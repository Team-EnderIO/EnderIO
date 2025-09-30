package com.enderio.enderio.tests.filters;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.common.filter.soul.EnderSoulFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnderSoulFilterTests {
    @Test
    public void testBasicAllowFilter() {
        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityType.ALLAY)), false, false);

        Assertions.assertTrue(filter.test(Soul.of(EntityType.ALLAY)));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.COD)));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.SHEEP)));
    }

    @Test
    public void testBasicDenyFilter() {
        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, Soul.of(EntityType.ALLAY)), true, false);

        Assertions.assertFalse(filter.test(Soul.of(EntityType.ALLAY)));
        Assertions.assertTrue(filter.test(Soul.of(EntityType.COD)));
        Assertions.assertTrue(filter.test(Soul.of(EntityType.SHEEP)));
    }

    @Test
    public void testBasicAllowFilterWithComponentComparison() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Health", 20);
        var soulWithHealth = new Soul(EntityType.ALLAY, tag);

        var filter = new EnderSoulFilter(NonNullList.of(Soul.EMPTY, soulWithHealth), false, true);

        Assertions.assertTrue(filter.test(soulWithHealth));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.ALLAY)));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.COD)));
        Assertions.assertFalse(filter.test(Soul.of(EntityType.SHEEP)));
    }
}
