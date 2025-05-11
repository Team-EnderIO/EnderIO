package com.enderio.machines.common.blocks.obelisks;

import com.enderio.core.common.network.menu.IntSyncSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class ObeliskMenu<T extends ObeliskBlockEntity<T>> extends PoweredMachineMenu<T> {
    public static final int INCREASE_BUTTON_ID = 0;
    public static final int DECREASE_BUTTON_ID = 1;
    public static final int VISIBILITY_BUTTON_ID = 2;

    private final IntSyncSlot maxRange;

    protected ObeliskMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, T blockEntity) {
        super(menuType, containerId, playerInventory, blockEntity);

        maxRange = addSyncSlot(IntSyncSlot.readOnly(blockEntity::getMaxRange));
    }

    protected ObeliskMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory,
            RegistryFriendlyByteBuf buf, BlockEntityType<? extends T>... blockEntityTypes) {
        super(menuType, containerId, playerInventory, buf, blockEntityTypes);

        maxRange = addSyncSlot(IntSyncSlot.standalone());
    }

    public boolean isRangeVisible() {
        // This is synced via the block entity.
        return getBlockEntity().isRangeVisible();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        // noinspection DuplicatedCode
        var blockEntity = getBlockEntity();
        switch (id) {
        case INCREASE_BUTTON_ID:
            blockEntity.increaseRange();
            return true;
        case DECREASE_BUTTON_ID:
            blockEntity.decreaseRange();
            return true;
        case VISIBILITY_BUTTON_ID:
            blockEntity.setRangeVisible(!isRangeVisible());
            return true;
        default:
            return false;
        }
    }

    public int getMaxRange() {
        return maxRange.get();
    }
}
