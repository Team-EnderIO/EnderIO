package com.enderio.enderio.foundation.soul;

import com.enderio.enderio.api.soul.binding.ComponentSoulBindable;
import com.enderio.enderio.api.soul.binding.ReadOnlyComponentSoulBindable;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.api.soul.storage.SingleComponentSoulHandler;
import com.enderio.enderio.api.soul.storage.SoulHandler;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class SoulCapabilityProviders {

    public static final ICapabilityProvider<ItemStack, Void, SoulBindable> COMPONENT_SOUL_BINDABLE_PROVIDER = (stack,
        ctx) -> new ComponentSoulBindable(stack, EIODataComponents.SOUL);

    public static final ICapabilityProvider<ItemStack, Void, SoulBindable> READ_ONLY_COMPONENT_SOUL_BINDABLE_PROVIDER = (stack,
        ctx) -> new ReadOnlyComponentSoulBindable(stack, EIODataComponents.SOUL);

    // Only allows access to the capability if one storage is in the stack.
    public static final ICapabilityProvider<ItemStack, Void, SoulHandler> SINGLE_COMPONENT_SOUL_HANDLER_PROVIDER = (stack,
        ctx) -> new SingleComponentSoulHandler(stack, EIODataComponents.SOUL);
}
