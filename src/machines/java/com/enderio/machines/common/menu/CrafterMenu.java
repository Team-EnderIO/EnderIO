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
        //        if (blockEntity != null) {
        //            for (int j = 0; j < 2; ++j) {
        //                for (int k = 0; k < 6; ++k) {
        //                    this.addSlot(new MachineSlot(blockEntity.getInventory(), k + j * 6, 8 + 36 + k * 18, 9 + j * 54));
        //                }
        //            }
        //            for (int k = 0; k < 6; ++k) {
        //                this.addSlot(new GhostMachineSlot(blockEntity.getInventory(), 12 + k, 8 + 36 + k * 18, 9 + 27));
        //            }
        //            this.addSlot(new MachineSlot(blockEntity.getInventory(), 18, 11, 60));
        //        }
        //        addInventorySlots(8, 84);
    }

    public static CrafterMenu factory(@Nullable MenuType<CrafterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof CrafterBlockEntity castBlockEntity)
            return new CrafterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new CrafterMenu(null, inventory, pContainerId);
    }

}

