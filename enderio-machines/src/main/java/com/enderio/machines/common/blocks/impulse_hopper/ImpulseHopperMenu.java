package com.enderio.machines.common.blocks.impulse_hopper;

import com.enderio.machines.common.blocks.base.menu.GhostMachineSlot;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ImpulseHopperMenu extends PoweredMachineMenu<ImpulseHopperBlockEntity> {

    public ImpulseHopperMenu(int containerId, Inventory inventory, ImpulseHopperBlockEntity blockEntity) {
        super(MachineMenus.IMPULSE_HOPPER.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public ImpulseHopperMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.IMPULSE_HOPPER.get(), MachineBlockEntities.IMPULSE_HOPPER.get(), containerId,
                playerInventory, buf);
        addSlots();
    }

    private void addSlots() {
        for (int i = 0; i < 6; i++) {
            this.addSlot(
                    new MachineSlot(getMachineInventory(), ImpulseHopperBlockEntity.INPUT.get(i), 8 + 36 + i * 18, 9));
            this.addSlot(new GhostMachineSlot(getMachineInventory(), ImpulseHopperBlockEntity.GHOST.get(i),
                    8 + 36 + i * 18, 9 + 27));
            this.addSlot(new MachineSlot(getMachineInventory(), ImpulseHopperBlockEntity.OUTPUT.get(i), 8 + 36 + i * 18,
                    9 + 54));
        }

        this.addSlot(new MachineSlot(getMachineInventory(), 18, 11, 60));

        addPlayerInventorySlots(8, 84);
    }
}
