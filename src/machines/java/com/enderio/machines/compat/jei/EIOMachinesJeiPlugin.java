package com.enderio.machines.compat.jei;

import com.enderio.EnderIO;
import com.enderio.base.compat.jei.categories.FireCraftingCategory;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.gui.screen.AlloySmelterScreen;
import com.enderio.machines.client.gui.screen.EnchanterScreen;
import com.enderio.machines.client.gui.screen.SagMillScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.compat.jei.categories.AlloySmeltingCategory;
import com.enderio.machines.compat.jei.categories.EnchanterCategory;
import com.enderio.machines.compat.jei.categories.SagMillCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@ParametersAreNonnullByDefault
public class EIOMachinesJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = EnderIO.loc("machines");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new AlloySmeltingCategory(guiHelper), new EnchanterCategory(guiHelper), new SagMillCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(AlloySmeltingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(MachineRecipes.ALLOY_SMELTING.type().get()));
        registration.addRecipes(EnchanterCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(MachineRecipes.ENCHANTING.type().get()));
        registration.addRecipes(SagMillCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(MachineRecipes.SAGMILLING.type().get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(MachineBlocks.ALLOY_SMELTER.asStack(), AlloySmeltingCategory.RECIPE_TYPE, RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(MachineBlocks.ENCHANTER.asStack(), EnchanterCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(MachineBlocks.PRIMITIVE_ALLOY_SMELTER.asStack(), AlloySmeltingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(MachineBlocks.SAG_MILL.asStack(), SagMillCategory.RECIPE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlloySmelterScreen.class, 78, 32, 18, 21, AlloySmeltingCategory.RECIPE_TYPE, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(EnchanterScreen.class, 37, 34, 23, 18, EnchanterCategory.RECIPE_TYPE);
        registration.addRecipeClickArea(SagMillScreen.class, 79, 32, 18, 22, SagMillCategory.RECIPE_TYPE); // TODO: Move this as it is in the way of the progress tooltip
    }
}
