package com.enderio.base.common.integrations.jei;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.integrations.jei.category.FireCraftingCategory;
import com.enderio.base.common.integrations.jei.extension.ShapedEntityStorageCategoryExtension;
import com.enderio.base.common.integrations.jei.subtype.EntityStorageSubtypeInterpreter;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.recipe.ShapedEntityStorageRecipe;
import com.enderio.core.client.gui.screen.EIOScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

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
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(ShapedEntityStorageRecipe.class, r -> ShapedEntityStorageRecipe.REGISTERED_RECIPES.contains(r.getId()), ShapedEntityStorageCategoryExtension::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        EnderIOJEIRecipes recipes = new EnderIOJEIRecipes();
        registration.addRecipes(FireCraftingCategory.TYPE, recipes.getAllFireCraftingRecipes());

        registration.addIngredientInfo(EIOItems.GRAINS_OF_INFINITY.asStack(), VanillaTypes.ITEM_STACK, EIOLang.JEI_GRAINS_HAND_GRIND);
        registration.addIngredientInfo(EIOItems.POWDERED_COAL.asStack(), VanillaTypes.ITEM_STACK, EIOLang.JEI_COAL_HAND_GRIND);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(EIOItems.FILLED_SOUL_VIAL.get(), new EntityStorageSubtypeInterpreter());
        registration.registerSubtypeInterpreter(EIOItems.BROKEN_SPAWNER.get(), new EntityStorageSubtypeInterpreter());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(EIOScreen.class, new FilterGhostIngredientHandler());
    }

    // region Utilities

    public static ItemStack getResultItem(Recipe<?> recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        }
        RegistryAccess registryAccess = level.registryAccess();
        return recipe.getResultItem(registryAccess);
    }

    // endregion
}
