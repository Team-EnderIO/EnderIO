package com.enderio.enderio.foundation.menu;

import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

/*
 * This slot if only meant for previews. Players can't interact with this slot.
 * */

public class PreviewMachineSlot extends MachineSlot {
    public PreviewMachineSlot(ItemStorage itemStorage, int index, int xPosition, int yPosition) {
        super(itemStorage, index, xPosition, yPosition);

        // TODO: May have to abandon the safety here? perhaps 'preview' shouldn't be in the inventory at all though!!!
        // Check config, we need to get this right or bad stuff will happen.
//        var layout = itemStorage.layout();
//        if (layout.slotConfig(index).canInsert() || layout.slotConfig(index).canExtract()) {
//            throw new RuntimeException("Preview slot can be externally modified!!");
//        }
    }

    public PreviewMachineSlot(ItemStorage itemStorage, SingleResourceSlotKey<ItemResource> slotId, int xPosition, int yPosition) {
        this(itemStorage, slotId.index(itemStorage), xPosition, yPosition);
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
