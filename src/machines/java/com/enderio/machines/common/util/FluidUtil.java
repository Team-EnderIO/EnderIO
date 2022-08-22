package com.enderio.machines.common.util;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Helper class for dealing with Fluid related things.
 */
public class FluidUtil {
    public static int INVALID_OPERATION = -1;

    /**
     * Gets an {@link IFluidHandlerItem} from an {@link ItemStack} or null if it's
     * not present.
     * 
     * @param itemStack {@link ItemStack} to get the {@link IFluidHandlerItem} from
     * @return An {@link IFluidHandlerItem} or null
     */
    public static IFluidHandlerItem getIFluidHandlerItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        Optional<IFluidHandlerItem> fluidHandlerCap = itemStack
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
        if (!fluidHandlerCap.isPresent()) {
            return null;
        }

        return fluidHandlerCap.get();
    }

    /**
     * Fill fluid into a {@link FluidTank} from a bucket.
     *
     * @param fluidTank The {@link FluidTank} to fill into.
     * @param bucket    The {@link BucketItem} to drain from.
     * @return {@link FluidUtil.INVALID_OPERATION} if 'fluidTank' or 'itemStack' is
     *         null; 0 if the fluid cannot be filled; otherwise the amount of fluid
     *         that is moved.
     */
    public static int fillTankFromBucket(@Nullable FluidTank fluidTank, @Nullable BucketItem bucket) {
        if (fluidTank == null || bucket == null) {
            return INVALID_OPERATION;
        }

        int filled = fluidTank.fill(new FluidStack(bucket.getFluid(), FluidType.BUCKET_VOLUME),
                IFluidHandler.FluidAction.SIMULATE);
        if (filled != FluidType.BUCKET_VOLUME) {
            return 0;
        }

        fluidTank.fill(new FluidStack(bucket.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
        return filled;
    }

    /**
     * Fill fluid into a {@link FluidTank} from an {@link ItemStack}.
     * 
     * @param fluidTank The {@link FluidTank} to fill into.
     * @param itemStack The {@link ItemStack} to drain from.
     * @return {@link FluidUtil.INVALID_OPERATION} if 'fluidTank' or 'itemStack' is
     *         null, or 'itemStack' does not have fluid capabilities; 0 if the fluid
     *         cannot be filled; otherwise the amount of fluid that is moved.
     */
    public static Pair<Integer, IFluidHandlerItem> fillTankFromItem(@Nullable FluidTank fluidTank,
            @Nullable ItemStack itemStack) {
        if (fluidTank == null || itemStack == null) {
            return Pair.of(INVALID_OPERATION, null);
        }

        IFluidHandlerItem fluidHandler = FluidUtil.getIFluidHandlerItem(itemStack);
        return Pair.of(fillTankFromItem(fluidTank, fluidHandler), fluidHandler);
    }

    /**
     * Fill fluid into a {@link FluidTank} from an {@link IFluidHandlerItem}.
     * 
     * @param fluidTank The {@link FluidTank} to fill into.
     * @param item      The {@link IFluidHandlerItem} to drain from.
     * @return {@link FluidUtil.INVALID_OPERATION} if 'fluidTank' or 'item' is null;
     *         0 if the fluid cannot be filled; otherwise the amount of fluid that
     *         is moved.
     */
    public static int fillTankFromItem(@Nullable FluidTank fluidTank,
            @Nullable IFluidHandlerItem item) {
        if (fluidTank == null || item == null) {
            return INVALID_OPERATION;
        }

        return moveFluids(item, fluidTank, fluidTank.getCapacity());
    }

    /**
     * Drain fluid from a {@link FluidTank} into a bucket.
     *
     * @param fluidTank The {@link FluidTank} to drain from.
     * @return A {@link Pair} of whether the drain succeeds, and if so the
     *         {@link FluidStack} from the drained tank.
     */
    public static Pair<Boolean, FluidStack> drainTankWithBucket(@Nullable FluidTank fluidTank) {
        return drainTankWithBucket(fluidTank, null);
    }

    /**
     * Drain fluid from a {@link FluidTank} into a bucket.
     *
     * @param fluidTank   The {@link FluidTank} to drain from.
     * @param shouldDrain A {@link Predicate} to check whether the tank should drain
     *                    the particular fluid. Can be null.
     * @return A {@link Pair} of whether the drain succeeds, and if so the
     *         {@link FluidStack} from the drained tank.
     */
    public static Pair<Boolean, FluidStack> drainTankWithBucket(@Nullable FluidTank fluidTank,
            @Nullable Predicate<FluidStack> shouldDrain) {
        if (fluidTank == null || fluidTank.isEmpty()) {
            return Pair.of(false, null);
        }

        FluidStack stack = fluidTank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
        if (stack.getAmount() != FluidType.BUCKET_VOLUME) {
            return Pair.of(false, null);
        }

        if (shouldDrain != null && !shouldDrain.test(stack)) {
            return Pair.of(false, null);
        }

        fluidTank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
        return Pair.of(true, stack);
    }

    /**
     * Drain fluid from a {@link FluidTank} into an {@link ItemStack}.
     * 
     * @param fluidTank The {@link FluidTank} to drain from.
     * @param itemStack The {@link ItemStack} to fill into.
     * @return {@link FluidUtil.INVALID_OPERATION} if 'fluidTank' or 'itemStack' is
     *         null, or 'itemStack' does not have fluid capabilities; 0 if the fluid
     *         cannot be drained; otherwise the amount of fluid that is moved.
     */
    public static Pair<Integer, IFluidHandlerItem> drainTankWithItem(@Nullable FluidTank fluidTank,
            @Nullable ItemStack itemStack) {
        if (fluidTank == null || itemStack == null) {
            return Pair.of(INVALID_OPERATION, null);
        }

        IFluidHandlerItem fluidHandler = FluidUtil.getIFluidHandlerItem(itemStack);
        return Pair.of(drainTankWithItem(fluidTank, fluidHandler), fluidHandler);
    }

    /**
     * Drain fluid from a {@link FluidTank} into an {@link IFluidHandlerItem}.
     * 
     * @param fluidTank The {@link FluidTank} to drain from.
     * @param item      The {@link IFluidHandlerItem} to fill into.
     * @return {@link FluidUtil.INVALID_OPERATION} if 'fluidTank' or 'item' is null;
     *         0 if the fluid cannot be drained; otherwise the amount of fluid that
     *         is moved.
     */
    public static int drainTankWithItem(@Nullable FluidTank fluidTank,
            @Nullable IFluidHandlerItem item) {
        if (fluidTank == null || item == null) {
            return INVALID_OPERATION;
        }

        return moveFluids(fluidTank, item, fluidTank.getFluidAmount());
    }

    /**
     * Moves a max amount fluid from one {@link IFluidHandlerItem} to another.
     * 
     * @param from     The {@link IFluidHandlerItem} to drain from.
     * @param to       The {@link IFluidHandlerItem} to fill into.
     * @param maxDrain The max amount of fluid to drain.
     * @return {@link FluidUtil.INVALID_OPERATION} if 'from' or 'to' is null, or
     *         'maxDrain' is below zero; 0 if the
     *         fluid cannot be drained; otherwise the amount of fluid that is moved.
     */
    public static int moveFluids(@Nullable IFluidHandler from, @Nullable IFluidHandler to, int maxDrain) {
        if (from == null || to == null || maxDrain < 0) {
            return INVALID_OPERATION;
        }

        // Simulate the transfer.
        FluidStack stack = from.drain(maxDrain, FluidAction.SIMULATE);
        if (stack.isEmpty()) {
            return 0;
        }

        // Execute the transfer.
        int filled = to.fill(stack, FluidAction.EXECUTE);
        stack.setAmount(filled);
        from.drain(stack, FluidAction.EXECUTE);
        return filled;
    }
}
