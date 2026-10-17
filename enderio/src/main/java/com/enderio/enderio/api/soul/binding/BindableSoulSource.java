package com.enderio.enderio.api.soul.binding;

import com.enderio.enderio.api.soul.Soul;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a source of a soul used for binding.
 * This can be implemented by an item for participation in soul binding.
 * For example, the soul vial uses this to supply its soul for binding.
 * If you implement an item that stores multiple souls, give the player a way to select a 'slot' and expose it with this.
 */
@ApiStatus.Experimental
@ApiStatus.AvailableSince("10.0.0")
public interface BindableSoulSource {
    boolean isSoulAvailable();

    Soul availableSoul();

    /**
     * Extract the soul from this source.
     * @param transaction
     * @return The soul, or {@link Soul#EMPTY} if none is available.
     * @implNote This should always return a copy of the Soul, so that it can be mutated or stored.
     */
    Soul extractSoul(TransactionContext transaction);
}
