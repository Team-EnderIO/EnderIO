package com.enderio.enderio.api.soul;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Some helper methods for interacting with {@link SoulBindable} capabilities.
 */
@ApiStatus.AvailableSince("8.0")
@ApiStatus.Experimental
public class SoulBoundUtils {
    /**
     * Test whether the {@code itemStack} supports soul binding.
     * @implNote This tests both whether the {@code itemStack} supports the capability and that {@link SoulBindable#canBeBound()} is true.
     * @param itemStack the item to test.
     * @return whether the item can be bound.
     */
    @Deprecated
    public static boolean canBindSoul(ItemStack itemStack) {
        var soulBindable = ItemAccess.forStack(itemStack).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable == null) {
            return false;
        }

        return soulBindable.canBeBound();
    }

    /**
     * Test whether the {@code soul} can be bound to the {@code itemStack}.
     * @param itemStack the item stack to bind to.
     * @param soul the soul to test.
     * @return whether it can be bound.
     */
    @Deprecated
    public static boolean canBindSoul(ItemStack itemStack, Soul soul) {
        var soulBindable = ItemAccess.forStack(itemStack).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable == null) {
            return false;
        }

        return soulBindable.canBeBound() && soulBindable.isSoulValid(soul);
    }

    /**
     * Get the soul bound to the {@code itemStack} or {@link Soul#EMPTY}.
     * @param itemStack the stack to get a soul for.
     * @return the bound soul, or {@link Soul#EMPTY} if there is no soul bound.
     */
    @Deprecated
    public static Soul getBoundSoul(ItemStack itemStack) {
        var soulBindable = ItemAccess.forStack(itemStack).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable == null) {
            return Soul.EMPTY;
        }

        return soulBindable.boundSoul();
    }

    /**
     * Whether the {@code itemStack} has a soul bound to it.
     * @param itemStack the stack to check.
     * @return whether there is a bound soul.
     */
    @Deprecated
    public static boolean isBound(ItemStack itemStack) {
        var soulBindable = ItemAccess.forStack(itemStack).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable == null) {
            return false;
        }

        return soulBindable.isBound();
    }

    /**
     * Attempt to bind a {@code soul} to the {@code itemStack}.
     * @param itemStack the stack to bind to.
     * @param soul the soul to be bound.
     * @return whether the binding was successful
     */
    @Deprecated
    public static boolean tryBindSoul(ItemStack itemStack, Soul soul) {
        var soulBindable = ItemAccess.forStack(itemStack).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable == null) {
            return false;
        }

        // Short-circuit if we're already bound.
        if (Soul.isSameEntitySameTag(soulBindable.boundSoul(), soul)) {
            return true;
        }

        return tryBindSoul(soulBindable, soul);
    }

    /**
     * Attempt to bind a {@code soul} to the {@code soulBindable}.
     * @param soulBindable the object to bind to.
     * @param soul the soul to be bound.
     * @return whether the binding was successful
     */
    public static boolean tryBindSoul(SoulBindable soulBindable, Soul soul) {
        if (!soulBindable.canBeBound() || !soulBindable.isSoulValid(soul)) {
            return false;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            if (soulBindable.bindSoul(soul, transaction)) {
                transaction.commit();
                return true;
            }

            return false;
        }
    }

    /**
     * Attempts to bind a soul to the given {@code itemStack}. If the {@code itemStack} cannot
     * have a soul bound to it then it is returned unchanged.
     * If the binding fails, the method returns an empty {@link Optional}.
     * If the soul is successfully bound, the method returns the original {@code itemStack}.
     *
     * @param itemStack The item stack that should have the soul bound to it.
     * @param soul      The soul to be bound to the {@code itemStack}.
     * @return An {@link Optional} containing the original {@code itemStack} if binding was successful or is not possible,
     *         or an empty {@link Optional} if binding failed.
     */
    @Deprecated
    public static Optional<ItemStack> getBoundIfCapable(ItemStack itemStack, Soul soul) {
        // Copy so we don't bind the original input.
        ItemStack result = itemStack.copy();

        // If there's no binding possible, allow it through
        var soulBindable = ItemAccess.forStack(result).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable == null) {
            return Optional.of(result);
        }

        if (tryBindSoul(soulBindable, soul)) {
            return Optional.of(result);
        }

        return Optional.empty();
    }
}
