package com.enderio.modconduits.common.modules.laserio;

import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.setup.Registration;
import com.enderio.base.api.integration.Integration;
import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.api.integration.IntegrationWrapper;
import com.enderio.base.api.filter.FluidFilter;
import com.enderio.base.api.filter.ItemFilter;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.modconduits.common.ModdedConduits;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class LaserIOIntegration implements Integration {

    public static final ICapabilityProvider<ItemStack, Void, ItemFilter> CARD_ITEM_FILTER_PROVIDER = (stack,
            v) -> new LaserItemFilter(BaseCard.getFilter(stack));

    public static final ICapabilityProvider<ItemStack, Void, ItemFilter> ITEM_FILTER_PROVIDER = (stack,
            v) -> new LaserItemFilter(stack);

    public static final ICapabilityProvider<ItemStack, Void, FluidFilter> CARD_FLUID_FILTER_PROVIDER = (stack,
            v) -> new LaserFluidFilter(BaseCard.getFilter(stack));

    public static final ICapabilityProvider<ItemStack, Void, FluidFilter> FLUID_FILTER_PROVIDER = (stack,
            v) -> new LaserFluidFilter(stack);

    public static final IntegrationWrapper<MekansimIntegration> MEK_LASER_IO_INTEGRATION = IntegrationManager
            .wrapper("mekanism", () -> MekansimIntegration::new, ModdedConduits.modEventBus);

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(this::registerCapEvent);
    }

    @SubscribeEvent
    public void registerCapEvent(RegisterCapabilitiesEvent event) {
        // Register cards
        event.registerItem(EIOCapabilities.ITEM_FILTER, CARD_ITEM_FILTER_PROVIDER, Registration.Card_Item.get());
        event.registerItem(EIOCapabilities.FLUID_FILTER, CARD_FLUID_FILTER_PROVIDER, Registration.Card_Fluid.get());

        // Register raw filters
        event.registerItem(EIOCapabilities.ITEM_FILTER, ITEM_FILTER_PROVIDER, Registration.Filter_Basic.get());
        event.registerItem(EIOCapabilities.FLUID_FILTER, FLUID_FILTER_PROVIDER, Registration.Filter_Basic.get());
    }
}
