package com.enderio.enderio.conduits.modded.client.modules.mekanism;

import com.enderio.enderio.api.conduits.model.RegisterConduitModelModifiersEvent;
import com.enderio.enderio.api.conduits.screen.RegisterConduitScreenTypesEvent;
import com.enderio.enderio.conduits.modded.client.ConduitClientModule;
import com.enderio.enderio.conduits.modded.client.modules.mekanism.models.ChemicalConduitModelModifier;
import com.enderio.enderio.conduits.modded.client.modules.mekanism.screens.ChemicalConduitScreenType;
import com.enderio.enderio.conduits.modded.client.modules.mekanism.screens.HeatConduitScreenType;
import com.enderio.enderio.conduits.modded.common.modules.mekanism.MekanismModule;
import net.neoforged.bus.api.IEventBus;

public class MekanismClientModule implements ConduitClientModule {

    public static final MekanismClientModule INSTANCE = new MekanismClientModule();

    private MekanismClientModule() {
    }

    @Override
    public void initialize(IEventBus modEventBus) {
        modEventBus.addListener(this::registerConduitScreenTypes);
        modEventBus.addListener(this::registerConduitCoreModelModifiers);
    }

    private void registerConduitScreenTypes(RegisterConduitScreenTypesEvent event) {
        event.register(MekanismModule.TYPE_CHEMICAL.get(), new ChemicalConduitScreenType());
        event.register(MekanismModule.TYPE_HEAT.get(), new HeatConduitScreenType());
    }

    private void registerConduitCoreModelModifiers(RegisterConduitModelModifiersEvent event) {
        event.register(MekanismModule.TYPE_CHEMICAL.get(), ChemicalConduitModelModifier::new);
    }
}
