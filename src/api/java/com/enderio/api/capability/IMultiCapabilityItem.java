package com.enderio.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.Nullable;

/**
 * Implement for an item that should use the {@link MultiCapabilityProvider} when initializing capabilities.
 */
public interface IMultiCapabilityItem extends IItemExtension {
    @Nullable
    @Override
    default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return initCapabilities(stack, nbt, new MultiCapabilityProvider());
    }

    @Nullable
    MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider);
}
