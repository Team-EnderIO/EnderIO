package com.enderio.enderio.content.conduits;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public class ConduitApiImpl implements ConduitApi {

    @Override
    public ItemStackTemplate getConduitStackTemplate(Holder<Conduit<?, ?>> conduit, int count) {
        return ConduitBlockItem.getStackTemplateFor(conduit, count);
    }

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

    @Override
    public <T extends Conduit<T, ?>> int compareConduits(Conduit<T, ?> a, Conduit<?, ?> b) {
        if (a.type() != b.type()) {
            throw new IllegalArgumentException("Conduits are not of the same type.");
        }

        //noinspection unchecked
        return a.compareTo((T) b);
    }
}
