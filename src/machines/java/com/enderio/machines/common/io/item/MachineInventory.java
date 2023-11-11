package com.enderio.machines.common.io.item;

import com.enderio.api.capability.IEnderCapabilityProvider;
import com.enderio.api.io.IIOConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.function.IntConsumer;

/**
 * A machine inventory.
 * Configured and controlled by a machine's {@link IIOConfig} and a {@link MachineInventoryLayout}.
 */
public class MachineInventory extends ItemStackHandler implements IEnderCapabilityProvider<IItemHandler> {
    private final IIOConfig config;
    private final MachineInventoryLayout layout;

    private final EnumMap<Direction, LazyOptional<Wrapped>> sideCache = new EnumMap<>(Direction.class);
    private LazyOptional<Wrapped> selfCache = LazyOptional.empty();

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

    @Override
    public Capability<IItemHandler> getCapabilityType() {
        return ForgeCapabilities.ITEM_HANDLER;
    }

    @Override
    public LazyOptional<IItemHandler> getCapability(@Nullable Direction side) {
        if (side == null) {
            // Create own cache if its been invalidated or not created yet.
            if (!selfCache.isPresent()) {
                selfCache = LazyOptional.of(() -> new Wrapped(this, null));
            }

            return selfCache.cast();
        }

        if (!config.getMode(side).canConnect()) {
            return LazyOptional.empty();
        }

        return sideCache.computeIfAbsent(side, dir -> LazyOptional.of(() -> new Wrapped(this, dir))).cast();
    }

    @Override
    public void invalidateSide(@Nullable Direction side) {
        if (side != null) {
            if (sideCache.containsKey(side)) {
                sideCache.get(side).invalidate();
                sideCache.remove(side);
            }
        } else {
            selfCache.invalidate();
        }
    }

    @Override
    public void invalidateCaps() {
        for (LazyOptional<Wrapped> access : sideCache.values()) {
            access.invalidate();
        }
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
