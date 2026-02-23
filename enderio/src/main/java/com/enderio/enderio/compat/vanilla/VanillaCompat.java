package com.enderio.enderio.compat.vanilla;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.api.soul.storage.SoulHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class VanillaCompat {

    public static final ICapabilityProvider<ItemStack, Void, SoulBindable> SPAWN_EGG_BINDABLE_PROVIDER =
        (stack, v) -> new SpawnEggSoulBindable(stack);

    public static final ICapabilityProvider<ItemStack, Void, SoulHandler> SPAWN_EGG_HANDLER_PROVIDER =
        (stack, v) -> new SpawnEggSoulHandler(stack);

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (var spawnEgg : BuiltInRegistries.ITEM.componentLookup().findAll(DataComponents.ENTITY_DATA)) {
            event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM, SPAWN_EGG_BINDABLE_PROVIDER, spawnEgg.value());
            event.registerItem(EnderIOCapabilities.SOUL_HANDLER_ITEM, SPAWN_EGG_HANDLER_PROVIDER, spawnEgg.value());
        }
    }
}
