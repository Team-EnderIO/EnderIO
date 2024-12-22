package com.enderio.machines.common.blocks.painting;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class PaintingMachineMenu extends PoweredMachineMenu<PaintingMachineBlockEntity> {

    private final FloatSyncSlot craftingProgressSlot;

    public PaintingMachineMenu(Inventory inventory, int pContainerId, PaintingMachineBlockEntity blockEntity) {
        super(MachineMenus.PAINTING_MACHINE.get(), pContainerId, inventory, blockEntity);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
    }

    public PaintingMachineMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.PAINTING_MACHINE.get(), MachineBlockEntities.PAINTING_MACHINE.get(), containerId,
                playerInventory, buf);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);

        addSlot(new MachineSlot(getMachineInventory(), PaintingMachineBlockEntity.INPUT, 67, 34));
        addSlot(new MachineSlot(getMachineInventory(), PaintingMachineBlockEntity.PAINT, 38, 34));
        addSlot(new MachineSlot(getMachineInventory(), PaintingMachineBlockEntity.OUTPUT, 120, 34));

        addPlayerInventorySlots(8, 84);
    }

    public float getCraftingProgress() {
        return craftingProgressSlot.get();
    }
}
