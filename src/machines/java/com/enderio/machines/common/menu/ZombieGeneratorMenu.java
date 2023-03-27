package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.ZombieGeneratorBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class ZombieGeneratorMenu extends MachineMenu<ZombieGeneratorBlockEntity> {

    public ZombieGeneratorMenu(@Nullable ZombieGeneratorBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.ZOMBIE_GENERATOR.get(), pContainerId);

        if (blockEntity != null) {
            if (blockEntity.requiresCapacitor()) {
                addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));
            }
        }

        addInventorySlots(8, 84);
    }

    public static ZombieGeneratorMenu factory(@Nullable MenuType<ZombieGeneratorMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());

        if (entity instanceof ZombieGeneratorBlockEntity castBlockEntity)
            return new ZombieGeneratorMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");

        return new ZombieGeneratorMenu(null, inventory, pContainerId);
    }
}
