package com.enderio.base.common.compat.vanilla;

import com.enderio.EnderIOBase;
import com.enderio.base.api.soul.binding.ISoulBindable;
import com.enderio.base.api.soul.storage.ISoulHandler;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID)
public class VanillaCompat {

    public static final ICapabilityProvider<ItemStack, Void, ISoulBindable> SPAWN_EGG_BINDABLE_PROVIDER =
        (stack, v) -> new SpawnEggSoulBindable(stack);

    public static final ICapabilityProvider<ItemStack, Void, ISoulHandler> SPAWN_EGG_HANDLER_PROVIDER =
        (stack, v) -> new SpawnEggSoulHandler(stack);

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (var spawnEgg : SpawnEggItem.eggs()) {
            event.registerItem(EIOCapabilities.SoulBindable.ITEM, SPAWN_EGG_BINDABLE_PROVIDER, spawnEgg);
            event.registerItem(EIOCapabilities.SoulHandler.ITEM, SPAWN_EGG_HANDLER_PROVIDER, spawnEgg);
        }
    }
}
