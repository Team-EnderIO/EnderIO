package com.enderio.enderio.api.soul.storage;

import com.enderio.enderio.api.soul.SoulResource;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

/**
 * A simple Soul {@link net.neoforged.neoforge.transfer.ResourceHandler} which stores a single soul.
 */
public class SimpleItemAccessSoulHandler extends ItemAccessResourceHandler<SoulResource> {
    protected final Item validItem;
    protected final DataComponentType<SimpleSoulContent> component;

    public SimpleItemAccessSoulHandler(ItemAccess itemAccess, DataComponentType<SimpleSoulContent> component) {
        super(itemAccess, 1);
        this.validItem = itemAccess.getResource().getItem();
        this.component = component;
    }

    @Override
    protected SoulResource getResourceFrom(ItemResource accessResource, int index) {
        if (accessResource.is(validItem)) {
            return SoulResource.of(accessResource.getOrDefault(component, SimpleSoulContent.EMPTY).copy());
        } else {
            return SoulResource.EMPTY;
        }
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        if (accessResource.is(validItem)) {
            return accessResource.getOrDefault(component, SimpleSoulContent.EMPTY).isEmpty() ? 0 : 1;
        } else {
            return 0;
        }
    }

    @Override
    protected @Nullable ItemResource update(ItemResource accessResource, int index, SoulResource newResource, int newAmount) {
        return accessResource.with(component, newAmount == 0 ? SimpleSoulContent.EMPTY : SimpleSoulContent.copyOf(newResource.toSoul()));
    }

    @Override
    public boolean isValid(int index, SoulResource resource) {
        return itemAccess.getResource().is(validItem);
    }

    @Override
    protected int getCapacity(int index, SoulResource resource) {
        return 1;
    }
}
