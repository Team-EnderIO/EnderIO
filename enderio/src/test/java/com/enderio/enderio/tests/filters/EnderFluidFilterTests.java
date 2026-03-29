package com.enderio.enderio.tests.filters;

import com.enderio.enderio.content.filters.fluid.EnderFluidFilter;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnderFluidFilterTests {
    @Test
    public void testBasicAllowFilter() {
        var filter = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), false, false);

        Assertions.assertFalse(filter.test(null, new FluidStack(Fluids.WATER, 1000)).isEmpty());
        Assertions.assertTrue(filter.test(null, new FluidStack(Fluids.LAVA, 1000)).isEmpty());
        Assertions.assertTrue(filter.test(null, new FluidStack(EIOFluids.HOOTCH.source(), 1000)).isEmpty());
    }

    @Test
    public void testBasicDenyFilter() {
        var filter = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), true, false);

        Assertions.assertTrue(filter.test(null, new FluidStack(Fluids.WATER, 1000)).isEmpty());
        Assertions.assertFalse(filter.test(null, new FluidStack(Fluids.LAVA, 1000)).isEmpty());
        Assertions.assertFalse(filter.test(null, new FluidStack(EIOFluids.HOOTCH.source(), 1000)).isEmpty());
    }

    @Test
    public void testBasicAllowFilterWithComponentComparison() {
        var filterStack = new FluidStack(Fluids.WATER, 1000);
        filterStack.set(DataComponents.RARITY, Rarity.UNCOMMON);
        var filter = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, filterStack), false, true);

        var testStack1 = new FluidStack(Fluids.WATER, 1000);
        testStack1.set(DataComponents.RARITY, Rarity.UNCOMMON);
        Assertions.assertFalse(filter.test(null, testStack1).isEmpty());

        var testStack2 = new FluidStack(Fluids.WATER, 1000);
        testStack2.set(DataComponents.RARITY, Rarity.COMMON);
        Assertions.assertTrue(filter.test(null, testStack2).isEmpty());

        var testStack3 = new FluidStack(Fluids.WATER, 1000);
        Assertions.assertTrue(filter.test(null, testStack3).isEmpty());

        var testStack4 = new FluidStack(Fluids.LAVA, 1000);
        testStack4.set(DataComponents.RARITY, Rarity.COMMON);
        Assertions.assertTrue(filter.test(null, testStack4).isEmpty());
    }

    @Test
    public void testEqualsSameValues() {
        var filter1 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), false, false);
        var filter2 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), false, false);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void testEqualsSameInstance() {
        var filter1 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), false, false);

        Assertions.assertEquals(filter1, filter1);
        Assertions.assertEquals(filter1.hashCode(), filter1.hashCode());
    }

    @Test
    public void testNotEqualsDifferentMatches() {
        var filter1 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), false, false);
        var filter2 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.LAVA, 1000)), false, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentIsDenyList() {
        var filter1 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), false, false);
        var filter2 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), true, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentShouldCompareComponents() {
        var filter1 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), false, false);
        var filter2 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), false, true);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentMatchCount() {
        var filter1 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000)), false, false);
        var filter2 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 1000)), false, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testEmptyListEquals() {
        var filter1 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, FluidStack.EMPTY), false, false);
        var filter2 = new EnderFluidFilter(NonNullList.of(FluidStack.EMPTY, FluidStack.EMPTY), false, false);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }
}
