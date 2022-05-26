package com.enderio.machines.common.io.item;

import com.enderio.api.capability.ICapabilityProvider;
import com.enderio.api.io.IIOConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.EnumMap;

public class MachineInventory extends ItemStackHandler implements ICapabilityProvider<IItemHandler> {

    private final IIOConfig config;

    private final MachineInventoryLayout layout;

    private boolean isForceMode = false;

    private final EnumMap<Direction, LazyOptional<Sided>> sideCache = new EnumMap<>(Direction.class);

    public MachineInventory(IIOConfig config, MachineInventoryLayout layout) {
        super(layout.getSlotCount());
        this.config = config;
        this.layout = layout;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!isForceMode) {
            if (layout.isOutput(slot) || !layout.validateStack(slot, stack))
                return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    public ItemStack forceInsertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        isForceMode = true;
        ItemStack returnValue = insertItem(slot, stack, simulate);
        isForceMode = false;
        return returnValue;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return isForceMode || (layout.validateStack(slot, stack) && !layout.isOutput(slot));
    }

    @Override
    public int getSlotLimit(int slot) {
        if (layout.getCapacitorSlot() == slot)
            return 1;
        return super.getSlotLimit(slot);
    }

    public ItemStack guiExtractItem(int slot, int amount, boolean simulate) {
        isForceMode = true;
        ItemStack returnValue = super.extractItem(slot, amount, simulate);
        isForceMode = false;
        return returnValue;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!layout.isOutput(slot) && !isForceMode)
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    public IIOConfig getConfig() {
        return config;
    }

    public MachineInventoryLayout getLayout() {
        return layout;
    }

    // region Sided access

    @Override
    public LazyOptional<IItemHandler> getCapability(Direction side) {
        return sideCache.computeIfAbsent(side, dir -> LazyOptional.of(() -> new Sided(this, dir))).cast();
    }

    @Override
    public void invalidateCaps() {
        for (LazyOptional<Sided> access : sideCache.values()) {
            access.invalidate();
        }
    }

    private static class Sided implements IItemHandler {

        private final MachineInventory master;

        private final Direction side;

        public Sided(MachineInventory master, Direction side) {
            this.master = master;
            this.side = side;
        }

        @Override
        public int getSlots() {
            return master.getSlots();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return master.getStackInSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (master.getConfig().getMode(side).canInput())
                return master.insertItem(slot, stack, simulate);
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (master.getConfig().getMode(side).canOutput())
                return master.extractItem(slot, amount, simulate);
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return master.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return master.isItemValid(slot, stack);
        }
    }

    // endregion
}
