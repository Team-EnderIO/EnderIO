package com.enderio.machines.common.blocks.base.menu;

import com.enderio.core.common.menu.SlotWithOverlay;
import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class MachineSlot extends SlotItemHandler implements SlotWithOverlay {

    @Nullable
    private ResourceLocation foregroundSprite;

    public MachineSlot(MachineInventory itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public MachineSlot(MachineInventory itemHandler, SingleSlotAccess access, int xPosition, int yPosition) {
        super(itemHandler, access.getIndex(), xPosition, yPosition);
    }

    @Override
    public MachineInventory getItemHandler() {
        return (MachineInventory) super.getItemHandler();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return getItemHandler().getLayout().guiCanInsert(this.getSlotIndex()) && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return getItemHandler().getLayout().guiCanExtract(this.getSlotIndex()) && super.mayPickup(playerIn);
    }

    public boolean canQuickInsertStack() {
        return getItemHandler().getLayout().guiCanInsert(getSlotIndex());
    }

    @Override
    @Nullable
    public ResourceLocation getForegroundSprite() {
        return foregroundSprite;
    }

    public MachineSlot setForeground(ResourceLocation sprite) {
        foregroundSprite = sprite;
        return this;
    }
}
