package com.enderio.base.common.integrations.jei.helper;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

public class FakeGrindingRecipe {

    public final SizedIngredient topInput;
    @Nullable
    public final SizedIngredient bottomInput;
    public final ItemStack result;

    public FakeGrindingRecipe(SizedIngredient topInput, @Nullable SizedIngredient bottomInput, ItemStack result) {
        this.topInput = topInput;
        this.bottomInput = bottomInput;
        this.result = result;
    }
}
