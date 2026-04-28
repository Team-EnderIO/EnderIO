//package com.enderio.modded_conduits.client.modules.mekanism;
//
//import com.enderio.enderio.api.conduits.screen.RegisterConduitScreenTypesEvent;
//import com.enderio.modded_conduits.client.ConduitClientModule;
//import com.enderio.modded_conduits.client.modules.mekanism.screens.ChemicalConduitScreenType;
//import com.enderio.modded_conduits.client.modules.mekanism.screens.EnderChemicalFilterScreen;
//import com.enderio.modded_conduits.client.modules.mekanism.screens.HeatConduitScreenType;
//import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
//import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilter;
//import net.neoforged.bus.api.IEventBus;
//import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
//import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
//
//public class MekanismClientModule implements ConduitClientModule {
//
//    public static final MekanismClientModule INSTANCE = new MekanismClientModule();
//
//    private MekanismClientModule() {
//    }
//
//    @Override
//    public void initialize(IEventBus modEventBus) {
//        modEventBus.addListener(this::registerConduitScreenTypes);
//        modEventBus.addListener(this::registerMenuScreens);
//        modEventBus.addListener(this::registerClientComponents);
//    }
//
//    private void registerConduitScreenTypes(RegisterConduitScreenTypesEvent event) {
//        event.register(MekanismModule.TYPE_CHEMICAL.get(), new ChemicalConduitScreenType());
//        event.register(MekanismModule.TYPE_HEAT.get(), new HeatConduitScreenType());
//    }
//
//    private void registerMenuScreens(RegisterMenuScreensEvent event) {
//        event.register(MekanismModule.CHEMICAL_FILTER_MENU.get(), EnderChemicalFilterScreen::new);
//    }
//
//    private void registerClientComponents(RegisterClientTooltipComponentFactoriesEvent event) {
//        event.register(EnderChemicalFilter.class, ClientEnderChemicalFilterTooltip::new);
//    }
//}
