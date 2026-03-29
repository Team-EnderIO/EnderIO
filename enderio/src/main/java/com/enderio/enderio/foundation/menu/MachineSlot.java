package com.enderio.enderio.foundation.menu;

import com.enderio.core.common.menu.SlotWithOverlay;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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
        if (stack.isEmpty()) {
            return false;
        }

        try (var tx = Transaction.openRoot()) {
            // Simulated extraction
            return getResourceHandler().internalInsert(index, ItemResource.of(stack), 1, tx) == 1;
        }
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        var resource = getResourceHandler().getResource(index);
        if (resource.isEmpty()) {
            return false;
        }

        try (var tx = Transaction.openRoot()) {
            // Simulated extraction
            return getResourceHandler().internalExtract(index, resource, 1, tx) == 1;
        }
    }

    public boolean canQuickInsertStack() {
        return isActive() && getResourceHandler().layout().slotConfig(index).canManualInsert();
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
