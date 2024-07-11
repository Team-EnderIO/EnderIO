package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.CrafterBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class CrafterMenu extends PoweredMachineMenu<CrafterBlockEntity> {

    public static int INPUTS_INDEX = 11;
    public static int INPUT_COUNT = 9;
    public static int LAST_INDEX = 20;

    public CrafterMenu(@Nullable CrafterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(MachineMenus.CRAFTER.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            //Total slots = 21
            //Capacitor slot [0]
            this.addSlot(new MachineSlot(getMachineInventory(), 0, 6, 60));

            // Main storage slots [1-9]
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.addSlot(new MachineSlot(getMachineInventory(), CrafterBlockEntity.INPUT.get((3 * i) + j), 113 + (j * 18), 16 + (i * 18)));
                }
            }

            // Main output slot [10]
            this.addSlot(new MachineSlot(getMachineInventory(), CrafterBlockEntity.OUTPUT, 172, 34));

            // Recipe Display slots [11-19]
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.addSlot(new GhostMachineSlot(getMachineInventory(), CrafterBlockEntity.GHOST.get((3 * i) + j), 31 + (j * 18), 16 + (i * 18)));
                }
            }

            // Recipe Output slot [20]
            this.addSlot(new PreviewMachineSlot(getMachineInventory(), CrafterBlockEntity.PREVIEW, 90, 34));
        }

        addPlayerInventorySlots(30, 84);
    }

    public static CrafterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof CrafterBlockEntity castBlockEntity) {
            return new CrafterMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new CrafterMenu(null, inventory, pContainerId);
    }

}

