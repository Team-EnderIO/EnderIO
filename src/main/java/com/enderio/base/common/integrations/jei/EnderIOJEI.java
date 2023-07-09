package com.enderio.base.common.integrations.jei;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.integrations.jei.category.FireCraftingCategory;
import com.enderio.base.common.integrations.jei.category.GrindingCategory;
import com.enderio.base.common.integrations.jei.extension.ShapedEntityStorageCategoryExtension;
import com.enderio.base.common.integrations.jei.subtype.EntityStorageSubtypeInterpreter;
import com.enderio.base.common.recipe.ShapedEntityStorageRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        registration.addRecipeCategories(new GrindingCategory(guiHelper));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(ShapedEntityStorageRecipe.class, r -> ShapedEntityStorageRecipe.REGISTERED_RECIPES.contains(r.getId()), ShapedEntityStorageCategoryExtension::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        EnderIOJEIRecipes recipes = new EnderIOJEIRecipes();
        registration.addRecipes(FireCraftingCategory.TYPE, recipes.getAllFireCraftingRecipes());
        registration.addRecipes(GrindingCategory.TYPE, recipes.getAllGrindingRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.GRINDSTONE), GrindingCategory.TYPE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(EIOItems.FILLED_SOUL_VIAL.get(), new EntityStorageSubtypeInterpreter());
        registration.registerSubtypeInterpreter(EIOItems.BROKEN_SPAWNER.get(), new EntityStorageSubtypeInterpreter());
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
