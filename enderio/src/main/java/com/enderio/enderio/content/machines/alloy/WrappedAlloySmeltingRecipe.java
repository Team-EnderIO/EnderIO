package com.enderio.enderio.content.machines.alloy;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class WrappedAlloySmeltingRecipe extends AlloySmeltingRecipe{
    private final SmeltingRecipe parent;

    public WrappedAlloySmeltingRecipe(SmeltingRecipe parent, int energy) {
        super(List.of(), null, energy, 0, true);
        this.parent = parent;
    }

    @Override
    public List<SizedIngredient> inputs() {
        return List.of(new SizedIngredient(parent.input(), 1));
    }

    @Override
    public ItemStackTemplate output() {
        return parent.result();
    }

    @Override
    public float experience() {
        return parent.experience();
    }
}
