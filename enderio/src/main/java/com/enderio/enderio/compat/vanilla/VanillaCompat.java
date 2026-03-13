package com.enderio.enderio.compat.vanilla;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.api.soul.storage.SoulHandler;
import com.enderio.enderio.init.EIOItems;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class VanillaCompat {

    public static final ICapabilityProvider<ItemStack, Void, SoulBindable> SPAWN_EGG_BINDABLE_PROVIDER =
        (stack, v) -> stack.getItem() instanceof SpawnEggItem ? new SpawnEggSoulBindable(stack) : null;

    public static final ICapabilityProvider<ItemStack, Void, SoulHandler> SPAWN_EGG_HANDLER_PROVIDER =
        (stack, v) -> stack.getItem() instanceof SpawnEggItem ? new SpawnEggSoulHandler(stack) : null;

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // TODO: 26.1 - components are not bound at this stage.
        // https://github.com/neoforged/NeoForge/issues/2981
//        for (var spawnEgg : BuiltInRegistries.ITEM.componentLookup().findAll(DataComponents.ENTITY_DATA)) {
//            event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM, SPAWN_EGG_BINDABLE_PROVIDER, spawnEgg.value());
//            event.registerItem(EnderIOCapabilities.SOUL_HANDLER_ITEM, SPAWN_EGG_HANDLER_PROVIDER, spawnEgg.value());
//        }

        // For now, we'll register to all items
        Item[] allItems = BuiltInRegistries.ITEM.stream().toArray(Item[]::new);
        event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM, SPAWN_EGG_BINDABLE_PROVIDER, allItems);
        event.registerItem(EnderIOCapabilities.SOUL_HANDLER_ITEM, SPAWN_EGG_HANDLER_PROVIDER, allItems);
    }
}
