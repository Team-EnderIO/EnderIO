package com.enderio.machines.common.blocks.base.menu;

import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/*
 * This slot if only meant for previews. Players can't interact with this slot.
 * */

public class PreviewMachineSlot extends MachineSlot {
    public PreviewMachineSlot(MachineInventory itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);

        // Check config, we need to get this right or bad stuff will happen.
        MachineInventoryLayout layout = itemHandler.getLayout();
        if (layout.canInsert(index) || layout.canExtract(index)) {
            throw new RuntimeException("Preview slot can be externally modified!!");
        }
    }

    public PreviewMachineSlot(MachineInventory itemHandler, SingleSlotAccess access, int xPosition, int yPosition) {
        this(itemHandler, access.getIndex(), xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
