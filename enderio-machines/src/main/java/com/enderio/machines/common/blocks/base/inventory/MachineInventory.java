package com.enderio.machines.common.blocks.base.inventory;

import com.enderio.base.api.io.IOConfigurable;
import com.enderio.machines.common.blocks.base.state.MachineState;
import java.util.function.IntConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * A machine inventory.
 * Configured and controlled by a machine's {@link IOConfigurable} and a {@link MachineInventoryLayout}.
 */
public class MachineInventory extends ItemStackHandler {
    private final IOConfigurable ioConfigurable;
    private final MachineInventoryLayout layout;
    private IntConsumer changeListener = i -> {
    };

    /**
     * Create a new machine inventory.
     */
    public MachineInventory(IOConfigurable ioConfigurable, MachineInventoryLayout layout) {
        super(layout.getSlotCount());
        this.ioConfigurable = ioConfigurable;
        this.layout = layout;
    }

    // TODO: Why are we not calling changeListener in onContentsChanged?
    public void addSlotChangedCallback(IntConsumer callback) {
        changeListener = changeListener.andThen(callback);
    }

    /**
     * Get the inventory layout.
     */
    public final MachineInventoryLayout getLayout() {
        return layout;
    }

    public final MachineInventoryLayout layout() {
        return layout;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return layout.isItemValid(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return layout.getStackLimit(slot);
    }

    @Nullable
    public IItemHandler getForSide(@Nullable Direction side) {
        if (side == null) {
            return new Wrapped(this, null);
        }

        if (ioConfigurable.getIOMode(side).canConnect()) {
            return new Wrapped(this, side);
        }

        return null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        boolean wasEmpty = !simulate && getStackInSlot(slot).isEmpty();
        ItemStack itemStack = super.insertItem(slot, stack, simulate);
        if (wasEmpty && itemStack.getCount() != stack.getCount()) {
            changeListener.accept(slot);
        }

        return itemStack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack itemStack = super.extractItem(slot, amount, simulate);
        if (!itemStack.isEmpty() && !simulate && getStackInSlot(slot).isEmpty()) {
            changeListener.accept(slot);
        }

        return itemStack;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        boolean changed = stack.getItem() != getStackInSlot(slot).getItem();
        super.setStackInSlot(slot, stack);
        if (changed) {
            this.changeListener.accept(slot);
        }
    }

    public void copyFromItem(ItemContainerContents contents) {
        contents.copyInto(this.stacks);
        for (int i = 0; i < getSlots(); i++) {
            onContentsChanged(i);
            this.changeListener.accept(i);
        }
    }

    public ItemContainerContents toItemContents() {
        return ItemContainerContents.fromItems(this.stacks);
    }

    // TODO: not a fan of this pattern.
    public void updateMachineState(MachineState state, boolean add) {

    }

    private record Wrapped(MachineInventory master, @Nullable Direction side) implements IItemHandler {

        @Override
        public int getSlots() {
            return master.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return master.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            // Check we allow insertion on the slot
            if (!master.getLayout().canInsert(slot)) {
                return stack;
            }

            // Check we allow input to the block on this side
            if (side != null && !master.ioConfigurable.getIOMode(side).canInput()) {
                return stack;
            }

            return master.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // Check we allow extraction on the slot
            if (!master.getLayout().canExtract(slot)) {
                return ItemStack.EMPTY;
            }

            // Check we allow output from the block on this side
            if (side != null && !master.ioConfigurable.getIOMode(side).canOutput()) {
                return ItemStack.EMPTY;
            }

            return master.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return master.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return master.isItemValid(slot, stack);
        }
    }
}
