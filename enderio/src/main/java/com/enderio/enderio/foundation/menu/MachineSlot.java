package com.enderio.enderio.foundation.menu;

import com.enderio.core.common.menu.SlotWithOverlay;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.Nullable;

public class MachineSlot extends ResourceHandlerSlot implements SlotWithOverlay {

    private final int index;
    @Nullable
    private Identifier foregroundSprite;

    public MachineSlot(MachineInventory handler, int index, int xPosition, int yPosition) {
        super(handler, handler::set, index, xPosition, yPosition);
        this.index = index;
    }

    public MachineSlot(MachineInventory handler, SingleSlotAccess access, int xPosition, int yPosition) {
        super(handler, handler::set, access.getIndex(), xPosition, yPosition);
        this.index = access.getIndex();
    }

    @Override
    public MachineInventory getResourceHandler() {
        return (MachineInventory) super.getResourceHandler();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return getResourceHandler().layout().guiCanInsert(this.index) && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return getResourceHandler().layout().guiCanExtract(this.index) && super.mayPickup(playerIn);
    }

    public boolean canQuickInsertStack() {
        return isActive() && getResourceHandler().layout().guiCanInsert(getSlotIndex());
    }

    @Override
    @Nullable
    public Identifier getForegroundSprite() {
        return foregroundSprite;
    }

    public MachineSlot setForeground(Identifier sprite) {
        foregroundSprite = sprite;
        return this;
    }
}
