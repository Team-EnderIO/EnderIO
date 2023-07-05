package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.PaintingMachineBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class PaintingMachineMenu extends MachineMenu<PaintingMachineBlockEntity> {
    public PaintingMachineMenu(@Nullable PaintingMachineBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.PAINTING_MACHINE.get(), pContainerId);
        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));

            addSlot(new MachineSlot(blockEntity.getInventory(), PaintingMachineBlockEntity.INPUT, 67, 34));
            addSlot(new MachineSlot(blockEntity.getInventory(), PaintingMachineBlockEntity.PAINT, 38, 34));
            addSlot(new MachineSlot(blockEntity.getInventory(), PaintingMachineBlockEntity.OUTPUT, 120, 34));
        }
        addInventorySlots(8,84);
    }

    public static PaintingMachineMenu factory(@Nullable MenuType<PaintingMachineMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof PaintingMachineBlockEntity castBlockEntity)
            return new PaintingMachineMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new PaintingMachineMenu(null, inventory, pContainerId);
    }
}
