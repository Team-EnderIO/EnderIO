package com.enderio.core.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class EnderRegistry<T> extends DeferredRegister<T> {
    protected EnderRegistry(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        super(registryKey, namespace);
    }

    @Override
    public <I extends T> EnderDeferredObject<T, I> register(String name, Supplier<? extends I> sup) {
        return this.register(name, key -> sup.get());
    }

    @Override
    public <I extends T> EnderDeferredObject<T, I> register(String name, Function<ResourceLocation, ? extends I> func) {
        return (EnderDeferredObject<T, I>) super.register(name, func);
    }

    public static <T> EnderRegistry<T> createRegistry(Registry<T> registry, String namespace) {
        return new EnderRegistry<>(registry.key(), namespace);
    }

    @Override
    protected <I extends T> DeferredHolder<T, I> createHolder(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation key) {
        return EnderDeferredObject.create(ResourceKey.create(registryKey, key));
    }
}
