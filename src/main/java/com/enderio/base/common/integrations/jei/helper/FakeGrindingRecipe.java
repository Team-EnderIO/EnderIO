package com.enderio.base.common.integrations.jei.helper;

import com.enderio.core.common.recipes.CountedIngredient;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FakeGrindingRecipe {

    public final CountedIngredient topInput;
    @Nullable
    public final CountedIngredient bottomInput;
    public final ItemStack result;

    public FakeGrindingRecipe(CountedIngredient topInput, @Nullable CountedIngredient bottomInput, ItemStack result) {
        this.topInput = topInput;
        this.bottomInput = bottomInput;
        this.result = result;
    }
}
