package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.SlicerBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class SlicerMenu extends MachineMenu<SlicerBlockEntity> {
    public SlicerMenu(@Nullable SlicerBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.SLICE_N_SPLICE.get(), pContainerId);
        
        if (blockEntity != null) {
            // Capacitor slot
            if (blockEntity.requiresCapacitor()) {
                addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));
            }

            // Tool inputs TODO: Shadow slots to show compatible tools?
            addSlot(new MachineSlot(blockEntity.getInventory(), 6, 54, 16));
            addSlot(new MachineSlot(blockEntity.getInventory(), 7, 72, 16));

            // Item inputs
            addSlot(new MachineSlot(blockEntity.getInventory(), 0, 44,  40));
            addSlot(new MachineSlot(blockEntity.getInventory(), 1, 62, 40));
            addSlot(new MachineSlot(blockEntity.getInventory(), 2, 80, 40));
            addSlot(new MachineSlot(blockEntity.getInventory(), 3, 44, 58));
            addSlot(new MachineSlot(blockEntity.getInventory(), 4, 62, 58));
            addSlot(new MachineSlot(blockEntity.getInventory(), 5, 80, 58));

            // Output
            addSlot(new MachineSlot(blockEntity.getInventory(), 8, 134, 49));
        }

        addInventorySlots(8,84);
    }

    public static SlicerMenu factory(@Nullable MenuType<SlicerMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof SlicerBlockEntity castBlockEntity)
            return new SlicerMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new SlicerMenu(null, inventory, pContainerId);
    }
}
