package com.enderio.machines.common.io.item;

import com.enderio.base.common.blockentity.IOConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.EnumMap;

public class ItemHandlerMaster extends ItemStackHandler {

    private final EnumMap<Direction, SidedItemHandlerAccess> access = new EnumMap(Direction.class);
    private final IOConfig config;

    private MachineInventoryLayout layout;
    private boolean isForceMode = false;

    public ItemHandlerMaster(IOConfig config, MachineInventoryLayout layout) {
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

    public SidedItemHandlerAccess getAccess(Direction direction) {
        return access.computeIfAbsent(direction,
            dir -> new SidedItemHandlerAccess(this, dir));
    }

    public IOConfig getConfig() {
        return config;
    }

    public MachineInventoryLayout getLayout() {
        return layout;
    }
}
