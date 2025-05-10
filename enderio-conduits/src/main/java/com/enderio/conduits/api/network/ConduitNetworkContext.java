package com.enderio.conduits.api.network;

import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.mojang.serialization.Codec;
import java.util.Set;

/**
 * Additional information that is stored on a conduit network.
 * This is not synced to the client, but can be saved if desired.
 * @param <T>
 */
public interface ConduitNetworkContext<T extends ConduitNetworkContext<T>> {
    Codec<ConduitNetworkContext<?>> GENERIC_CODEC = EnderIOConduitsRegistries.CONDUIT_NETWORK_CONTEXT_TYPE.byNameCodec()
            .dispatch(ConduitNetworkContext::type, ConduitNetworkContextType::codecOrThrow);

    /**
     * Merges this context with another.
     * @param other the other context to merge with.
     * @return a new instance of the context, or this if merging is not required.
     * @apiNote The result of this method does not need to be a new instance, unlike {@link #split(IConduitNetwork, Set)}.
     */
    T mergeWith(T other);

    /**
     * Splits this context when the networks split.
     * @param selfNetwork The network to create a "split" context for.
     * @param allNetworks All the networks involved in the split.
     * @return a new instance of the context.
     * @apiNote The result of this method must be a new instance, an exception will be thrown if multiple networks share a context.
     */
    T split(IConduitNetwork selfNetwork, Set<? extends IConduitNetwork> allNetworks);

    /**
     * @return the type of this context. Used for type comparisons and serialization.
     */
    ConduitNetworkContextType<T> type();
}
