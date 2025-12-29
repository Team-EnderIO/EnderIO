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

    @Nullable
    private Identifier foregroundSprite;

    public MachineSlot(MachineInventory handler, int index, int xPosition, int yPosition) {
        super(handler, handler::set, index, xPosition, yPosition);
    }

    public MachineSlot(MachineInventory handler, SingleSlotAccess access, int xPosition, int yPosition) {
        super(handler, handler::set, access.getIndex(), xPosition, yPosition);
    }

    @Override
    public MachineInventory getResourceHandler() {
        return (MachineInventory) super.getResourceHandler();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return getResourceHandler().getLayout().guiCanInsert(this.getSlotIndex()) && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return getResourceHandler().getLayout().guiCanExtract(this.getSlotIndex()) && super.mayPickup(playerIn);
    }

    public boolean canQuickInsertStack() {
        return isActive() && getResourceHandler().getLayout().guiCanInsert(getSlotIndex());
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
