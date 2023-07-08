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
    public static int INPUTS_INDEX = 3;
    public static int INPUT_COUNT = 6;
    public static int LAST_INDEX = 9;

    public SlicerMenu(@Nullable SlicerBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.SLICE_N_SPLICE.get(), pContainerId);
        
        if (blockEntity != null) {
            // Capacitor slot
            addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));

            // Tool inputs TODO: Shadow slots to show compatible tools?
            addSlot(new MachineSlot(blockEntity.getInventory(), SlicerBlockEntity.AXE, 54, 16));
            addSlot(new MachineSlot(blockEntity.getInventory(), SlicerBlockEntity.SHEARS, 72, 16));

            for (int i = 0; i < 6; i++) {
                addSlot(new MachineSlot(blockEntity.getInventory(), SlicerBlockEntity.INPUTS.get(i), 44 + 18 * (i % 3), i < 3 ? 40 : 58));
            }
            addSlot(new MachineSlot(blockEntity.getInventory(), SlicerBlockEntity.OUTPUT, 134, 49));
        }

        addInventorySlots(8,84);
    }

    public static SlicerMenu factory(@Nullable MenuType<SlicerMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof SlicerBlockEntity castBlockEntity)
            return new SlicerMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new SlicerMenu(null, inventory, pContainerId);
    }
}
