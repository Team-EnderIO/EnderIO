package com.enderio.core.common.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockEntityTypeDeferredRegister extends DeferredRegister<BlockEntityType<?>> {

    public static BlockEntityTypeDeferredRegister create(String namespace) {
        return new BlockEntityTypeDeferredRegister(namespace);
    }

    protected BlockEntityTypeDeferredRegister(String namespace) {
        super(Registries.BLOCK_ENTITY_TYPE, namespace);
    }

    // Some shortcuts
    @SafeVarargs
    public final <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name,
        BlockEntityType.BlockEntitySupplier<? extends T> factory, Supplier<Block>... validBlocks) {
        return register(name, () -> new BlockEntityType<>(factory,
            Arrays.stream(validBlocks).map(Supplier::get).toArray(Block[]::new)));
    }

    // Builder for complex block entities with multiple valid blocks or capabilities.
    public <T extends BlockEntity> Builder<T> builder(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory) {
        return new Builder<>(name, factory);
    }

    @SafeVarargs
    public final <T extends BlockEntity> Builder<T> builder(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory,
        Supplier<Block>... validBlocks) {
        return new Builder<>(name, factory, Arrays.asList(validBlocks));
    }

    @Override
    protected <I extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, I> createHolder(ResourceKey<? extends Registry<BlockEntityType<?>>> registryKey,
        Identifier key) {
        //noinspection unchecked,rawtypes
        return (DeferredHolder<BlockEntityType<?>, I>) new BlockEntityTypeDeferredHolder(registryKey, key);
    }

    @Override
    public void register(IEventBus bus) {
        super.register(bus);
        bus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (var entry : getEntries()) {
            if (entry instanceof BlockEntityTypeDeferredHolder<?> holder) {
                holder.registerCapabilityProviders(event);
            }
        }
    }

    public class Builder<T extends BlockEntity> {
        private final String name;
        private final BlockEntityType.BlockEntitySupplier<? extends T> factory;
        private final List<Supplier<Block>> validBlocks = new ArrayList<>();
        private final Set<BlockEntityTypeDeferredHolder.AttachedCapability<T, ?, ?>> attachedCapabilities = new HashSet<>();

        private Builder(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory) {
            this.name = name;
            this.factory = factory;
        }

        private Builder(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, List<Supplier<Block>> validBlocks) {
            this.name = name;
            this.factory = factory;
            this.validBlocks.addAll(validBlocks);
        }

        public Builder<T> validBlock(Supplier<Block> validBlock) {
            validBlocks.add(validBlock);
            return this;
        }

        public <TCap, TContext> Builder<T> capability(BlockCapability<TCap, TContext> capability, ICapabilityProvider<? super T, TContext, TCap> provider) {
            attachedCapabilities.add(new BlockEntityTypeDeferredHolder.AttachedCapability<>(capability, provider));
            return this;
        }

        public Builder<T> apply(Consumer<Builder<T>> applicator) {
            applicator.accept(this);
            return this;
        }

        public DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> build() {
            var holder = (BlockEntityTypeDeferredHolder<T>)register(name, () -> new BlockEntityType<T>(factory,
                validBlocks.stream().map(Supplier::get).toArray(Block[]::new)));

            holder.attachedCapabilities = attachedCapabilities;

            return holder;
        }
    }
}
