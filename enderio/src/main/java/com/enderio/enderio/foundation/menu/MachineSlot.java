package com.enderio.enderio.foundation.menu;

import com.enderio.core.common.menu.SlotWithOverlay;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.Nullable;

public class MachineSlot extends ResourceHandlerSlot implements SlotWithOverlay {

    private final int index;
    @Nullable
    private Identifier foregroundSprite;

    public MachineSlot(ItemStorage itemStorage, int index, int xPosition, int yPosition) {
        super(itemStorage, itemStorage::set, index, xPosition, yPosition);
        this.index = index;
    }

    public MachineSlot(ItemStorage itemStorage, ResourceSlotId<ItemResource> slotId, int xPosition, int yPosition) {
        this(itemStorage, slotId.index(itemStorage), xPosition, yPosition);
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
