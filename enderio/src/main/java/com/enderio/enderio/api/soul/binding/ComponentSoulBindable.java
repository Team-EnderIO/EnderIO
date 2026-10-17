package com.enderio.enderio.api.soul.binding;

import com.enderio.enderio.api.soul.Soul;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ComponentSoulBindable implements SoulBindable {
    protected final MutableDataComponentHolder parent;
    protected final DataComponentType<Soul> componentType;

    private final SoulJournal journal = new SoulJournal();

    public ComponentSoulBindable(MutableDataComponentHolder parent, DataComponentType<Soul> componentType) {
        this.parent = parent;
        this.componentType = componentType;
    }

    @Override
    public Soul boundSoul() {
        return parent.getOrDefault(componentType, Soul.EMPTY);
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

        journal.updateSnapshots(transaction);
        parent.set(componentType, newSoul);
        return true;
    }

    private class SoulJournal extends SnapshotJournal<Soul> {
        @Override
        protected Soul createSnapshot() {
            return parent.getOrDefault(componentType, Soul.EMPTY);
        }

        @Override
        protected void revertToSnapshot(Soul snapshot) {
            parent.set(componentType, snapshot);
        }
    }
}
