package com.enderio.machines.common.menu;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class SagMillMenu extends MachineMenu<SagMillBlockEntity> {
    public SagMillMenu(@Nullable SagMillBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.SAG_MILL.get(), pContainerId);

        if (blockEntity != null) {
            // Capacitor slot
            if (blockEntity.requiresCapacitor()) {
                addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));
            }

            // Input
            addSlot(new MachineSlot(blockEntity.getInventory(), 0, 80, 12));

            // Outputs
            addSlot(new MachineSlot(blockEntity.getInventory(), 1, 49, 59));
            addSlot(new MachineSlot(blockEntity.getInventory(), 2, 70, 59));
            addSlot(new MachineSlot(blockEntity.getInventory(), 3, 91, 59));
            addSlot(new MachineSlot(blockEntity.getInventory(), 4, 112, 59));

            // Grindingball slot
            if (blockEntity.getTier() != MachineTier.SIMPLE) {
                addSlot(new MachineSlot(blockEntity.getInventory(), 5, 122, 23));
            }
        }
        addInventorySlots(8,84);
    }

    public static SagMillMenu factory(@Nullable MenuType<SagMillMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof SagMillBlockEntity castBlockEntity)
            return new SagMillMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new SagMillMenu(null, inventory, pContainerId);
    }
}
