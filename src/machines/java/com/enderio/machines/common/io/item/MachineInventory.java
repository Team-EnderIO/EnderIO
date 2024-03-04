package com.enderio.machines.common.io.item;

import com.enderio.api.io.IIOConfig;
import com.enderio.machines.common.blockentity.MachineState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;

/**
 * A machine inventory.
 * Configured and controlled by a machine's {@link IIOConfig} and a {@link MachineInventoryLayout}.
 */
public class MachineInventory extends ItemStackHandler {
    private final IIOConfig config;
    private final MachineInventoryLayout layout;
    private IntConsumer changeListener = i -> {};

    /**
     * Create a new machine inventory.
     */
    public MachineInventory(IIOConfig config, MachineInventoryLayout layout) {
        super(layout.getSlotCount());
        this.config = config;
        this.layout = layout;
    }
    public void addSlotChangedCallback(IntConsumer callback) {
        changeListener = changeListener.andThen(callback);
    }

    /**
     * Get the IO config for the machine.
     */
    public final IIOConfig getConfig() {
        return config;
    }

    /**
     * Get the inventory layout.
     */
    public final MachineInventoryLayout getLayout() {
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

        if (config.getMode(side).canConnect()) {
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
            if (side != null && !master.getConfig().getMode(side).canInput()) {
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
            if (side != null && !master.getConfig().getMode(side).canOutput()) {
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
