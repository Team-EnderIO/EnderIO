package com.enderio.core.common.storage.delegates;

import com.enderio.core.common.storage.EnderResourceHandler;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.Supplier;

public class ExternalDelegatingResourceStorage<T extends Resource> extends DelegatingResourceHandler<T> {
    public ExternalDelegatingResourceStorage(EnderResourceHandler<T> delegate) {
        super(delegate);
    }

    public ExternalDelegatingResourceStorage(Supplier<EnderResourceHandler<T>> delegate) {
        super(delegate::get);
    }

    @Override
    public EnderResourceHandler<T> getDelegate() {
        return (EnderResourceHandler<T>) super.getDelegate();
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        if (!getDelegate().layout().slotConfig(index).canInsertExternal()) {
            return 0;
        }

        return super.insert(index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        if (!getDelegate().layout().slotConfig(index).canExtractExternal()) {
            return 0;
        }

        return super.extract(index, resource, amount, transaction);
    }
}
