package com.enderio.modded_conduits.tests.filters;

import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilter;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.NonNullList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for EnderChemicalFilter equals and hashCode methods.
 */
public class EnderChemicalFilterTests {

    /**
     * Helper method to create a list of empty chemical stacks.
     */
    private NonNullList<ChemicalStack> createEmptyMatches(int size) {
        NonNullList<ChemicalStack> list = NonNullList.create();
        for (int i = 0; i < size; i++) {
            list.add(ChemicalStack.EMPTY);
        }
        return list;
    }

    @Test
    public void testEqualsSameValues() {
        // Use the EMPTY constant which should be the same
        var filter1 = EnderChemicalFilter.EMPTY;
        var filter2 = EnderChemicalFilter.EMPTY;

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void testEqualsSameInstance() {
        var matches = createEmptyMatches(2);
        var filter = new EnderChemicalFilter(matches, false);

        Assertions.assertEquals(filter, filter);
        Assertions.assertEquals(filter.hashCode(), filter.hashCode());
    }

    @Test
    public void testNotEqualsDifferentMatches() {
        var matches1 = createEmptyMatches(2);
        var matches2 = createEmptyMatches(3);
        var filter1 = new EnderChemicalFilter(matches1, false);
        var filter2 = new EnderChemicalFilter(matches2, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentIsDenyList() {
        var matches = createEmptyMatches(2);
        var filter1 = new EnderChemicalFilter(matches, false);
        var filter2 = new EnderChemicalFilter(matches, true);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testNotEqualsDifferentMatchCount() {
        var matches1 = createEmptyMatches(2);
        var matches2 = createEmptyMatches(3);
        var filter1 = new EnderChemicalFilter(matches1, false);
        var filter2 = new EnderChemicalFilter(matches2, false);

        Assertions.assertNotEquals(filter1, filter2);
    }

    @Test
    public void testEmptyListEquals() {
        var matches = createEmptyMatches(2);
        var filter1 = new EnderChemicalFilter(matches, false);
        var filter2 = new EnderChemicalFilter(matches, false);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void testEmptyFilterEquals() {
        var filter1 = new EnderChemicalFilter(0);
        var filter2 = new EnderChemicalFilter(0);

        Assertions.assertEquals(filter1, filter2);
        Assertions.assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    public void testNotEqualsDifferentSizeEmptyFilters() {
        var filter1 = new EnderChemicalFilter(1);
        var filter2 = new EnderChemicalFilter(2);

        Assertions.assertNotEquals(filter1, filter2);
    }
}
