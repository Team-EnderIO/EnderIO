package com.enderio.core.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBlockEntityMenu<T extends BlockEntity> extends BaseEnderMenu {

    // TODO: Should block entity even be nullable?
    // Why create the menu if we failed to attach correctly...

    @Nullable
    private final T blockEntity;

    protected BaseBlockEntityMenu(@Nullable MenuType<?> menuType, int containerId, @Nullable T blockEntity,
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
