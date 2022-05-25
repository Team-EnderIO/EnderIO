package com.enderio.machines.common.blockentity.data.sidecontrol.item;

import com.enderio.machines.common.blockentity.data.sidecontrol.IOConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.EnumMap;

// TODO: PR 27 - Consider an interface IMachineInventory with easier accessors for inputs, i.e. getInput(1) for the 1st input. That way the capacitor slot can be put to slot 0 if present but it doesn't ruin machine logic.
public class ItemHandlerMaster extends ItemStackHandler {

    private final EnumMap<Direction, SidedItemHandlerAccess> access = new EnumMap(Direction.class);
    private final IOConfig config;

    private ItemSlotLayout layout;
    private boolean isForceMode = false;

    public ItemHandlerMaster(IOConfig config, ItemSlotLayout layout) {
        super(layout.getSlotCount());
        this.config = config;
        this.layout = layout;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!isForceMode) {
            if (layout.isSlotType(slot, ItemSlotLayout.SlotType.OUTPUT) || !layout.validateStack(slot, stack))
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
        return isForceMode || (layout.validateStack(slot, stack) && !layout.isSlotType(slot, ItemSlotLayout.SlotType.OUTPUT));
    }

    @Override
    public int getSlotLimit(int slot) {
        if (layout.isSlotType(slot, ItemSlotLayout.SlotType.CAPACITOR))
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
        if (layout.isSlotType(slot, ItemSlotLayout.SlotType.INPUT) && !isForceMode)
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

    public ItemSlotLayout getLayout() {
        return layout;
    }
}
