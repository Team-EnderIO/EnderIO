package com.enderio.core.common.storage.delegates;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.function.Supplier;

public class ModifiableDelegatingResourceHandler<H extends ResourceHandler<T> & IndexModifier<T>, T extends Resource> extends DelegatingResourceHandler<T> implements IndexModifier<T> {
    public ModifiableDelegatingResourceHandler(H delegate) {
        super(delegate);
    }

    public ModifiableDelegatingResourceHandler(Supplier<H> delegate) {
        super(delegate::get);
    }

    @SuppressWarnings("unchecked")
    @Override
    public H getDelegate() {
        return (H) super.getDelegate();
    }

    @Override
    public void set(int index, T resource, int amount) {
        getDelegate().set(index, resource, amount);
    }
}
