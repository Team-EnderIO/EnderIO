package com.enderio.machines.compat.jei;

import com.enderio.EnderIO;
import com.enderio.base.compat.jei.categories.FireCraftingCategory;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.compat.jei.categories.AlloySmeltingCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@ParametersAreNonnullByDefault
public class EIOMachinesJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = EnderIO.loc("machines");

    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();

        registration.addRecipeCategories(new AlloySmeltingCategory(jeiHelpers.getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(AlloySmeltingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(MachineRecipes.ALLOY_SMELTING.type().get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(MachineBlocks.ALLOY_SMELTER.asStack(), AlloySmeltingCategory.RECIPE_TYPE, RecipeTypes.SMELTING);
    }
}
