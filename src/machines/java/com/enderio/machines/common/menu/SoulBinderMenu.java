package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.SoulBinderBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class SoulBinderMenu extends MachineMenu<SoulBinderBlockEntity> {

    public SoulBinderMenu(@Nullable SoulBinderBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.SOUL_BINDER.get(), pContainerId);

        if (blockEntity != null) {
            if (blockEntity.requiresCapacitor()) {
                addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));
            }
            addSlot(new MachineSlot(blockEntity.getInventory(), 0, 38, 34));
            addSlot(new MachineSlot(blockEntity.getInventory(), 1, 59, 34));
            addSlot(new MachineSlot(blockEntity.getInventory(), 2, 112, 34));
            addSlot(new MachineSlot(blockEntity.getInventory(), 3, 134, 34));

        }

        addInventorySlots(8,84);
    }

    public static SoulBinderMenu factory(@Nullable MenuType<SoulBinderMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof SoulBinderBlockEntity castBlockEntity)
            return new SoulBinderMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new SoulBinderMenu(null, inventory, pContainerId);
    }
}
