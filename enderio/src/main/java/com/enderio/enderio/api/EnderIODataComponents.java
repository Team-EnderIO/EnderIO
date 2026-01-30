package com.enderio.enderio.api;

import com.enderio.enderio.api.conduits.Conduit;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.UnaryOperator;

/**
 * Data components available for reuse by addons.
 */
@ApiStatus.AvailableSince("8.0.5")
public class EnderIODataComponents {

    /**
     * Intended for the enderio:conduit item - specifies which conduit to place.
     */
    public static final DataComponentType<Holder<Conduit<?, ?>>> CONDUIT = create(b ->
        b.persistent(Conduit.CODEC).networkSynchronized(Conduit.STREAM_CODEC));

    private static <T> DataComponentType<T> create(UnaryOperator<DataComponentType.Builder<T>> config) {
        return config.apply(DataComponentType.builder()).build();
    }
}
