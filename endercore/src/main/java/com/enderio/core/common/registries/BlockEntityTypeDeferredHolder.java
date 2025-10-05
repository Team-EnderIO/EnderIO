package com.enderio.core.common.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public class BlockEntityTypeDeferredHolder<T extends BlockEntity> extends DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> {
    Set<AttachedCapability<T, ?, ?>> attachedCapabilities = Set.of();

    public BlockEntityTypeDeferredHolder(ResourceKey<? extends Registry<BlockEntityType<?>>> registryKey, ResourceLocation valueName) {
        this(ResourceKey.create(registryKey, valueName));
    }

    protected BlockEntityTypeDeferredHolder(ResourceKey<BlockEntityType<?>> key) {
        super(key);
    }

    public void registerCapabilityProviders(RegisterCapabilitiesEvent event) {
        for (AttachedCapability<T, ?, ?> capabilityProvider : attachedCapabilities) {
            capabilityProvider.registerProvider(event, value());
        }
    }

    public record AttachedCapability<T extends BlockEntity, TCap, TContext>(
        BlockCapability<TCap, TContext> capability,
        ICapabilityProvider<? super T, TContext, TCap> provider) {

        private void registerProvider(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
            event.registerBlockEntity(capability, type, provider);
        }
    }
}
