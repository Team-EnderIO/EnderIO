package com.enderio.modconduits.mods.laserio;

import com.direwolf20.laserio.setup.Registration;
import com.enderio.base.api.filter.ResourceFilter;
import com.enderio.base.api.integration.Integration;
import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.api.integration.IntegrationWrapper;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.modconduits.ModdedConduits;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class LaserIOIntegration implements Integration {

    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> ITEM_FILTER_PROVIDER =
        (stack, v) -> new LaserItemFilter(stack);

    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> FLUID_FILTER_PROVIDER =
        (stack, v) -> new LaserFluidFilter(stack);

    public static final IntegrationWrapper<MekansimIntegration> MEK_LASER_IO_INTEGRATION = IntegrationManager.wrapper("mekanism", () -> MekansimIntegration::new, ModdedConduits.modEventBus);

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(this::registerCapEvent);
    }

    @SubscribeEvent
    public void registerCapEvent(RegisterCapabilitiesEvent event) {
        event.registerItem(EIOCapabilities.Filter.ITEM, ITEM_FILTER_PROVIDER, Registration.Card_Item.get());
        event.registerItem(EIOCapabilities.Filter.ITEM, FLUID_FILTER_PROVIDER, Registration.Card_Fluid.get());
    }
}
