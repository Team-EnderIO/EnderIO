package com.enderio.enderio.foundation.storage;

import com.enderio.core.CoreNBTKeys;
import com.enderio.core.common.fluid.FluidStackWithTank;
import com.enderio.core.common.storage.MultiResourceSlotKey;
import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.ResourceStorageLayout;
import com.enderio.core.common.storage.SingleResourceSlotKey;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.Objects;

public class FluidStorage<TOwner> implements ResourceStorage<FluidResource>, ValueIOSerializable {
    private final ResourceStorageLayout<FluidResource, TOwner> layout;
    private final TOwner owner;
    private final NonNullList<FluidStack> stacks;
    private final ArrayList<StackJournal> snapshotJournals;

    public FluidStorage(ResourceStorageLayout<FluidResource, TOwner> layout, TOwner owner) {
        this.layout = layout;
        this.owner = owner;
        this.stacks = NonNullList.withSize(layout.size(), FluidStack.EMPTY);
        this.snapshotJournals = new ArrayList<>(this.stacks.size());
        updateStacksSize();
    }

    // Helpers for dealing with stacks
    public FluidStack getStack(SingleResourceSlotKey<FluidResource> key) {
        int index = layout.indexOf(key);
        return getResource(index).toStack(getAmountAsInt(index));
    }

    public void setStack(SingleResourceSlotKey<FluidResource> key, FluidStack stack) {
        set(layout.indexOf(key), FluidResource.of(stack), stack.getAmount());
    }

    public FluidStack getStack(MultiResourceSlotKey<FluidResource> key, int index) {
        int absoluteIndex = layout.indexOf(key, index);
        return getResource(absoluteIndex).toStack(getAmountAsInt(absoluteIndex));
    }

    public void setStack(MultiResourceSlotKey<FluidResource> key, int index, FluidStack stack) {
        set(layout.indexOf(key, index), FluidResource.of(stack), stack.getAmount());
    }

    private void updateStacksSize() {
        this.snapshotJournals.ensureCapacity(this.stacks.size());

        while(this.snapshotJournals.size() < this.stacks.size()) {
            this.snapshotJournals.add(new StackJournal(this.snapshotJournals.size()));
        }

        if (this.snapshotJournals.size() > this.stacks.size()) {
            this.snapshotJournals.subList(this.stacks.size(), this.snapshotJournals.size()).clear();
        }
    }

    protected void onContentsChanged(int index, FluidStack previousContents) {
    }

    public ResourceStorageLayout<FluidResource, TOwner> layout() {
        return layout;
    }

    @Override
    public void set(int index, FluidResource resource, int amount) {
        Objects.checkIndex(index, size());
        FluidStack oldContents = stacks.set(index, resource.toStack(amount));
        onContentsChanged(index, oldContents);
    }

    @Override
    public int size() {
        return layout.size();
    }

    @Override
    public FluidResource getResource(int index) {
        Objects.checkIndex(index, size());
        return FluidResource.of(stacks.get(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return stacks.get(index).getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return layout.get(index).getCapacityAsInt(resource, owner);
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return layout.get(index).isValid(index, resource, owner);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonNegative(amount);

        if (!layout.get(index).canInsert()) {
            return 0;
        }

        FluidStack currentStack = this.stacks.get(index);
        int currentAmount = currentStack.getAmount();
        if ((currentAmount == 0 || resource.matches(currentStack)) && this.isValid(index, resource)) {
            int inserted = Math.min(amount, this.getCapacityAsInt(index, resource) - currentAmount);
            if (inserted > 0) {
                this.snapshotJournals.get(index).updateSnapshots(transaction);
                this.stacks.set(index, resource.toStack(currentAmount + inserted));
                return inserted;
            }
        }

        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonNegative(amount);

        if (!layout.get(index).canExtract()) {
            return 0;
        }

        FluidStack currentStack = this.stacks.get(index);
        if (resource.matches(currentStack)) {
            int currentAmount = currentStack.getAmount();
            int extracted = Math.min(amount, currentAmount);
            if (extracted > 0) {
                this.snapshotJournals.get(index).updateSnapshots(transaction);
                this.stacks.set(index, resource.toStack(currentAmount - extracted));
                return extracted;
            }
        }

        return 0;
    }

    @Override
    public void serialize(ValueOutput output) {
        var tankList = output.list(CoreNBTKeys.TANKS, FluidStackWithTank.CODEC);
        for (int i = 0; i < size(); i++) {
            tankList.add(new FluidStackWithTank(i, stacks.get(i)));
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        var tankList = input.listOrEmpty(CoreNBTKeys.TANKS, FluidStackWithTank.CODEC);
        for (var stackWithTank : tankList) {
            if (stackWithTank.isValidInHandler(size())) {
                stacks.set(stackWithTank.tank(), stackWithTank.stack());
            }
        }
    }

    private class StackJournal extends SnapshotJournal<FluidStack> {
        private final int index;

        private StackJournal(int index) {
            this.index = index;
        }

        protected FluidStack createSnapshot() {
            return FluidStorage.this.stacks.get(this.index).copy();
        }

        protected void revertToSnapshot(FluidStack snapshot) {
            FluidStorage.this.stacks.set(this.index, snapshot);
        }

        protected void onRootCommit(FluidStack originalState) {
            FluidStorage.this.onContentsChanged(this.index, originalState);
        }
    }
}
