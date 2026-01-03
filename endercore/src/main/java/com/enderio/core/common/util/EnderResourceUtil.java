package com.enderio.core.common.util;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class EnderResourceUtil {

    // region Item Helpers

    public static ItemStack getItemStack(ResourceHandler<ItemResource> handler, int index) {
        ItemResource resource = handler.getResource(index);
        int amount = handler.getAmountAsInt(index);
        return resource.toStack(amount);
    }

    public static ItemStack getItemStack(ResourceStorage<ItemResource> storage, ResourceSlotId<ItemResource> slotId) {
        return getItemStack(storage, slotId.index(storage.layout()));
    }

    public static ItemAccess getItemAccess(ResourceHandler<ItemResource> handler, int index) {
        return ItemAccess.forHandlerIndex(handler, index);
    }

    public static ItemAccess getItemAccessStrict(ResourceHandler<ItemResource> handler, int index) {
        return ItemAccess.forHandlerIndexStrict(handler, index);
    }

    public static ItemAccess getItemAccess(ResourceStorage<ItemResource> storage, ResourceSlotId<ItemResource> slotId) {
        return getItemAccess(storage, slotId.index(storage.layout()));
    }

    public static ItemAccess getItemAccessStrict(ResourceStorage<ItemResource> storage, ResourceSlotId<ItemResource> slotId) {
        return getItemAccessStrict(storage, slotId.index(storage.layout()));
    }

    // endregion

    // region Fluid Helpers

    public static FluidStack getFluidStack(ResourceHandler<FluidResource> handler, int index) {
        FluidResource resource = handler.getResource(index);
        int amount = handler.getAmountAsInt(index);
        return resource.toStack(amount);
    }

    public static FluidStack getFluidStack(ResourceStorage<FluidResource> storage, ResourceSlotId<FluidResource> slotId) {
        return getFluidStack(storage, slotId.index(storage.layout()));
    }

    // endregion

    // region Move resources between slots.

    public static boolean tryMoveItem(ResourceStorage<ItemResource> storage, int from, int to, @Nullable TransactionContext transaction) {
        return tryMoveResource(storage, from, to, ItemResource.EMPTY, transaction);
    }

    public static boolean tryMoveItem(ResourceStorage<ItemResource> storage, ResourceSlotId<ItemResource> from, ResourceSlotId<ItemResource> to, @Nullable TransactionContext transaction) {
        return tryMoveResource(storage, from, to, ItemResource.EMPTY, transaction);
    }

    public static boolean tryMoveFluid(ResourceStorage<FluidResource> storage, int from, int to, @Nullable TransactionContext transaction) {
        return tryMoveResource(storage, from, to, FluidResource.EMPTY, transaction);
    }

    public static boolean tryMoveFluid(ResourceStorage<FluidResource> storage, ResourceSlotId<FluidResource> from, ResourceSlotId<FluidResource> to, @Nullable TransactionContext transaction) {
        return tryMoveResource(storage, from, to, FluidResource.EMPTY, transaction);
    }

    /**
     * Helper to move all the resources in one slot to another.
     * @param storage
     * @param from
     * @param to
     * @param <T>
     */
    public static <T extends Resource> boolean tryMoveResource(ResourceStorage<T> storage, int from, int to, T emptyResource, @Nullable TransactionContext transaction) {
        T resourceToMove = storage.getResource(from);
        int amountToMove = storage.getAmountAsInt(from);

        try (Transaction subTransaction = Transaction.open(transaction)) {
            int amountMoved = storage.internalInsert(to, resourceToMove, amountToMove, subTransaction);
            if (amountMoved != amountToMove) {
                return false;
            }

            storage.setTransactional(from, emptyResource, 0, subTransaction);
            subTransaction.commit();
            return true;
        }
    }

    public static <T extends Resource> boolean tryMoveResource(ResourceStorage<T> storage, ResourceSlotId<T> from, ResourceSlotId<T> to, T emptyResource, @Nullable TransactionContext transaction) {
        return tryMoveResource(storage, from.index(storage.layout()), to.index(storage.layout()), emptyResource, transaction);
    }

    // endregion

    // region Move from one storage into specific slot of another storage.

    public static <T extends Resource> int moveInto(
        @Nullable ResourceHandler<T> from,
        @Nullable ResourceHandler<T> to,
        int toIndex,
        Predicate<T> filter,
        int amount,
        @Nullable TransactionContext transaction) {

        Objects.requireNonNull(filter, "Filter may not be null");
        TransferPreconditions.checkNonNegative(amount);
        if (from == null || to == null || amount == 0) return 0;

        return ResourceHandlerUtil.move(from, RangedResourceHandler.ofSingleIndex(to, toIndex), filter, amount, transaction);
    }

    public static <T extends Resource> int moveInto(
        @Nullable ResourceHandler<T> from,
        @Nullable ResourceStorage<T> to,
        ResourceSlotId<T> toId,
        Predicate<T> filter,
        int amount,
        @Nullable TransactionContext transaction) {

        Objects.requireNonNull(filter, "Filter may not be null");
        TransferPreconditions.checkNonNegative(amount);
        if (from == null || to == null || amount == 0) return 0;

        return moveInto(from, to, toId.index(to.layout()), filter, amount, transaction);
    }

    // endregion
}
