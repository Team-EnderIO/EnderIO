package com.enderio.enderio.common.compat.jei;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.common.block.glass.GlassBlocks;
import com.enderio.enderio.common.compat.jei.category.FireCraftingCategory;
import com.enderio.enderio.common.compat.jei.extension.ShapedEntityStorageCategoryExtension;
import com.enderio.enderio.common.compat.jei.subtype.ConduitSubtypeInterpreter;
import com.enderio.enderio.common.compat.jei.subtype.SoulBindableSubtypeInterpreter;
import com.enderio.enderio.common.init.EIOBlocks;
import com.enderio.enderio.common.init.EIOItems;
import com.enderio.enderio.common.item.misc.BrokenSpawnerItem;
import com.enderio.enderio.common.recipe.ShapedEntityStorageRecipe;
import com.enderio.enderio.conduits.common.init.ConduitBlocks;
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

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class EnderIOJEI implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return EnderIO.rl("base");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new FireCraftingCategory(guiHelper));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory()
                .addExtension(ShapedEntityStorageRecipe.class, new ShapedEntityStorageCategoryExtension());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        EnderIOJEIRecipes recipes = new EnderIOJEIRecipes();
        registration.addRecipes(FireCraftingCategory.TYPE, recipes.getAllFireCraftingRecipes());

        List<ItemStack> spawners = BrokenSpawnerItem.getPossibleStacks();
        registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, spawners);

        List<ItemStack> glasses = new ArrayList<>();
        for (GlassBlocks glass : EIOBlocks.GLASS_BLOCKS.values()) {
            for (var color : glass.COLORS.values()) {
                glasses.add(new ItemStack(color.asItem()));
            }
        }
        registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, glasses);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(EIOItems.SOUL_VIAL.get(), new SoulBindableSubtypeInterpreter());
        registration.registerSubtypeInterpreter(EIOItems.BROKEN_SPAWNER.get(), new SoulBindableSubtypeInterpreter());
        registration.registerSubtypeInterpreter(ConduitBlocks.CONDUIT.asItem(), new ConduitSubtypeInterpreter());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(EnderContainerScreen.class, new FilterGhostIngredientHandler());
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
