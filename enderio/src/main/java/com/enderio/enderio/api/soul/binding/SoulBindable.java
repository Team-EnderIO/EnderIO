package com.enderio.enderio.api.soul.binding;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.storage.SoulHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * Intended for things that can be bound to a soul.
 * This means the soul becomes irretrievable - the soul can only be overwritten.
 * @see SoulHandler for soul storage handling.
 */
@ApiStatus.AvailableSince("8.0")
public interface SoulBindable {

    /**
     * @return the currently bound soul, or {@link Soul#EMPTY}.
     */
    Soul boundSoul();

    /**
     * @return whether this item has a bound soul.
     */
    @ApiStatus.NonExtendable
    default boolean isBound() {
        return boundSoul().hasEntity();
    }

    /**
     * @implNote This also controls whether Ender IO adds a tooltip to show the soul binding status.
     * @return whether this item can be rebound.
     */
    boolean canBeBound();

    /**
     * Whether this soul is valid for this item.
     * @param soul soul to check.
     * @return whether the soul is valid.
     */
    boolean isSoulValid(Soul soul);

    /**
     * Bind (or rebind) the soul.
     * @param newSoul the soul to bind.
     * @param transaction transaction context.
     * @return whether the new soul was bound. If {@link #canBeBound()} or {@link #isSoulValid(Soul)} return false, this will always false.
     */
    boolean bindSoul(Soul newSoul, TransactionContext transaction);

    @ApiStatus.Experimental
    default boolean shouldDisplayTooltip() {
        return true;
    }
}
