package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.FarmingStationBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class FarmingStationMenu extends MachineMenu<FarmingStationBlockEntity> {
    public FarmingStationMenu(@Nullable FarmingStationBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.FARMING_STATION.get(), pContainerId);
        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12 + 42, 60));

            //Tool slots
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.HOE, 44 + 42, 19));
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.AXE, 62 + 42, 19));
            addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.SHEARS, 80 + 42, 19));

            //Bonemeal slots
            for (int i = 0; i < 2; i++) {
                addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.FERTILIZERS.get(i), 116 + 42 + 18 * i, 19));
            }

            //Seed slots
            for (int i = 0; i < 4; i++) {
                addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.INPUTS.get(i), 53 + 42 + 18 * (i % 2), i < 2 ? 44 : 62));
            }

            //Output, seeds / trees harvested
            for (int i = 0; i < 6; i++) {
                addSlot(new MachineSlot(blockEntity.getInventory(), FarmingStationBlockEntity.OUTPUT.get(i), 107 + 42 + 18 * (i % 3), i < 3 ? 44 : 62));
            }
        }
        addInventorySlots(8 + 42, 87);
    }

    public static FarmingStationMenu factory(@Nullable MenuType<FarmingStationMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof FarmingStationBlockEntity castBlockEntity)
            return new FarmingStationMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new FarmingStationMenu(null, inventory, pContainerId);
    }

}
