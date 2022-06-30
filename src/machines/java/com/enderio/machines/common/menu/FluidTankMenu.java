package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;

public class FluidTankMenu extends MachineMenu<FluidTankBlockEntity> {

    public FluidTankMenu(@Nullable FluidTankBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.FLUID_TANK.get(), pContainerId);
        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getInventory(), 0, 44, 21));
            addSlot(new MachineSlot(blockEntity.getInventory(), 1, 44, 52));
            addSlot(new MachineSlot(blockEntity.getInventory(), 2, 116, 21));
            addSlot(new MachineSlot(blockEntity.getInventory(), 3, 116, 52));
        }
        addInventorySlots(8,84);
    }

    public static FluidTankMenu factory(@Nullable MenuType<FluidTankMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof FluidTankBlockEntity castBlockEntity)
            return new FluidTankMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new FluidTankMenu(null, inventory, pContainerId);

    }
}
