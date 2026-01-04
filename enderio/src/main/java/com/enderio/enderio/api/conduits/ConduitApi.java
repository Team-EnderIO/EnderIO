package com.enderio.enderio.api.conduits;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

public interface ConduitApi {

    ConduitApi INSTANCE = ServiceLoader.load(ConduitApi.class).findFirst().orElseThrow();

    ItemStack getConduitItem(Holder<Conduit<?, ?>> conduit, int count);

    default ItemStack getConduitItem(Holder<Conduit<?, ?>> conduit) {
        return getConduitItem(conduit, 1);
    }

    int getConduitSortIndex(Holder<Conduit<?, ?>> conduit);

    /**
     * Create the default description ID for a conduit.
     * @param key the conduit key
     * @return the description ID
     */
    @ApiStatus.AvailableSince("8.1.0")
    String makeDescriptionId(ResourceKey<Conduit<?, ?>> key);
}
