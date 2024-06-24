package com.enderio.core.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBlockEntityMenu<T extends BlockEntity> extends BaseEnderMenu {

    @Nullable
    private final T blockEntity;

    protected BaseBlockEntityMenu(@Nullable MenuType<?> menuType, int containerId, @Nullable T blockEntity, Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.blockEntity = blockEntity;
    }

    @Nullable
    public T getBlockEntity() {
        return blockEntity;
    }
}
