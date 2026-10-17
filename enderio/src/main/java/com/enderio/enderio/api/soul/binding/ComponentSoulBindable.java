package com.enderio.enderio.api.soul.binding;

import com.enderio.enderio.api.soul.Soul;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ComponentSoulBindable implements SoulBindable {
    protected final ItemAccess itemAccess;
    protected final DataComponentType<Soul> componentType;

    public ComponentSoulBindable(ItemAccess itemAccess, DataComponentType<Soul> componentType) {
        this.itemAccess = itemAccess;
        this.componentType = componentType;
    }

    @Override
    public Soul boundSoul() {
        return itemAccess.getResource().getOrDefault(componentType, Soul.EMPTY);
    }

    @Override
    public boolean canBeBound() {
        return true;
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        return true;
    }

    @Override
    public boolean bindSoul(Soul newSoul, TransactionContext transaction) {
        if (!canBeBound() || !isSoulValid(newSoul)) {
            return false;
        }

        ItemResource newlyBoundResource = itemAccess.getResource().with(componentType, newSoul);

        // Ensure we were able to exchange the current item for the new one.
        return itemAccess.exchange(newlyBoundResource, 1, transaction) == 1;
    }
}
