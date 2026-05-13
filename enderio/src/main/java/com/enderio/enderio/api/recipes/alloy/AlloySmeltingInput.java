package com.enderio.enderio.api.recipes.alloy;

import com.enderio.enderio.content.machines.alloy.AlloySmelterMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;
import java.util.Objects;

public record AlloySmeltingInput(AlloySmelterMode mode, List<ItemStack> inputs) implements RecipeInput {

    @Override
    public ItemStack getItem(int slotIndex) {
        Objects.checkIndex(slotIndex, inputs.size());
        return inputs.get(slotIndex);
    }

    @Override
    public int size() {
        return inputs.size();
    }
}
