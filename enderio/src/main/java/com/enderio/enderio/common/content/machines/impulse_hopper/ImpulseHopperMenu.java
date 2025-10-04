package com.enderio.enderio.common.content.machines.impulse_hopper;

import com.enderio.enderio.common.foundation.menu.GhostMachineSlot;
import com.enderio.enderio.common.foundation.menu.MachineSlot;
import com.enderio.enderio.common.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.common.init.MachineBlockEntities;
import com.enderio.enderio.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ImpulseHopperMenu extends PoweredMachineMenu<ImpulseHopperBlockEntity> {

    public ImpulseHopperMenu(int containerId, Inventory inventory, ImpulseHopperBlockEntity blockEntity) {
        super(MachineMenus.IMPULSE_HOPPER.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public ImpulseHopperMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.IMPULSE_HOPPER.get(), containerId, playerInventory, buf,
                MachineBlockEntities.IMPULSE_HOPPER.get());
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
