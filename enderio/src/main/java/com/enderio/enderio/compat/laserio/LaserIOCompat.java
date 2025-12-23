//package com.enderio.enderio.compat.laserio;
//
//import com.direwolf20.laserio.common.items.cards.BaseCard;
//import com.direwolf20.laserio.setup.Registration;
//import com.enderio.enderio.api.EnderIOCapabilities;
//import com.enderio.enderio.api.filter.FluidFilter;
//import com.enderio.enderio.api.filter.ItemFilter;
//import net.minecraft.world.item.ItemStack;
//import net.neoforged.bus.api.IEventBus;
//import net.neoforged.neoforge.capabilities.ICapabilityProvider;
//import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
//
//public class LaserIOCompat {
//    public static final ICapabilityProvider<ItemStack, Void, ItemFilter> CARD_ITEM_FILTER_PROVIDER = (stack,
//        v) -> new LaserItemFilter(BaseCard.getFilter(stack));
//
//    public static final ICapabilityProvider<ItemStack, Void, ItemFilter> ITEM_FILTER_PROVIDER = (stack,
//        v) -> new LaserItemFilter(stack);
//
//    public static final ICapabilityProvider<ItemStack, Void, FluidFilter> CARD_FLUID_FILTER_PROVIDER = (stack,
//        v) -> new LaserFluidFilter(BaseCard.getFilter(stack));
//
//    public static final ICapabilityProvider<ItemStack, Void, FluidFilter> FLUID_FILTER_PROVIDER = (stack,
//        v) -> new LaserFluidFilter(stack);
//
//    public static void init(IEventBus eventBus) {
//        eventBus.addListener(LaserIOCompat::registerCapEvent);
//    }
//
//    private static void registerCapEvent(RegisterCapabilitiesEvent event) {
//        // Register cards
//        event.registerItem(EnderIOCapabilities.ITEM_FILTER, CARD_ITEM_FILTER_PROVIDER, Registration.Card_Item.get());
//        event.registerItem(EnderIOCapabilities.FLUID_FILTER, CARD_FLUID_FILTER_PROVIDER, Registration.Card_Fluid.get());
//
//        // Register raw filters
//        event.registerItem(EnderIOCapabilities.ITEM_FILTER, ITEM_FILTER_PROVIDER, Registration.Filter_Basic.get());
//        event.registerItem(EnderIOCapabilities.FLUID_FILTER, FLUID_FILTER_PROVIDER, Registration.Filter_Basic.get());
//    }
//}
