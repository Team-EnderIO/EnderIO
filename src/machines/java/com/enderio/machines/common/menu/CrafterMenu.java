package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.CrafterBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class CrafterMenu extends MachineMenu<CrafterBlockEntity> {

    public CrafterMenu(CrafterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.CRAFTER.get(), pContainerId);
        this.addSlot(new MachineSlot(blockEntity.getInventory(), 0, 6, 60));
        addInventorySlots(30, 84);
        if (blockEntity != null) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.addSlot(new MachineSlot(blockEntity.getInventory(), 1 + (3 * i) + j, 113 + (j * 18), 16 + (i * 18)));
                }
            }
            this.addSlot(new MachineSlot(blockEntity.getInventory(), 10, 172, 34));
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.addSlot(new GhostMachineSlot(blockEntity.getInventory(), 11 + (3 * i) + j, 31 + (j * 18), 16 + (i * 18), 1));
                }
            }
            this.addSlot(new PreviewSlot(blockEntity.getInventory(), 20, 90, 34));
        }
    }

    public static CrafterMenu factory(@Nullable MenuType<CrafterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof CrafterBlockEntity castBlockEntity)
            return new CrafterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new CrafterMenu(null, inventory, pContainerId);
    }

}

