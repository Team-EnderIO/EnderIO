//package com.enderio.modded_conduits.common.modules.mekanism.laserio;
//
//import com.direwolf20.laserio.common.items.cards.BaseCard;
//import com.direwolf20.laserio.setup.Registration;
//import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
//import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.ChemicalFilter;
//import net.minecraft.world.item.ItemStack;
//import net.neoforged.bus.api.IEventBus;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.neoforge.capabilities.ICapabilityProvider;
//import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
//
//public class MekanismLaserIOCompat {
//    public static final ICapabilityProvider<ItemStack, Void, ChemicalFilter> CARD_CHEMICAL_FILTER_PROVIDER = (stack,
//            v) -> new LaserChemicalFilter(BaseCard.getFilter(stack));
//
//    public static final ICapabilityProvider<ItemStack, Void, ChemicalFilter> CHEMICAL_FILTER_PROVIDER = (stack,
//            v) -> new LaserChemicalFilter(stack);
//
//    public static void init(IEventBus eventBus) {
//        eventBus.addListener(MekanismLaserIOCompat::registerCapEvent);
//    }
//
//    @SubscribeEvent
//    public static void registerCapEvent(RegisterCapabilitiesEvent event) {
//        event.registerItem(MekanismModule.Capabilities.CHEMICAL_FILTER, CARD_CHEMICAL_FILTER_PROVIDER, Registration.Card_Chemical.get());
//        event.registerItem(MekanismModule.Capabilities.CHEMICAL_FILTER, CHEMICAL_FILTER_PROVIDER, Registration.Filter_Basic.get());
//    }
//}
