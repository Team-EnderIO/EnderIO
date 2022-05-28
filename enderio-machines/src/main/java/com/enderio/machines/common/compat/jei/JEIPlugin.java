package com.enderio.machines.common.compat.jei;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.compat.jei.categories.AlloySmeltingCategory;
import com.enderio.machines.common.compat.jei.categories.EnchantingCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static ResourceLocation CATEGORY_ALLOY_SMELTING = EIOMachines.loc("alloy_smelting");
    public static ResourceLocation CATEGORY_ENCHANTING = EIOMachines.loc("enchanting");

    @Override
    public ResourceLocation getPluginUid() {
        return EIOMachines.loc("machines");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AlloySmeltingCategory(guiHelper));
        registration.addRecipeCategories(new EnchantingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        JEIRecipes recipes = new JEIRecipes();

        registration.addRecipes(recipes.getAlloyingRecipes(), CATEGORY_ALLOY_SMELTING);
        registration.addRecipes(recipes.getEnchantingRecipes(), CATEGORY_ENCHANTING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // TODO: Automate this somehow... Maybe tag machines?
//        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SIMPLE_ALLOY_SMELTER.get()), CATEGORY_ALLOY_SMELTING);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SIMPLE_POWERED_FURNACE.get()), VanillaRecipeCategoryUid.FURNACE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.ALLOY_SMELTER.get()), CATEGORY_ALLOY_SMELTING, VanillaRecipeCategoryUid.FURNACE);
//        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.ENHANCED_ALLOY_SMELTER.get()), CATEGORY_ALLOY_SMELTING, VanillaRecipeCategoryUid.FURNACE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.ENCHANTER.get()), CATEGORY_ENCHANTING);
    }
}