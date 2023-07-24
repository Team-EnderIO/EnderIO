package com.enderio.machines.common.integrations.jei;

import com.enderio.EnderIO;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.integrations.jei.category.*;
import com.enderio.machines.common.integrations.jei.transfer.CrafterRecipeTransferHandler;
import com.enderio.machines.common.menu.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
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
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.PRIMITIVE_ALLOY_SMELTER.get()), PrimitiveAlloySmeltingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.ALLOY_SMELTER.get()), AlloySmeltingCategory.TYPE, RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.ENCHANTER.get()), EnchanterCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SAG_MILL.get()), SagMillCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SLICE_AND_SPLICE.get()), SlicingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SOUL_BINDER.get()), SoulBindingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.FLUID_TANK.get()), TankCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.PRESSURIZED_FLUID_TANK.get()), TankCategory.TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AlloySmeltingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new EnchanterCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new PrimitiveAlloySmeltingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SagMillCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SlicingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SoulBindingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new TankCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        MachineJEIRecipes recipes = new MachineJEIRecipes();

        registration.addRecipes(AlloySmeltingCategory.TYPE, recipes.getAlloySmeltingRecipesWithSmelting());
        registration.addRecipes(EnchanterCategory.TYPE, recipes.getEnchanterRecipes());
        registration.addRecipes(PrimitiveAlloySmeltingCategory.TYPE, recipes.getAlloySmeltingRecipes());
        registration.addRecipes(SagMillCategory.TYPE, recipes.getSagmillingRecipes());
        registration.addRecipes(SlicingRecipeCategory.TYPE, recipes.getSlicingRecipes());
        registration.addRecipes(SoulBindingCategory.TYPE, recipes.getSoulBindingRecipes());
        registration.addRecipes(TankCategory.TYPE, recipes.getTankRecipes());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AlloySmelterMenu.class, MachineMenus.ALLOY_SMELTER.get(), AlloySmeltingCategory.TYPE,
            AlloySmelterMenu.INPUTS_INDEX, AlloySmelterMenu.INPUT_COUNT,
            AlloySmelterMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(EnchanterMenu.class, MachineMenus.ENCHANTER.get(), EnchanterCategory.TYPE,
            EnchanterMenu.INPUTS_INDEX, EnchanterMenu.INPUT_COUNT,
            EnchanterMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(PrimitiveAlloySmelterMenu.class, MachineMenus.PRIMITIVE_ALLOY_SMELTER.get(), AlloySmeltingCategory.TYPE,
            PrimitiveAlloySmelterMenu.INPUTS_INDEX, PrimitiveAlloySmelterMenu.INPUT_COUNT,
            PrimitiveAlloySmelterMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(SagMillMenu.class, MachineMenus.SAG_MILL.get(), SagMillCategory.TYPE,
            SagMillMenu.INPUTS_INDEX, SagMillMenu.INPUT_COUNT,
            SagMillMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(SlicerMenu.class, MachineMenus.SLICE_N_SPLICE.get(), SlicingRecipeCategory.TYPE,
            SlicerMenu.INPUTS_INDEX, SlicerMenu.INPUT_COUNT,
            SlicerMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(SoulBinderMenu.class, MachineMenus.SOUL_BINDER.get(), SoulBindingCategory.TYPE,
            SoulBinderMenu.INPUTS_INDEX, SoulBinderMenu.INPUT_COUNT,
            SoulBinderMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(new CrafterRecipeTransferHandler(registration.getTransferHelper()), RecipeTypes.CRAFTING);
    }
}
