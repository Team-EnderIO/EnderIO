package com.enderio.enderio.compat.vanilla;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.binding.BindableSoulSource;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;

@EventBusSubscriber
public class VanillaCompat {

    public static final ICapabilityProvider<ItemStack, ItemAccess, SoulBindable> SPAWN_EGG_BINDABLE_PROVIDER =
        (_, itemAccess) -> itemAccess.getResource().getItem() instanceof SpawnEggItem ? new SpawnEggSoulBindable(itemAccess) : null;

    public static final ICapabilityProvider<ItemStack, ItemAccess, BindableSoulSource> SPAWN_EGG_BINDABLE_SOUL_SOURCE =
        (_, itemAccess) -> itemAccess.getResource().getItem() instanceof SpawnEggItem ? new SpawnEggBindableSoulSource(itemAccess) : null;

    public static final ICapabilityProvider<ItemStack, ItemAccess, SoulBindable> EGG_BINDABLE_PROVIDER =
        (_, itemAccess) -> new EggSoulBindable(itemAccess);

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // https://github.com/neoforged/NeoForge/issues/2981
        Item[] allItems = BuiltInRegistries.ITEM.stream().toArray(Item[]::new);
        event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM, SPAWN_EGG_BINDABLE_PROVIDER, allItems);
        event.registerItem(EnderIOCapabilities.SOUL_BINDABLE_ITEM, EGG_BINDABLE_PROVIDER, Items.EGG);
        event.registerItem(EnderIOCapabilities.BINDABLE_SOUL_SOURCE, SPAWN_EGG_BINDABLE_SOUL_SOURCE, allItems);
    }
}
