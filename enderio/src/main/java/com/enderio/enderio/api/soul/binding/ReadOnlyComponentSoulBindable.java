package com.enderio.enderio.api.soul.binding;

import com.enderio.enderio.api.soul.Soul;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ReadOnlyComponentSoulBindable implements SoulBindable {

    protected final ItemAccess itemAccess;
    protected final DataComponentType<Soul> componentType;

    public ReadOnlyComponentSoulBindable(ItemAccess itemAccess, DataComponentType<Soul> componentType) {
        this.itemAccess = itemAccess;
        this.componentType = componentType;
    }

    @Override
    public Soul boundSoul() {
        return itemAccess.getResource().getOrDefault(componentType, Soul.EMPTY);
    }

    @Override
    public boolean canBeBound() {
        return false;
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        return false;
    }

    @Override
    public boolean bindSoul(Soul newSoul, TransactionContext transaction) {
        return false;
    }
}
