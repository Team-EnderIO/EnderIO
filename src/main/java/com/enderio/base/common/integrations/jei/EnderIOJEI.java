package com.enderio.base.common.integrations.jei;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.integrations.jei.category.FireCraftingCategory;
import com.enderio.base.common.integrations.jei.subtype.SoulVialSubtypeInterpreter;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class EnderIOJEI implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return EnderIO.loc("base");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new FireCraftingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();


        // TODO: Finish and enable in 1.19.4
//        registration.addRecipes(FireCraftingCategory.TYPE, recipeManager.getAllRecipesFor(EIORecipes.FIRE_CRAFTING.type().get()));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(EIOItems.FILLED_SOUL_VIAL.get(), new SoulVialSubtypeInterpreter());
    }
}
