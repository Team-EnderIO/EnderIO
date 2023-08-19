package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.DrainBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class DrainMenu extends MachineMenu<DrainBlockEntity> {
    public DrainMenu(@Nullable DrainBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.DRAIN.get(), pContainerId);

        if (blockEntity != null && blockEntity.requiresCapacitor()) {
            addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));
        }

        addInventorySlots(8, 84);
    }

    public static DrainMenu factory(@Nullable MenuType<DrainMenu> pMenuType, int pContainerId, Inventory inventory,
        FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof DrainBlockEntity castBlockEntity)
            return new DrainMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new DrainMenu(null, inventory, pContainerId);
    }
}
