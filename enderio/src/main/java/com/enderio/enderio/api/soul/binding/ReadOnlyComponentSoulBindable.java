package com.enderio.enderio.api.soul.binding;

import com.enderio.enderio.api.soul.Soul;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ReadOnlyComponentSoulBindable implements SoulBindable {

    protected final MutableDataComponentHolder parent;
    protected final DataComponentType<Soul> componentType;

    public ReadOnlyComponentSoulBindable(MutableDataComponentHolder parent, DataComponentType<Soul> componentType) {
        this.parent = parent;
        this.componentType = componentType;
    }

    @Override
    public Soul boundSoul() {
        return parent.getOrDefault(componentType, Soul.EMPTY);
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
