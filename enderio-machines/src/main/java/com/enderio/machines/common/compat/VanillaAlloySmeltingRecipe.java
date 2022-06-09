package com.enderio.machines.common.compat;

import com.enderio.api.machines.recipes.IAlloySmeltingRecipe;
import com.enderio.api.machines.recipes.OutputStack;
import com.enderio.api.recipe.CountedIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;

public class VanillaAlloySmeltingRecipe implements IAlloySmeltingRecipe {
    private final SmeltingRecipe vanillaRecipe;

    public static final int RF_PER_ITEM = ForgeHooks.getBurnTime(new ItemStack(Items.COAL, 1), RecipeType.SMELTING) * 10 / 8;

    public VanillaAlloySmeltingRecipe(SmeltingRecipe vanillaRecipe) {
        this.vanillaRecipe = vanillaRecipe;
    }

    @Override
    public List<CountedIngredient> getInputs() {
        return List.of(CountedIngredient.of(vanillaRecipe.getIngredients().get(0)));
    }

    @Override
    public float getExperience() {
        return vanillaRecipe.getExperience();
    }

    @Override
    public int getEnergyCost(IAlloySmeltingRecipe.Container container) {
        return RF_PER_ITEM * container.getInputsTaken();
    }

    // TODO: Write our own matcher that can check all three slots :)
    @Deprecated
    @Override
    public boolean matches(IAlloySmeltingRecipe.Container container, Level level) {
        return vanillaRecipe.matches(container, level);
    }

    @Override
    public List<OutputStack> craft(Container container) {
        ItemStack result = vanillaRecipe.assemble(container);
        result.setCount(result.getCount() * container.getInputsTaken());
        return List.of(OutputStack.of(result));
    }

    @Override
    public List<OutputStack> getResultStacks() {
        return List.of(OutputStack.of(vanillaRecipe.getResultItem()));
    }

    @Override
    public ResourceLocation getId() {
        return vanillaRecipe.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RecipeType<?> getType() {
        return vanillaRecipe.getType();
    }
}
