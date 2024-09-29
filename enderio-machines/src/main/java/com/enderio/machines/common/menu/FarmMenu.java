package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.FarmingStationBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class FarmMenu extends MachineMenu<FarmingStationBlockEntity> {

    public FarmMenu(@Nullable FarmingStationBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.FARMING_STATION.get(), pContainerId);
        
        if (blockEntity != null) {
            // Capacitor slot
            addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 63));

            // Tool inputs TODO: Shadow slots to show compatible tools?
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.AXE, 44, 19));
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.HOE, 44 + 18, 19));
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.SHEAR, 44 + 18 * 2, 19));

            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.NE, 53, 44));
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.SE, 53 + 18, 44));
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.SW, 53, 44 + 18));
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.NW, 53 + 18, 44 + 18));

            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.BONEMEAL.get(0), 116, 19));
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.BONEMEAL.get(1), 116 + 18, 19));

            for (int i = 0; i < 6; i++) {
                addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.OUTPUT.get(i), 107 + 18 * (i % 3), i < 3 ? 44 : 44 + 18));
            }
        }

        addInventorySlots(8,87);
    }

    public static FarmMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof FarmingStationBlockEntity castBlockEntity) {
            return new FarmMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new FarmMenu(null, inventory, pContainerId);
    }
}
