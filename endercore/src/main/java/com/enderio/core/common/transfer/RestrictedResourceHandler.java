package com.enderio.core.common.transfer;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;
import java.util.function.Supplier;

public class RestrictedResourceHandler<T extends Resource> extends DelegatingResourceHandler<T> {

    private final int[] validIndices;

    public static <T extends Resource> RestrictedResourceHandler<T> create(ResourceHandler<T> delegate, int... validIndices) {
        return new RestrictedResourceHandler<>(delegate, validIndices);
    }

    public static <T extends Resource> RestrictedResourceHandler<T> create(Supplier<ResourceHandler<T>> delegate, int... validIndices) {
        return new RestrictedResourceHandler<>(delegate, validIndices);
    }

    private RestrictedResourceHandler(ResourceHandler<T> delegate, int[] validIndices) {
        this(() -> delegate, validIndices);
    }

    private RestrictedResourceHandler(Supplier<ResourceHandler<T>> delegate, int[] validIndices) {
        super(delegate);
        this.validIndices = validIndices;

        if (validIndices.length == 0) {
            throw new IllegalArgumentException("Valid indices cannot be empty");
        }

        int delegateSize = delegate.get().size();
        for (int index : validIndices) {
            if (index >= delegateSize) {
                throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for handler of size " + delegateSize);
            }
        }
    }

    @Override
    public int size() {
        return validIndices.length;
    }

    @Override
    protected int convertIndex(int index) {
        Objects.checkIndex(index, size());
        return validIndices[index];
    }

    @Override
    public int extract(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int extracted = 0;
        ResourceHandler<T> handler = getDelegate();
        for (int i = 0; i < validIndices.length; i++) {
            extracted += handler.extract(validIndices[i], resource, amount - extracted, transaction);
            if (extracted == amount) {
                return extracted;
            }
        }

        return extracted;
    }

    @Override
    public int insert(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int inserted = 0;
        ResourceHandler<T> handler = getDelegate();
        for (int i = 0; i < validIndices.length; i++) {
            inserted += handler.insert(validIndices[i], resource, amount - inserted, transaction);
            if (inserted == amount) {
                return inserted;
            }
        }

        return inserted;
    }
}
