package com.enderio.enderio.content.filters.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class ItemFilterUtils {
    // Ignore damage as it is controlled with the damage filter.
    private static final List<DataComponentType<?>> IGNORED_COMPONENT_TYPES = List.of(DataComponents.DAMAGE);

    public static boolean doComponentsMatch(ItemStack referenceStack, ItemStack stack) {
        for (var component : referenceStack.getComponents()) {
            if (IGNORED_COMPONENT_TYPES.contains(component.type())) {
                continue;
            }

            if (!Objects.equals(stack.get(component.type()), component.value())) {
                return false;
            }
        }

        // Ensure no additional components are present
        for (var component : stack.getComponents()) {
            if (IGNORED_COMPONENT_TYPES.contains(component.type())) {
                continue;
            }

            if (!Objects.equals(referenceStack.get(component.type()), component.value())) {
                return false;
            }
        }

        return true;
    }
}
