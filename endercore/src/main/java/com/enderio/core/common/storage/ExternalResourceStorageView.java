package com.enderio.core.common.storage;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;
import java.util.function.Supplier;

public class ExternalResourceStorageView<T extends Resource> extends DelegatingResourceHandler<T> {
    public ExternalResourceStorageView(ResourceStorage<T> delegate) {
        super(delegate);
    }

    public ExternalResourceStorageView(Supplier<ResourceStorage<T>> delegate) {
        super(delegate::get);
    }

    @Override
    public ResourceStorage<T> getDelegate() {
        return (ResourceStorage<T>) super.getDelegate();
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonNegative(amount);

        if (!getDelegate().layout().slotConfig(index).externalRules().canInsert(resource)) {
            return 0;
        }

        return super.insert(index, resource, amount, transaction);
    }

    @Override
    public int insert(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int inserted = 0;
        int size = size();
        for (int index = 0; index < size; index++) {
            inserted += insert(index, resource, amount - inserted, transaction);
            if (inserted == amount) {
                break;
            }
        }

        return inserted;
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonNegative(amount);

        if (!getDelegate().layout().slotConfig(index).externalRules().canExtract(resource)) {
            return 0;
        }

        return super.extract(index, resource, amount, transaction);
    }

    @Override
    public int extract(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int extracted = 0;
        int size = size();
        for (int index = 0; index < size; index++) {
            extracted += extract(index, resource, amount - extracted, transaction);
            if (extracted == amount) {
                break;
            }
        }

        return extracted;
    }
}
