package com.enderio.base.integration.jei;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.integration.jei.categories.FireCraftingCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@ParametersAreNonnullByDefault
public class EIOJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = EnderIO.loc("main");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new FireCraftingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(FireCraftingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(EIORecipes.FIRE_CRAFTING.type().get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.FLINT_AND_STEEL), FireCraftingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(EIOFluids.FIRE_WATER.getBucket().get()), FireCraftingCategory.RECIPE_TYPE);
    }
}
