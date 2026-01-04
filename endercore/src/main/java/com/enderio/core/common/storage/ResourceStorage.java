package com.enderio.core.common.storage;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * A resource handler which provides write-access to its slots and helpers for EnderCore's layout system.
 * @param <T> The type of resource being stored.
 */
// TODO: Rename to something that reflects that this is basically just a resource handler with a layout now.
// Perhaps LayoutedResourceHandler?
public interface ResourceStorage<T extends Resource> extends ResourceHandler<T> {

    ResourceStorageLayout<T, ?> layout();

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
    default int insert(ResourceSlotId<T> slotId, T resource, int amount, TransactionContext transaction) {
        return this.insert(slotId.index(layout()), resource, amount, transaction);
    }

    @ApiStatus.NonExtendable
    default int extract(ResourceSlotId<T> slotId, T resource, int amount, TransactionContext transaction) {
        return this.extract(slotId.index(layout()), resource, amount, transaction);
    }
}

