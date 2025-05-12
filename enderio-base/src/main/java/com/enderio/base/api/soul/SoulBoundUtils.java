package com.enderio.base.api.soul;

import com.enderio.base.api.soul.binding.ISoulBindable;
import com.enderio.base.common.init.EIOCapabilities;
import jdk.jfr.Experimental;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Some helper methods for interacting with {@link ISoulBindable} capabilities.
 */
@ApiStatus.AvailableSince("8.0")
@Experimental
public class SoulBoundUtils {
    /**
     * Test whether the {@code itemStack} supports soul binding.
     * @implNote This tests both whether the {@code itemStack} supports the capability and that {@link ISoulBindable#canBind()} is true.
     * @param itemStack the item to test.
     * @return whether the item can be bound.
     */
    public static boolean canBindSoul(ItemStack itemStack) {
        var soulBindable = itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable == null) {
            return false;
        }

        return soulBindable.canBind();
    }

    /**
     * Test whether the {@code soul} can be bound to the {@code itemStack}.
     * @param itemStack the item stack to bind to.
     * @param soul the soul to test.
     * @return whether it can be bound.
     */
    public static boolean canBindSoul(ItemStack itemStack, Soul soul) {
        var soulBindable = itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable == null) {
            return false;
        }

        return soulBindable.canBind() && soulBindable.isSoulValid(soul);
    }

    /**
     * Get the soul bound to the {@code itemStack} or {@link Soul#EMPTY}.
     * @param itemStack the stack to get a soul for.
     * @return the bound soul, or {@link Soul#EMPTY} if there is no soul bound.
     */
    public static Soul getBoundSoul(ItemStack itemStack) {
        var soulBindable = itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable == null) {
            return Soul.EMPTY;
        }

        return soulBindable.getBoundSoul();
    }

    /**
     * Whether the {@code itemStack} has a soul bound to it.
     * @param itemStack the stack to check.
     * @return whether there is a bound soul.
     */
    public static boolean isBound(ItemStack itemStack) {
        var soulBindable = itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable == null) {
            return false;
        }

        return soulBindable.hasSoul();
    }

    /**
     * Attempt to bind a {@code soul} to the {@code itemStack}.
     * @param itemStack the stack to bind to.
     * @param soul the soul to be bound.
     * @return whether the binding was successful
     */
    public static boolean tryBindSoul(ItemStack itemStack, Soul soul) {
        var soulBindable = itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable == null) {
            return false;
        }

        // Short-circuit if we're already bound.
        if (Soul.isSameEntitySameTag(soulBindable.getBoundSoul(), soul)) {
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
    public static boolean tryBindSoul(ISoulBindable soulBindable, Soul soul) {
        if (!soulBindable.canBind() || !soulBindable.isSoulValid(soul)) {
            return false;
        }

        soulBindable.bindSoul(soul);
        return true;
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
    public static Optional<ItemStack> getBoundIfCapable(ItemStack itemStack, Soul soul) {
        var soulBindable = itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM);

        // If there's no binding possible, allow it through
        if (soulBindable == null) {
            return Optional.of(itemStack);
        }

        if (tryBindSoul(soulBindable, soul)) {
            return Optional.of(itemStack);
        }

        return Optional.empty();
    }
}
