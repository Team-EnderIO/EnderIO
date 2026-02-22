package com.enderio.core.common.storage;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * A resource handler which provides write-access to its slots and helpers for EnderCore's layout system.
 * @param <T> The type of resource being stored.
 */
public interface ResourceStorage<T extends Resource> extends ResourceHandler<T> {

    ResourceStorageLayout<T, ?> layout();

    void set(int index, T resource, int amount);
    void setTransactional(int index, T resource, int amount, TransactionContext transaction);

    // For use in block implementations - bypasses layout slot settings
    int internalInsert(int index, T resource, int amount, TransactionContext transaction);

    default int internalInsert(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int inserted = 0;
        int size = size();
        for (int index = 0; index < size; index++) {
            inserted += internalInsert(index, resource, amount - inserted, transaction);
            if (inserted == amount) break;
        }
        return inserted;
    }

    int internalExtract(int index, T resource, int amount, TransactionContext transaction);

    default int internalExtract(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int extracted = 0;
        int size = size();
        for (int index = 0; index < size; index++) {
            extracted += internalExtract(index, resource, amount - extracted, transaction);
            if (extracted == amount) break;
        }
        return extracted;
    }

    @ApiStatus.NonExtendable
    default T getResource(ResourceSlotId<T> slotId) {
        return this.getResource(slotId.index(layout()));
    }

    @ApiStatus.NonExtendable
    default int getAmountAsInt(ResourceSlotId<T> slotId) {
        return this.getAmountAsInt(slotId.index(layout()));
    }

    @ApiStatus.NonExtendable
    default long getAmountAsLong(ResourceSlotId<T> slotId) {
        return this.getAmountAsLong(slotId.index(layout()));
    }

    @ApiStatus.NonExtendable
    default int getCapacityAsInt(ResourceSlotId<T> slotId, T resource) {
        return this.getCapacityAsInt(slotId.index(layout()), resource);
    }

    @ApiStatus.NonExtendable
    default long getCapacityAsLong(ResourceSlotId<T> slotId, T resource) {
        return this.getCapacityAsLong(slotId.index(layout()), resource);
    }

    @ApiStatus.NonExtendable
    default boolean isValid(ResourceSlotId<T> slotId, T resource) {
        return this.isValid(slotId.index(layout()), resource);
    }

    @ApiStatus.NonExtendable
    default void set(ResourceSlotId<T> slotId, T resource, int amount) {
        this.set(slotId.index(layout()), resource, amount);
    }

    @ApiStatus.NonExtendable
    default int insert(ResourceSlotId<T> slotId, T resource, int amount, TransactionContext transaction) {
        return this.insert(slotId.index(layout()), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int internalInsert(ResourceSlotId<T> slotId, T resource, int amount, TransactionContext transaction) {
        return this.internalInsert(slotId.index(layout()), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int extract(ResourceSlotId<T> slotId, T resource, int amount, TransactionContext transaction) {
        return this.extract(slotId.index(layout()), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int internalExtract(ResourceSlotId<T> slotId, T resource, int amount, TransactionContext transaction) {
        return this.internalExtract(slotId.index(layout()), resource, amount, transaction);
    }
}

