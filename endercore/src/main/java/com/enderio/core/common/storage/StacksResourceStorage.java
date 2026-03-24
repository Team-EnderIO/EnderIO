package com.enderio.core.common.storage;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

// Mostly the same as StacksResourceHandler, but we save differently and support variable fluid capacity.
public abstract class StacksResourceStorage<T extends Resource, S> implements ResourceStorage<T>, ValueIOSerializable {

    private final ResourceStorageLayout<T> layout;
    private final S emptyStack;
    private final Codec<S> stackCodec;
    private final ArrayList<StackJournal> snapshotJournals;

    protected NonNullList<S> stacks;

    protected StacksResourceStorage(ResourceStorageLayout<T> layout, S emptyStack, Codec<S> stackCodec) {
        this.layout = layout;
        this.emptyStack = emptyStack;
        this.stackCodec = stackCodec;
        this.stacks = NonNullList.withSize(layout.size(), emptyStack);
        this.snapshotJournals = new ArrayList<>(this.stacks.size());
        this.updateStacksSize();
    }

    private void updateStacksSize() {
        snapshotJournals.ensureCapacity(stacks.size());

        // Add missing entries
        while (snapshotJournals.size() < stacks.size()) {
            snapshotJournals.add(new StackJournal(snapshotJournals.size()));
        }

        // Remove superfluous entries
        if (snapshotJournals.size() > stacks.size()) {
            snapshotJournals.subList(stacks.size(), snapshotJournals.size()).clear();
        }
    }

    protected abstract T getResourceFrom(S stack);

    protected abstract int getAmountFrom(S stack);

    protected abstract S getStackFrom(T resource, int amount);

    protected abstract S copyOf(S stack);

    protected boolean matches(S stack, T resource) {
        return this.getResourceFrom(stack).equals(resource);
    }

    protected void onContentsChanged(int index, S previousContents) {
    }

    @Override
    public boolean isValid(int index, T resource) {
        Objects.checkIndex(index, size());
        return layout.slotConfig(index).isValid(index, resource);
    }

    protected int getCapacity(int index, T resource) {
        Objects.checkIndex(index, size());
        return layout.slotConfig(index).getCapacityAsInt(resource);
    }

    // Helpers for dealing with stacks
    public S getStack(ResourceSlotId<T> slotId) {
        int index = slotId.index(layout);
        return getStackFrom(getResource(index), getAmountAsInt(index));
    }

    public void setStack(ResourceSlotId<T> slotId, S stack) {
        set(slotId.index(layout), getResourceFrom(stack), getAmountFrom(stack));
    }

    @Unmodifiable
    public List<S> getStacks(MultiResourceSlotKey<T> key) {
        return getStacks(key.slots());
    }

    @Unmodifiable
    public List<S> getStacks(Collection<ResourceSlotId<T>> slots) {
        return slots.stream().map(this::getStack).toList();
    }

    @Override
    public ResourceStorageLayout<T> layout() {
        return layout;
    }

    @Override
    public void set(int index, T resource, int amount) {
        TransferPreconditions.checkNonNegative(amount);
        if (resource.isEmpty() && amount > 0) {
            throw new IllegalArgumentException("Resource is empty but the amount is positive: " + amount);
        }

        S oldContents = stacks.set(index, getStackFrom(resource, amount));
        onContentsChanged(index, oldContents);
    }

    @Override
    public void setTransactional(int index, T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        if (resource.isEmpty() && amount > 0) {
            throw new IllegalArgumentException("Resource is empty but the amount is positive: " + amount);
        } else {
            this.snapshotJournals.get(index).updateSnapshots(transaction);
            this.stacks.set(index, this.getStackFrom(resource, amount));
        }
    }

    @Override
    public int internalInsert(int index, T resource, int amount, TransactionContext transaction) {
        return insertImpl(index, resource, amount, transaction);
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonNegative(amount);

        if (!layout.slotConfig(index).canInsert()) {
            return 0;
        }

        return insertImpl(index, resource, amount, transaction);
    }

    @Override
    public int internalExtract(int index, T resource, int amount, TransactionContext transaction) {
        return extractImpl(index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonNegative(amount);

        if (!layout.slotConfig(index).canExtract()) {
            return 0;
        }

        return extractImpl(index, resource, amount, transaction);
    }

    @Override
    public int size() {
        return stacks.size();
    }

    @Override
    public T getResource(int index) {
        Objects.checkIndex(index, size());
        return getResourceFrom(stacks.get(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return getAmountFrom(stacks.get(index));
    }

    @Override
    public long getCapacityAsLong(int index, T resource) {
        Objects.checkIndex(index, size());
        return resource.isEmpty() || isValid(index, resource) ? getCapacity(index, resource) : 0;
    }

    private int insertImpl(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        S currentStack = stacks.get(index);
        int currentAmount = getAmountFrom(currentStack);

        if ((currentAmount == 0 || matches(currentStack, resource)) && isValid(index, resource)) {
            int inserted = Math.min(amount, getCapacity(index, resource) - currentAmount);

            if (inserted > 0) {
                snapshotJournals.get(index).updateSnapshots(transaction);
                stacks.set(index, getStackFrom(resource, currentAmount + inserted));
                return inserted;
            }
        }

        return 0;
    }

    private int extractImpl(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        S currentStack = stacks.get(index);

        if (matches(currentStack, resource)) {
            int currentAmount = getAmountFrom(currentStack);
            int extracted = Math.min(amount, currentAmount);

            if (extracted > 0) {
                snapshotJournals.get(index).updateSnapshots(transaction);
                stacks.set(index, getStackFrom(resource, currentAmount - extracted));
                return extracted;
            }
        }

        return 0;
    }

    @Override
    public void serialize(ValueOutput output) {
        // We serialize slot + item together to ensure order and avoid resizing the inventory.
        var slotList = output.childrenList("Contents");
        for (int i = 0; i < size(); i++) {
            var slotAndStack = slotList.addChild();
            slotAndStack.putInt("Index", i);
            slotAndStack.store("Stack", stackCodec, stacks.get(i));
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        var slotList = input.childrenListOrEmpty("Contents");
        for (var slotAndStack : slotList) {
            int index = slotAndStack.getIntOr("Index", -1);
            if (index >= 0 && index < size()) {
                slotAndStack.read("Stack", stackCodec)
                    .ifPresentOrElse(stack -> stacks.set(index, stack),
                        () -> stacks.set(index, emptyStack));
            }
        }
    }

    private class StackJournal extends SnapshotJournal<S> {
        private final int index;

        private StackJournal(int index) {
            this.index = index;
        }

        protected S createSnapshot() {
            return (S)StacksResourceStorage.this.copyOf(StacksResourceStorage.this.stacks.get(this.index));
        }

        protected void revertToSnapshot(S snapshot) {
            StacksResourceStorage.this.stacks.set(this.index, snapshot);
        }

        protected void onRootCommit(S originalState) {
            StacksResourceStorage.this.onContentsChanged(this.index, originalState);
        }
    }
}
