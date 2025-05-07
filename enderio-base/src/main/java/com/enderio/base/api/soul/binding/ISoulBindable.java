package com.enderio.base.api.soul.binding;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.storage.ISoulHandler;
import org.jetbrains.annotations.ApiStatus;

/**
 * Intended for items that can be soul-bound, like machines or single-soul things like vials.
 * @see ISoulHandler for soul storage handling.
 */
@ApiStatus.Experimental
@ApiStatus.AvailableSince("8.0")
public interface ISoulBindable {

    /**
     * @return the currently bound soul, or {@link Soul#EMPTY}.
     */
    Soul getBoundSoul();

    /**
     * @return whether this item has a bound soul.
     */
    default boolean hasSoul() {
        return getBoundSoul().hasEntity();
    }

    /**
     * @implNote This also controls whether Ender IO adds a tooltip to show the soul binding status.
     * @return whether this item can be rebound.
     */
    boolean canBind();

    /**
     * Whether this soul is valid for this item.
     * @param soul soul to check.
     * @return whether the soul is valid.
     */
    boolean isSoulValid(Soul soul);

    /**
     * Bind (or rebind) the soul on this item.
     * @param newSoul the soul to bind.
     * @throws IllegalArgumentException if the soul is not valid for this item.
     * @throws UnsupportedOperationException if the item cannot be rebound.
     */
    void bindSoul(Soul newSoul);
}
