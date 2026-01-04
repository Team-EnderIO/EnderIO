package com.enderio.modded_conduits.client.modules.mekanism;

import com.enderio.enderio.api.conduits.model.RegisterConduitModelModifiersEvent;
import com.enderio.enderio.api.conduits.screen.RegisterConduitScreenTypesEvent;
import com.enderio.modded_conduits.client.ConduitClientModule;
import com.enderio.modded_conduits.client.modules.mekanism.models.ChemicalConduitModelModifier;
import com.enderio.modded_conduits.client.modules.mekanism.screens.ChemicalConduitScreenType;
import com.enderio.modded_conduits.client.modules.mekanism.screens.EnderChemicalFilterScreen;
import com.enderio.modded_conduits.client.modules.mekanism.screens.HeatConduitScreenType;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class MekanismClientModule implements ConduitClientModule {

    public static final MekanismClientModule INSTANCE = new MekanismClientModule();

    private MekanismClientModule() {
    }

    @Override
    public void initialize(IEventBus modEventBus) {
        modEventBus.addListener(this::registerConduitScreenTypes);
        modEventBus.addListener(this::registerConduitCoreModelModifiers);
        modEventBus.addListener(this::registerMenuScreens);
    }

    private void registerConduitScreenTypes(RegisterConduitScreenTypesEvent event) {
        event.register(MekanismModule.TYPE_CHEMICAL.get(), new ChemicalConduitScreenType());
        event.register(MekanismModule.TYPE_HEAT.get(), new HeatConduitScreenType());
    }

    private void registerConduitCoreModelModifiers(RegisterConduitModelModifiersEvent event) {
        event.register(MekanismModule.TYPE_CHEMICAL.get(), ChemicalConduitModelModifier::new);
    }
    
    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MekanismModule.CHEMICAL_FILTER_MENU.get(), EnderChemicalFilterScreen::new);
    }
}
