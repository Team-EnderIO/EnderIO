package com.enderio.base.common.integrations.laserio;

import com.direwolf20.laserio.setup.Registration;
import com.enderio.EnderIOBase;
import com.enderio.base.api.filter.ResourceFilter;
import com.enderio.base.api.integration.Integration;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = EnderIOBase.MODULE_MOD_ID)
public class LaserIOIntegration implements Integration {

    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> ITEM_FILTER_PROVIDER =
        (stack, v) -> new LaserItemFilter(stack);

    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> FLUID_FILTER_PROVIDER =
        (stack, v) -> new LaserFluidFilter(stack);

    @SubscribeEvent
    public static void registerCapEvent(RegisterCapabilitiesEvent event) {
        event.registerItem(EIOCapabilities.Filter.ITEM, ITEM_FILTER_PROVIDER, Registration.Card_Item.get());
        event.registerItem(EIOCapabilities.Filter.ITEM, FLUID_FILTER_PROVIDER, Registration.Card_Fluid.get());
    }
}
