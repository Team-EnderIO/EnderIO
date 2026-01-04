package com.enderio.enderio.content.conduits;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;

public class ConduitApiImpl implements ConduitApi {

    @Override
    public ItemStack getConduitItem(Holder<Conduit<?, ?>> conduit, int count) {
        return ConduitBlockItem.getStackFor(conduit, count);
    }

    @Override
    public int getConduitSortIndex(Holder<Conduit<?, ?>> conduit) {
        return ConduitSorter.getSortIndex(conduit);
    }

    @Override
    public String makeDescriptionId(ResourceKey<Conduit<?, ?>> key) {
        return Util.makeDescriptionId(EnderIORegistries.Keys.CONDUIT.identifier().getPath(), key.identifier());
    }
}
