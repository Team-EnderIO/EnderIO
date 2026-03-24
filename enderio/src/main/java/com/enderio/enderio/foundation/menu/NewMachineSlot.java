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

public class NewMachineSlot extends ResourceHandlerSlot implements SlotWithOverlay {

    private final int index;
    @Nullable
    private Identifier foregroundSprite;

    public NewMachineSlot(ItemStorage handler, int index, int xPosition, int yPosition) {
        super(handler, handler::set, index, xPosition, yPosition);
        this.index = index;
    }

    public NewMachineSlot(ItemStorage handler, ResourceSlotId<ItemResource> slotId, int xPosition, int yPosition) {
        this(handler, slotId.index(handler), xPosition, yPosition);
    }

    @Override
    public ItemStorage getResourceHandler() {
        return (ItemStorage) super.getResourceHandler();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return getResourceHandler().layout().slotConfig(this.index).canManualInsert() && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return getResourceHandler().layout().slotConfig(this.index).canManualExtract() && super.mayPickup(playerIn);
    }

    public boolean canQuickInsertStack() {
        return isActive() && getResourceHandler().layout().slotConfig(getSlotIndex()).canManualInsert();
    }

    @Override
    @Nullable
    public Identifier getForegroundSprite() {
        return foregroundSprite;
    }

    public NewMachineSlot setForeground(Identifier sprite) {
        foregroundSprite = sprite;
        return this;
    }
}
