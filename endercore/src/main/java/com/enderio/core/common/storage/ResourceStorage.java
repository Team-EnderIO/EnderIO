package com.enderio.core.common.storage;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.neoforged.neoforge.transfer.ResourceHandler;
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

    // For use in block implementations - bypasses layout slot settings
    int internalInsert(int index, T resource, int amount, TransactionContext transaction);
    int internalExtract(int index, T resource, int amount, TransactionContext transaction);

    // region Single-Slot Access

    @ApiStatus.NonExtendable
    default T getResource(SingleResourceSlotKey<T> key) {
        return this.getResource(layout().indexOf(key));
    }

    @ApiStatus.NonExtendable
    default int getAmountAsInt(SingleResourceSlotKey<T> key) {
        return this.getAmountAsInt(layout().indexOf(key));
    }

    @ApiStatus.NonExtendable
    default long getAmountAsLong(SingleResourceSlotKey<T> key) {
        return this.getAmountAsLong(layout().indexOf(key));
    }

    @ApiStatus.NonExtendable
    default int getCapacityAsInt(SingleResourceSlotKey<T> key, T resource) {
        return this.getCapacityAsInt(layout().indexOf(key), resource);
    }

    @ApiStatus.NonExtendable
    default long getCapacityAsLong(SingleResourceSlotKey<T> key, T resource) {
        return this.getCapacityAsLong(layout().indexOf(key), resource);
    }

    @ApiStatus.NonExtendable
    default boolean isValid(SingleResourceSlotKey<T> key, T resource) {
        return this.isValid(layout().indexOf(key), resource);
    }

    @ApiStatus.NonExtendable
    default void set(SingleResourceSlotKey<T> key, T resource, int amount) {
        this.set(layout().indexOf(key), resource, amount);
    }

    @ApiStatus.NonExtendable
    default int insert(SingleResourceSlotKey<T> key, T resource, int amount, TransactionContext transaction) {
        return this.insert(layout().indexOf(key), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int internalInsert(SingleResourceSlotKey<T> key, T resource, int amount, TransactionContext transaction) {
        return this.internalInsert(layout().indexOf(key), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int extract(SingleResourceSlotKey<T> key, T resource, int amount, TransactionContext transaction) {
        return this.extract(layout().indexOf(key), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int internalExtract(SingleResourceSlotKey<T> key, T resource, int amount, TransactionContext transaction) {
        return this.internalExtract(layout().indexOf(key), resource, amount, transaction);
    }

    // endregion
    
    // region Multi-Slot Access

    @ApiStatus.NonExtendable
    default T getResource(MultiResourceSlotKey<T> key, int index) {
        return this.getResource(layout().indexOf(key, index));
    }

    @ApiStatus.NonExtendable
    default int getAmountAsInt(MultiResourceSlotKey<T> key, int index) {
        return this.getAmountAsInt(layout().indexOf(key, index));
    }

    @ApiStatus.NonExtendable
    default long getAmountAsLong(MultiResourceSlotKey<T> key, int index) {
        return this.getAmountAsLong(layout().indexOf(key, index));
    }

    @ApiStatus.NonExtendable
    default int getCapacityAsInt(MultiResourceSlotKey<T> key, int index, T resource) {
        return this.getCapacityAsInt(layout().indexOf(key, index), resource);
    }

    @ApiStatus.NonExtendable
    default long getCapacityAsLong(MultiResourceSlotKey<T> key, int index, T resource) {
        return this.getCapacityAsLong(layout().indexOf(key, index), resource);
    }

    @ApiStatus.NonExtendable
    default boolean isValid(MultiResourceSlotKey<T> key, int index, T resource) {
        return this.isValid(layout().indexOf(key, index), resource);
    }

    @ApiStatus.NonExtendable
    default void set(ResourceStorage<T> storage, MultiResourceSlotKey<T> key, int index, T resource, int amount) {
        storage.set(layout().indexOf(key, index), resource, amount);
    }

    @ApiStatus.NonExtendable
    default int insert(MultiResourceSlotKey<T> key, int index, T resource, int amount, TransactionContext transaction) {
        return this.insert(layout().indexOf(key, index), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int internalInsert(MultiResourceSlotKey<T> key, int index, T resource, int amount, TransactionContext transaction) {
        return this.internalInsert(layout().indexOf(key, index), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int extract(MultiResourceSlotKey<T> key, int index, T resource, int amount, TransactionContext transaction) {
        return this.extract(layout().indexOf(key, index), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int internalExtract(MultiResourceSlotKey<T> key, int index, T resource, int amount, TransactionContext transaction) {
        return this.internalExtract(layout().indexOf(key, index), resource, amount, transaction);
    }
    
    // endregion
}

