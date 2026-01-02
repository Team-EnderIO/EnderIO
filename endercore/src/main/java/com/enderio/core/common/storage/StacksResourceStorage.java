package com.enderio.core.common.storage;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;

// Mostly the same as StacksResourceHandler, but we save differently and support variable fluid capacity.
// TODO: Might be better just not using the base class?
public abstract class StacksResourceStorage<T extends Resource, S, C> extends StacksResourceHandler<S, T>
    implements ResourceStorage<T>, ValueIOSerializable {

    private final ResourceStorageLayout<T, C> layout;
    protected final C context;
    private final Codec<S> stackCodec;

    public StacksResourceStorage(ResourceStorageLayout<T, C> layout, C context, S defaultStack, Codec<S> stackCodec) {
        super(layout.size(), defaultStack, stackCodec);
        this.layout = layout;
        this.context = context;
        this.stackCodec = stackCodec;
    }

    // Helpers for dealing with stacks
    public S getStack(ResourceSlotId<T> slotId) {
        int index = slotId.index(layout);
        return getStackFrom(getResource(index), getAmountAsInt(index));
    }

    public void setStack(ResourceSlotId<T> slotId, S stack) {
        set(slotId.index(layout), getResourceFrom(stack), getAmountFrom(stack));
    }

    public ResourceStorageLayout<T, C> layout() {
        return layout;
    }

    @Override
    protected int getCapacity(int index, T resource) {
        Objects.checkIndex(index, size());
        return layout.slotConfig(index).getCapacityAsInt(resource, context);
    }

    @Override
    public boolean isValid(int index, T resource) {
        Objects.checkIndex(index, size());
        return layout.slotConfig(index).isValid(index, resource, context);
    }

    @Override
    public int internalInsert(int index, T resource, int amount, TransactionContext transaction) {
        return super.insert(index, resource, amount, transaction);
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonNegative(amount);

        if (!layout.slotConfig(index).canInsert()) {
            return 0;
        }

        return super.insert(index, resource, amount, transaction);
    }

    @Override
    public int internalExtract(int index, T resource, int amount, TransactionContext transaction) {
        return super.extract(index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonNegative(amount);

        if (!layout.slotConfig(index).canExtract()) {
            return 0;
        }

        return super.extract(index, resource, amount, transaction);
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
        // Flush to empty stacks then re-fill with saved contents
        stacks.clear();

        var slotList = input.childrenListOrEmpty("Contents");
        for (var slotAndStack : slotList) {
            int index = slotAndStack.getIntOr("Index", -1);
            if (index >= 0 && index < size()) {
                slotAndStack.read("Stack", stackCodec)
                    .ifPresent(stack -> stacks.set(index, stack));
            }
        }
    }
}
