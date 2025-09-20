package com.enderio.modconduits.common.modules.laserio;

import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.setup.Registration;
import com.enderio.base.api.integration.Integration;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import com.enderio.modconduits.common.modules.mekanism.chemical_filter.ChemicalFilter;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class MekansimIntegration implements Integration {

    public static final ICapabilityProvider<ItemStack, Void, ChemicalFilter> CARD_CHEMICAL_FILTER_PROVIDER = (stack,
            v) -> new LaserChemicalFilter(BaseCard.getFilter(stack));

    public static final ICapabilityProvider<ItemStack, Void, ChemicalFilter> CHEMICAL_FILTER_PROVIDER = (stack,
            v) -> new LaserChemicalFilter(stack);

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(this::registerCapEvent);
    }

    @SubscribeEvent
    public void registerCapEvent(RegisterCapabilitiesEvent event) {
        event.registerItem(MekanismModule.Capabilities.CHEMICAL_FILTER, CARD_CHEMICAL_FILTER_PROVIDER, Registration.Card_Chemical.get());
        event.registerItem(MekanismModule.Capabilities.CHEMICAL_FILTER, CHEMICAL_FILTER_PROVIDER, Registration.Filter_Basic.get());
    }
}
