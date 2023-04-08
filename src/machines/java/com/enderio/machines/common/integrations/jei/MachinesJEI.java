package com.enderio.machines.common.integrations.jei;

import com.enderio.EnderIO;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.integrations.jei.category.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class MachinesJEI implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return EnderIO.loc("machines");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.PRIMITIVE_ALLOY_SMELTER.get()), PrimitiveAlloySmeltingCategory.TYPE, RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.ALLOY_SMELTER.get()), AlloySmeltingCategory.TYPE, RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.ENCHANTER.get()), EnchanterCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SAG_MILL.get()), SagMillCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SLICE_AND_SPLICE.get()), SlicingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.THE_VAT.get()), VattingCategory.TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AlloySmeltingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new EnchanterCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new PrimitiveAlloySmeltingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SagMillCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SlicingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new VattingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        MachineJEIRecipes recipes = new MachineJEIRecipes();

        registration.addRecipes(AlloySmeltingCategory.TYPE, recipes.getAlloySmeltingRecipes());
        registration.addRecipes(EnchanterCategory.TYPE, recipes.getEnchanterRecipes());
        registration.addRecipes(PrimitiveAlloySmeltingCategory.TYPE, recipes.getAlloySmeltingRecipes());
        registration.addRecipes(SagMillCategory.TYPE, recipes.getSagmillingRecipes());
        registration.addRecipes(SlicingRecipeCategory.TYPE, recipes.getSlicingRecipes());
        registration.addRecipes(VattingCategory.TYPE, recipes.getVattingRecipes());
    }
}
