package com.enderio.conduits.common.menu;

import com.enderio.conduits.common.blockentity.ConduitBundle;
import com.enderio.conduits.common.blockentity.SlotData;
import com.enderio.conduits.common.blockentity.SlotType;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ConduitSlot extends SlotItemHandler {

    private final Supplier<Direction> visibleDirection;
    private final Supplier<Integer> visibleType;

    private final Direction visibleForDirection;
    private final int visibleForType;
    private final SlotType slotType;
    private final ConduitBundle bundle;

    public ConduitSlot(ConduitBundle bundle, IItemHandler itemHandler, Supplier<Direction> visibleDirection, Direction visibleForDirection, Supplier<Integer> visibleType, int visibleForType, SlotType slotType) {
        super(itemHandler, new SlotData(visibleForDirection, visibleForType, slotType).slotIndex(), slotType.getX(), slotType.getY());
        this.visibleDirection = visibleDirection;
        this.visibleType = visibleType;
        this.visibleForDirection = visibleForDirection;
        this.visibleForType = visibleForType;
        this.slotType = slotType;
        this.bundle = bundle;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isVisible() && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return isVisible() && super.mayPickup(playerIn);
    }

    @Override
    @NotNull
    public ItemStack remove(int amount) {
        return isVisible() ? super.remove(amount) : ItemStack.EMPTY;
    }

    public void updateVisibilityPosition() {
        if (isVisible()) {
            x = slotType.getX();
            y = slotType.getY();
        } else {
            x = Integer.MIN_VALUE;
            y = Integer.MIN_VALUE;
        }
    }

    private boolean isVisible() {
        return visibleDirection.get() == visibleForDirection
            && visibleType.get() == visibleForType
            && bundle.getTypes().size() > visibleForType
            && slotType.isAvailableFor(bundle.getTypes().get(visibleForType).getMenuData());
    }
}
