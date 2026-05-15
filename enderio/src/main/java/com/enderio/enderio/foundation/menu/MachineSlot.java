package com.enderio.enderio.foundation.menu;

import com.enderio.core.common.menu.SlotWithOverlay;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import net.minecraft.resources.Identifier;
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
    public ItemStorage getResourceHandler() {
        return (ItemStorage) super.getResourceHandler();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && getResourceHandler().layout().slotConfig(index).guiRules().canInsert(ItemResource.of(stack));
    }

    public boolean canQuickInsertStack(ItemResource itemResource) {
        return isActive() && getResourceHandler().layout().slotConfig(index).guiRules().canInsert(itemResource);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return getResourceHandler().layout().slotConfig(index).getCapacityAsInt(ItemResource.of(stack));
    }

    @Override
    public int getMaxStackSize() {
        return getResourceHandler().layout().slotConfig(index).getCapacityAsInt(ItemResource.EMPTY);
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
