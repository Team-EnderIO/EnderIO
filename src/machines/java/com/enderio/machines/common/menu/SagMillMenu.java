package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class SagMillMenu extends MachineMenu<SagMillBlockEntity> {
    public static int INPUTS_INDEX = 1;
    public static int INPUT_COUNT = 1;
    public static int LAST_INDEX = 6;

    public SagMillMenu(@Nullable SagMillBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.SAG_MILL.get(), pContainerId);

        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));

            addSlot(new MachineSlot(blockEntity.getInventory(), SagMillBlockEntity.INPUT, 80, 12));

            addSlot(new MachineSlot(blockEntity.getInventory(), SagMillBlockEntity.OUTPUT.get(0), 49, 59));
            addSlot(new MachineSlot(blockEntity.getInventory(), SagMillBlockEntity.OUTPUT.get(1), 70, 59));
            addSlot(new MachineSlot(blockEntity.getInventory(), SagMillBlockEntity.OUTPUT.get(2), 91, 59));
            addSlot(new MachineSlot(blockEntity.getInventory(), SagMillBlockEntity.OUTPUT.get(3), 112, 59));

            addSlot(new MachineSlot(blockEntity.getInventory(), SagMillBlockEntity.GRINDING_BALL, 122, 23));
        }
        addInventorySlots(8,84);
    }

    public static SagMillMenu factory(@Nullable MenuType<SagMillMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof SagMillBlockEntity castBlockEntity)
            return new SagMillMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new SagMillMenu(null, inventory, pContainerId);
    }
}
