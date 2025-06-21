package com.enderio.machines.common.blocks.crafter;

import com.enderio.machines.common.blocks.base.menu.GhostMachineSlot;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.blocks.base.menu.PreviewMachineSlot;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class CrafterMenu extends PoweredMachineMenu<CrafterBlockEntity> {

    public static final int INPUTS_INDEX = 11;
    public static final int INPUT_COUNT = 9;
    public static final int LAST_INDEX = 20;

    public CrafterMenu(int containerId, Inventory inventory, CrafterBlockEntity blockEntity) {
        super(MachineMenus.CRAFTER.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public CrafterMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.CRAFTER.get(), containerId, playerInventory, buf, MachineBlockEntities.CRAFTER.get());
        addSlots();
    }

    private void addSlots() {
        // Total slots = 21
        // Capacitor slot [0]
        addCapacitorSlot(6, 60);

        // Main storage slots [1-9]
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new MachineSlot(getMachineInventory(), CrafterBlockEntity.INPUT.get((3 * i) + j),
                        113 + (j * 18), 16 + (i * 18)));
            }
        }

        // Main output slot [10]
        this.addSlot(new MachineSlot(getMachineInventory(), CrafterBlockEntity.OUTPUT, 172, 34));

        // Recipe Display slots [11-19]
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new GhostMachineSlot(getMachineInventory(), CrafterBlockEntity.GHOST.get((3 * i) + j),
                        31 + (j * 18), 16 + (i * 18)));
            }
        }

        // Recipe Output slot [20]
        this.addSlot(new PreviewMachineSlot(getMachineInventory(), CrafterBlockEntity.PREVIEW, 90, 34));

        addPlayerInventorySlots(30, 84);
    }
}
