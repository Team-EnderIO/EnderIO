package com.enderio.core.common.storage;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * A resource handler which uses a {@link ResourceStorageLayout} to define its slots and {@link ResourceSlotId}s to reference them.
 */
public interface EnderResourceHandler<T extends Resource> extends ResourceHandler<T>, IndexModifier<T> {

    ResourceStorageLayout<T, ?> layout();

    @ApiStatus.NonExtendable
    default int indexOf(ResourceSlotId<T> slotId) {
        return slotId.index(layout());
    }

    @ApiStatus.NonExtendable
    default T getResource(ResourceSlotId<T> slotId) {
        return this.getResource(indexOf(slotId));
    }

    @ApiStatus.NonExtendable
    default int getAmountAsInt(ResourceSlotId<T> slotId) {
        return this.getAmountAsInt(indexOf(slotId));
    }

    @ApiStatus.NonExtendable
    default long getAmountAsLong(ResourceSlotId<T> slotId) {
        return this.getAmountAsLong(indexOf(slotId));
    }

    @ApiStatus.NonExtendable
    default int getCapacityAsInt(ResourceSlotId<T> slotId, T resource) {
        return this.getCapacityAsInt(indexOf(slotId), resource);
    }

    @ApiStatus.NonExtendable
    default long getCapacityAsLong(ResourceSlotId<T> slotId, T resource) {
        return this.getCapacityAsLong(indexOf(slotId), resource);
    }

    @ApiStatus.NonExtendable
    default boolean isValid(ResourceSlotId<T> slotId, T resource) {
        return this.isValid(indexOf(slotId), resource);
    }

    @ApiStatus.NonExtendable
    default void set(ResourceSlotId<T> slotId, T resource, int amount) {
        this.set(indexOf(slotId), resource, amount);
    }

    @ApiStatus.NonExtendable
    default int insert(ResourceSlotId<T> slotId, T resource, int amount, TransactionContext transaction) {
        return this.insert(indexOf(slotId), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int extract(ResourceSlotId<T> slotId, T resource, int amount, TransactionContext transaction) {
        return this.extract(indexOf(slotId), resource, amount, transaction);
    }
}

