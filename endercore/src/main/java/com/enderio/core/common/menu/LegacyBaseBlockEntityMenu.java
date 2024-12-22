package com.enderio.core.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@Deprecated(forRemoval = true, since = "7.1")
public abstract class LegacyBaseBlockEntityMenu<T extends BlockEntity> extends BaseEnderMenu {

    // TODO: Should block entity even be nullable?
    // Why create the menu if we failed to attach correctly...

    @Nullable
    private final T blockEntity;

    protected LegacyBaseBlockEntityMenu(@Nullable MenuType<?> menuType, int containerId, @Nullable T blockEntity,
            Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.blockEntity = blockEntity;
    }

    // TODO: This will become protected once all menus are driving screens directly.
    @Nullable
    public T getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return getBlockEntity() != null && Container.stillValidBlockEntity(getBlockEntity(), pPlayer);
    }
}
