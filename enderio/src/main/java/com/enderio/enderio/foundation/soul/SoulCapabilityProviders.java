package com.enderio.enderio.foundation.soul;

import com.enderio.enderio.api.soul.SoulResource;
import com.enderio.enderio.api.soul.binding.BindableSoulSource;
import com.enderio.enderio.api.soul.binding.ComponentSoulBindable;
import com.enderio.enderio.api.soul.binding.SingleSlotSoulHandlerBindableSoulSource;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.api.soul.storage.SimpleItemAccessSoulHandler;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;

public class SoulCapabilityProviders {

    public static final ICapabilityProvider<ItemStack, ItemAccess, SoulBindable> COMPONENT_SOUL_BINDABLE_PROVIDER = (_,
        itemAccess) -> new ComponentSoulBindable(itemAccess, EIODataComponents.SOUL);

    public static final ICapabilityProvider<ItemStack, ItemAccess, BindableSoulSource> SINGLE_SLOT_SOUL_HANDLER_BINDABLE_SOUL_SOURCE =
        (_, itemAccess) -> new SingleSlotSoulHandlerBindableSoulSource(itemAccess);

    public static final ICapabilityProvider<ItemStack, ItemAccess, ResourceHandler<SoulResource>> SIMPLE_SOUL_HANDLER =
        (_, itemAccess) -> new SimpleItemAccessSoulHandler(itemAccess, EIODataComponents.SOUL_CONTENT);
}
