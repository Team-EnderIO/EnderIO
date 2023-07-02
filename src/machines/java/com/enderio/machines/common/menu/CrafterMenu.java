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

    public static int INPUTS_INDEX = 11;
    public static int INPUT_COUNT = 9;
    public static int LAST_INDEX = 20;

    public CrafterMenu(CrafterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.CRAFTER.get(), pContainerId);
        if (blockEntity != null) {
            //Total slots = 21
            //Capacitor slot [0]
            this.addSlot(new MachineSlot(blockEntity.getInventory(), 0, 6, 60));

            // Main storage slots [1-9]
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.addSlot(new MachineSlot(blockEntity.getInventory(), CrafterBlockEntity.INPUT.get((3 * i) + j), 113 + (j * 18), 16 + (i * 18)));
                }
            }
            // Main output slot [10]
            this.addSlot(new MachineSlot(blockEntity.getInventory(), CrafterBlockEntity.OUTPUT, 172, 34));
            // Recipe Display slots [11-19]
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.addSlot(new GhostMachineSlot(blockEntity.getInventory(), CrafterBlockEntity.GHOST.get((3 * i) + j), 31 + (j * 18), 16 + (i * 18)));
                }
            }
            // Recipe Output slot [20]
            this.addSlot(new PreviewMachineSlot(blockEntity.getInventory(), CrafterBlockEntity.PREVIEW, 90, 34));

            addInventorySlots(30, 84);
        }
    }

    public static CrafterMenu factory(@Nullable MenuType<CrafterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof CrafterBlockEntity castBlockEntity)
            return new CrafterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new CrafterMenu(null, inventory, pContainerId);
    }

}

