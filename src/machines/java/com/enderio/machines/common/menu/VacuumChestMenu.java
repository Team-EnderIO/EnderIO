package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.VacuumChestBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class VacuumChestMenu extends MachineMenu<VacuumChestBlockEntity> {

    public VacuumChestMenu(VacuumChestBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.VACUUM_CHEST.get(), pContainerId);
        if (blockEntity != null) {
            for (int j = 0; j < 3; ++j) {
                for (int k = 0; k < 9; ++k) {
                    this.addSlot(new MachineSlot(blockEntity.getInventory(), k + j * 9, 8 + k * 18, 18 + j * 18));
                }
            }
            this.addSlot(new MachineSlot(blockEntity.getInventory(), 27, 8, 86));
        }
        addInventorySlots(8, 124);
    }

    public static VacuumChestMenu factory(@Nullable MenuType<VacuumChestMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof VacuumChestBlockEntity castBlockEntity)
            return new VacuumChestMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new VacuumChestMenu(null, inventory, pContainerId);
    }
}