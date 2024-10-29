package com.enderio.machines.common.integrations.jei;

import com.enderio.EnderIOBase;
import com.enderio.base.common.integrations.jei.subtype.EntityStorageSubtypeInterpreter;
import com.enderio.machines.client.gui.screen.AlloySmelterScreen;
import com.enderio.machines.client.gui.screen.EnchanterScreen;
import com.enderio.machines.client.gui.screen.FluidTankScreen;
import com.enderio.machines.client.gui.screen.PrimitiveAlloySmelterScreen;
import com.enderio.machines.client.gui.screen.SagMillScreen;
import com.enderio.machines.client.gui.screen.SlicerScreen;
import com.enderio.machines.client.gui.screen.SoulBinderScreen;
import com.enderio.machines.client.gui.screen.VatScreen;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.integrations.jei.category.AlloySmeltingCategory;
import com.enderio.machines.common.integrations.jei.category.EnchanterCategory;
import com.enderio.machines.common.integrations.jei.category.PrimitiveAlloySmeltingCategory;
import com.enderio.machines.common.integrations.jei.category.SagMillCategory;
import com.enderio.machines.common.integrations.jei.category.SlicingRecipeCategory;
import com.enderio.machines.common.integrations.jei.category.SoulBindingCategory;
import com.enderio.machines.common.integrations.jei.category.SoulEngineCategory;
import com.enderio.machines.common.integrations.jei.category.TankCategory;
import com.enderio.machines.common.integrations.jei.category.VATCategory;
import com.enderio.machines.common.integrations.jei.transfer.CrafterRecipeTransferHandler;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.menu.PrimitiveAlloySmelterMenu;
import com.enderio.machines.common.menu.SagMillMenu;
import com.enderio.machines.common.menu.SlicerMenu;
import com.enderio.machines.common.menu.SoulBinderMenu;
import com.enderio.machines.common.menu.VatMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class MachinesJEI implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return EnderIOBase.loc("machines");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.PRIMITIVE_ALLOY_SMELTER.get()),
                PrimitiveAlloySmeltingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.ALLOY_SMELTER.get()), AlloySmeltingCategory.TYPE,
                RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.ENCHANTER.get()), EnchanterCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SAG_MILL.get()), SagMillCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SLICE_AND_SPLICE.get()), SlicingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SOUL_BINDER.get()), SoulBindingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.FLUID_TANK.get()), TankCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.PRESSURIZED_FLUID_TANK.get()), TankCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.SOUL_ENGINE.get()), SoulEngineCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MachineBlocks.VAT.get()), VATCategory.TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AlloySmeltingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new EnchanterCategory(registration.getJeiHelpers().getGuiHelper()));
        registration
                .addRecipeCategories(new PrimitiveAlloySmeltingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SagMillCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SlicingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SoulBindingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new TankCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SoulEngineCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new VATCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        MachineJEIRecipes recipes = new MachineJEIRecipes();

        registration.addRecipes(AlloySmeltingCategory.TYPE, recipes.getAlloySmeltingRecipesWithSmelting());
        registration.addRecipes(EnchanterCategory.TYPE, recipes.getEnchanterRecipes());
        registration.addRecipes(PrimitiveAlloySmeltingCategory.TYPE, recipes.getAlloySmeltingRecipes());
        registration.addRecipes(SagMillCategory.TYPE, recipes.getSagMillingRecipes());
        registration.addRecipes(SlicingRecipeCategory.TYPE, recipes.getSlicingRecipes());
        registration.addRecipes(SoulBindingCategory.TYPE, recipes.getSoulBindingRecipes());
        registration.addRecipes(TankCategory.TYPE, recipes.getTankRecipes());
        registration.addRecipes(SoulEngineCategory.TYPE, recipes.getMobGeneratorRecipes());
        registration.addRecipes(VATCategory.TYPE, recipes.getVATRecipes());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AlloySmelterMenu.class, MachineMenus.ALLOY_SMELTER.get(),
                AlloySmeltingCategory.TYPE, AlloySmelterMenu.INPUTS_INDEX, AlloySmelterMenu.INPUT_COUNT,
                AlloySmelterMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(EnchanterMenu.class, MachineMenus.ENCHANTER.get(), EnchanterCategory.TYPE,
                EnchanterMenu.INPUTS_INDEX, EnchanterMenu.INPUT_COUNT, EnchanterMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(PrimitiveAlloySmelterMenu.class,
                MachineMenus.PRIMITIVE_ALLOY_SMELTER.get(), AlloySmeltingCategory.TYPE,
                PrimitiveAlloySmelterMenu.INPUTS_INDEX, PrimitiveAlloySmelterMenu.INPUT_COUNT,
                PrimitiveAlloySmelterMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(SagMillMenu.class, MachineMenus.SAG_MILL.get(), SagMillCategory.TYPE,
                SagMillMenu.INPUTS_INDEX, SagMillMenu.INPUT_COUNT, SagMillMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(SlicerMenu.class, MachineMenus.SLICE_N_SPLICE.get(),
                SlicingRecipeCategory.TYPE, SlicerMenu.INPUTS_INDEX, SlicerMenu.INPUT_COUNT, SlicerMenu.LAST_INDEX + 1,
                36);

        registration.addRecipeTransferHandler(SoulBinderMenu.class, MachineMenus.SOUL_BINDER.get(),
                SoulBindingCategory.TYPE, SoulBinderMenu.INPUTS_INDEX, SoulBinderMenu.INPUT_COUNT,
                SoulBinderMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(VatMenu.class, MachineMenus.VAT.get(), VATCategory.TYPE,
                VatMenu.INPUTS_INDEX, VatMenu.INPUT_COUNT, VatMenu.LAST_INDEX + 1, 36);

        registration.addRecipeTransferHandler(new CrafterRecipeTransferHandler(registration.getTransferHelper()),
                RecipeTypes.CRAFTING);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(MachineBlocks.SOUL_ENGINE.asItem(),
                new EntityStorageSubtypeInterpreter());

        for (var solarPanel : MachineBlocks.SOLAR_PANELS.values()) {
            registration.registerSubtypeInterpreter(solarPanel.asItem(), new EntityStorageSubtypeInterpreter());
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlloySmelterScreen.class, 56, 56, 14, 14, AlloySmeltingCategory.TYPE);
        registration.addRecipeClickArea(AlloySmelterScreen.class, 104, 56, 14, 14, AlloySmeltingCategory.TYPE);

        // TODO: Where to put Crafter recipe area

        registration.addRecipeClickArea(EnchanterScreen.class, 111, 35, 24, 17, EnchanterCategory.TYPE);

        registration.addRecipeClickArea(FluidTankScreen.class, 62, 24, 15, 10, TankCategory.TYPE);
        registration.addRecipeClickArea(FluidTankScreen.class, 47, 40, 10, 9, TankCategory.TYPE);
        registration.addRecipeClickArea(FluidTankScreen.class, 98, 24, 15, 10, TankCategory.TYPE);
        registration.addRecipeClickArea(FluidTankScreen.class, 119, 40, 10, 9, TankCategory.TYPE);

        // TODO: Painting machine needs a viewer

        registration.addRecipeClickArea(PrimitiveAlloySmelterScreen.class, 79, 35, 24, 17,
                PrimitiveAlloySmeltingCategory.TYPE);
        registration.addRecipeClickArea(SagMillScreen.class, 80, 47, 16, 24, SagMillCategory.TYPE);
        registration.addRecipeClickArea(SlicerScreen.class, 98, 61, 24, 16, SlicingRecipeCategory.TYPE);
        registration.addRecipeClickArea(SoulBinderScreen.class, 80, 34, 24, 17, SoulBindingCategory.TYPE);
        registration.addRecipeClickArea(VatScreen.class, 75, 33, 28, 30, VATCategory.TYPE);

        registration.addGhostIngredientHandler(MachineScreen.class, new MachinesGhostSlotHandler());
    }
}
