//package com.enderio.enderio.compat.jei_machines_to_merge;
//
//import com.enderio.enderio.EnderIO;
//import com.enderio.enderio.client.content.machines.gui.screen.AlloySmelterScreen;
//import com.enderio.enderio.client.content.machines.gui.screen.EnchanterScreen;
//import com.enderio.enderio.client.content.machines.gui.screen.FluidTankScreen;
//import com.enderio.enderio.client.content.machines.gui.screen.SagMillScreen;
//import com.enderio.enderio.client.content.machines.gui.screen.SlicerScreen;
//import com.enderio.enderio.client.content.machines.gui.screen.SoulBinderScreen;
//import com.enderio.enderio.client.content.machines.gui.screen.VatScreen;
//import com.enderio.enderio.client.content.machines.gui.screen.WeatherObeliskScreen;
//import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
//import com.enderio.enderio.compat.jei.subtype.SoulBindableSubtypeInterpreter;
//import com.enderio.enderio.compat.jei_machines_to_merge.category.AlloySmeltingCategory;
//import com.enderio.enderio.compat.jei_machines_to_merge.category.EnchanterCategory;
//import com.enderio.enderio.compat.jei_machines_to_merge.category.SagMillCategory;
//import com.enderio.enderio.compat.jei_machines_to_merge.category.SlicingRecipeCategory;
//import com.enderio.enderio.compat.jei_machines_to_merge.category.SoulBindingCategory;
//import com.enderio.enderio.compat.jei_machines_to_merge.category.SoulEngineCategory;
//import com.enderio.enderio.compat.jei_machines_to_merge.category.TankCategory;
//import com.enderio.enderio.compat.jei_machines_to_merge.category.VATCategory;
//import com.enderio.enderio.compat.jei_machines_to_merge.category.WeatherChangeCategory;
//import com.enderio.enderio.compat.jei_machines_to_merge.transfer.CrafterRecipeTransferHandler;
//import com.enderio.enderio.compat.jei_machines_to_merge.transfer.FluidTankTransferHelper;
//import com.enderio.enderio.compat.jei_machines_to_merge.transfer.VATTransferHelper;
//import com.enderio.enderio.compat.jei_machines_to_merge.transfer.WeatherObeliskTransferHelper;
//import com.enderio.enderio.content.enchanter.EnchanterMenu;
//import com.enderio.enderio.content.machines.alloy.AlloySmelterMenu;
//import com.enderio.enderio.content.machines.sag_mill.SagMillMenu;
//import com.enderio.enderio.content.machines.slicer.SlicerMenu;
//import com.enderio.enderio.content.machines.soul_binder.SoulBinderMenu;
//import com.enderio.enderio.init.EIOBlocks;
//import com.enderio.enderio.init.EIOMenus;
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.constants.RecipeTypes;
//import mezz.jei.api.registration.IGuiHandlerRegistration;
//import mezz.jei.api.registration.IRecipeCatalystRegistration;
//import mezz.jei.api.registration.IRecipeCategoryRegistration;
//import mezz.jei.api.registration.IRecipeRegistration;
//import mezz.jei.api.registration.IRecipeTransferRegistration;
//import mezz.jei.api.registration.ISubtypeRegistration;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.item.ItemStack;
//
//@JeiPlugin
//public class MachinesJEI implements IModPlugin {
//    @Override
//    public Identifier getPluginUid() {
//        return EnderIO.rl("machines");
//    }
//
//    @Override
//    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.ALLOY_SMELTER.get()), AlloySmeltingCategory.TYPE,
//                RecipeTypes.SMELTING);
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.ENCHANTER.get()), EnchanterCategory.TYPE);
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.SAG_MILL.get()), SagMillCategory.TYPE);
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.SLICE_AND_SPLICE.get()), SlicingRecipeCategory.TYPE);
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.SOUL_BINDER.get()), SoulBindingCategory.TYPE);
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.FLUID_TANK.get()), TankCategory.TYPE);
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.PRESSURIZED_FLUID_TANK.get()), TankCategory.TYPE);
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.SOUL_ENGINE.get()), SoulEngineCategory.TYPE);
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.VAT.get()), VATCategory.TYPE);
//        registration.addRecipeCatalyst(new ItemStack(EIOBlocks.WEATHER_OBELISK.get()), WeatherChangeCategory.TYPE);
//    }
//
//    @Override
//    public void registerCategories(IRecipeCategoryRegistration registration) {
//        registration.addRecipeCategories(new AlloySmeltingCategory(registration.getJeiHelpers().getGuiHelper()));
//        registration.addRecipeCategories(new EnchanterCategory(registration.getJeiHelpers().getGuiHelper()));
//        registration.addRecipeCategories(new SagMillCategory(registration.getJeiHelpers().getGuiHelper()));
//        registration.addRecipeCategories(new SlicingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
//        registration.addRecipeCategories(new SoulBindingCategory(registration.getJeiHelpers().getGuiHelper()));
//        registration.addRecipeCategories(new TankCategory(registration.getJeiHelpers().getGuiHelper()));
//        registration.addRecipeCategories(new SoulEngineCategory(registration.getJeiHelpers().getGuiHelper()));
//        registration.addRecipeCategories(new VATCategory(registration.getJeiHelpers().getGuiHelper()));
//        registration.addRecipeCategories(new WeatherChangeCategory(registration.getJeiHelpers().getGuiHelper()));
//    }
//
//    @Override
//    public void registerRecipes(IRecipeRegistration registration) {
//        MachineJEIRecipes recipes = new MachineJEIRecipes();
//
//        registration.addRecipes(AlloySmeltingCategory.TYPE, recipes.getAlloySmeltingRecipes());
//        registration.addRecipes(EnchanterCategory.TYPE, recipes.getEnchanterRecipes());
//        registration.addRecipes(SagMillCategory.TYPE, recipes.getSagMillingRecipes());
//        registration.addRecipes(SlicingRecipeCategory.TYPE, recipes.getSlicingRecipes());
//        registration.addRecipes(SoulBindingCategory.TYPE, recipes.getSoulBindingRecipes());
//        registration.addRecipes(TankCategory.TYPE, recipes.getTankRecipes());
//        registration.addRecipes(SoulEngineCategory.TYPE, recipes.getMobGeneratorRecipes());
//        registration.addRecipes(VATCategory.TYPE, recipes.getVATRecipes());
//        registration.addRecipes(WeatherChangeCategory.TYPE, recipes.getWeatherRecipes());
//    }
//
//    @Override
//    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
//        registration.addRecipeTransferHandler(AlloySmelterMenu.class, EIOMenus.ALLOY_SMELTER.get(),
//                AlloySmeltingCategory.TYPE, AlloySmelterMenu.INPUTS_INDEX, AlloySmelterMenu.INPUT_COUNT,
//                AlloySmelterMenu.LAST_INDEX + 1, 36);
//
//        registration.addRecipeTransferHandler(EnchanterMenu.class, EIOMenus.ENCHANTER.get(), EnchanterCategory.TYPE,
//                EnchanterMenu.INPUTS_INDEX, EnchanterMenu.INPUT_COUNT, EnchanterMenu.LAST_INDEX + 1, 36);
//
//        registration.addRecipeTransferHandler(SagMillMenu.class, EIOMenus.SAG_MILL.get(), SagMillCategory.TYPE,
//                SagMillMenu.INPUTS_INDEX, SagMillMenu.INPUT_COUNT, SagMillMenu.LAST_INDEX + 1, 36);
//
//        registration.addRecipeTransferHandler(SlicerMenu.class, EIOMenus.SLICE_N_SPLICE.get(),
//                SlicingRecipeCategory.TYPE, SlicerMenu.INPUTS_INDEX, SlicerMenu.INPUT_COUNT, SlicerMenu.LAST_INDEX + 1,
//                36);
//
//        registration.addRecipeTransferHandler(SoulBinderMenu.class, EIOMenus.SOUL_BINDER.get(),
//                SoulBindingCategory.TYPE, SoulBinderMenu.INPUTS_INDEX, SoulBinderMenu.INPUT_COUNT,
//                SoulBinderMenu.LAST_INDEX + 1, 36);
//
//        registration.addRecipeTransferHandler(new VATTransferHelper(registration.getTransferHelper()), VATCategory.TYPE);
//
//        registration.addRecipeTransferHandler(new CrafterRecipeTransferHandler(registration.getTransferHelper()),
//                RecipeTypes.CRAFTING);
//
//        registration.addRecipeTransferHandler(new WeatherObeliskTransferHelper(registration.getTransferHelper()), WeatherChangeCategory.TYPE);
//
//        registration.addRecipeTransferHandler(new FluidTankTransferHelper(registration.getTransferHelper()), TankCategory.TYPE);
//
//
//    }
//
//    @Override
//    public void registerItemSubtypes(ISubtypeRegistration registration) {
//        registration.registerSubtypeInterpreter(EIOBlocks.POWERED_SPAWNER.asItem(),
//                new SoulBindableSubtypeInterpreter());
//
//        registration.registerSubtypeInterpreter(EIOBlocks.SOUL_ENGINE.asItem(),
//                new SoulBindableSubtypeInterpreter());
//
//        for (var solarPanel : EIOBlocks.SOLAR_PANELS.values()) {
//            registration.registerSubtypeInterpreter(solarPanel.asItem(), new SoulBindableSubtypeInterpreter());
//        }
//    }
//
//    @Override
//    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
//        registration.addRecipeClickArea(AlloySmelterScreen.class, 56, 56, 14, 14, AlloySmeltingCategory.TYPE);
//        registration.addRecipeClickArea(AlloySmelterScreen.class, 104, 56, 14, 14, AlloySmeltingCategory.TYPE);
//
//        // TODO: Where to put Crafter recipe area
//
//        registration.addRecipeClickArea(EnchanterScreen.class, 111, 35, 24, 17, EnchanterCategory.TYPE);
//
//        registration.addRecipeClickArea(FluidTankScreen.class, 62, 24, 15, 10, TankCategory.TYPE);
//        registration.addRecipeClickArea(FluidTankScreen.class, 47, 40, 10, 9, TankCategory.TYPE);
//        registration.addRecipeClickArea(FluidTankScreen.class, 98, 24, 15, 10, TankCategory.TYPE);
//        registration.addRecipeClickArea(FluidTankScreen.class, 119, 40, 10, 9, TankCategory.TYPE);
//
//        // TODO: Painting machine needs a viewer
//
//        registration.addRecipeClickArea(SagMillScreen.class, 80, 47, 16, 24, SagMillCategory.TYPE);
//        registration.addRecipeClickArea(SlicerScreen.class, 98, 61, 24, 16, SlicingRecipeCategory.TYPE);
//        registration.addRecipeClickArea(SoulBinderScreen.class, 80, 34, 24, 17, SoulBindingCategory.TYPE);
//        registration.addRecipeClickArea(VatScreen.class, 75, 33, 28, 30, VATCategory.TYPE);
//        registration.addRecipeClickArea(WeatherObeliskScreen.class, 80, 27, 14, 34, WeatherChangeCategory.TYPE);
//
//        registration.addGhostIngredientHandler(MachineScreen.class, new MachinesGhostSlotHandler());
//    }
//}
