package com.enderio.core.common.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class EnderDeferredRegister<T, U extends DeferredHolder<T, ? extends T>> extends DeferredRegister<T> {
    protected EnderDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        super(registryKey, namespace);
    }

    protected abstract U wrapHolder(DeferredHolder<T, ? extends T> holder);

    @Override
    public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> sup) {
        return super.register(name, sup);
    }

    @Override
    public <I extends T> DeferredHolder<T, I> register(String name, Function<Identifier, ? extends I> func) {
        return super.register(name, func);
    }
}
