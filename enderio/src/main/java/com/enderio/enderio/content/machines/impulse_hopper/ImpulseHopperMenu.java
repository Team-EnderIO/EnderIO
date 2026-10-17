package com.enderio.enderio.content.machines.impulse_hopper;

import com.enderio.enderio.foundation.menu.GhostMachineSlot;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ImpulseHopperMenu extends PoweredMachineMenu<ImpulseHopperBlockEntity> {

    public ImpulseHopperMenu(int containerId, Inventory inventory, ImpulseHopperBlockEntity blockEntity) {
        super(EIOMenus.IMPULSE_HOPPER.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public ImpulseHopperMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.IMPULSE_HOPPER.get(), containerId, playerInventory, buf,
            EIOBlockEntities.IMPULSE_HOPPER.get());
        addSlots();
    }

    private void addSlots() {
        for (int i = 0; i < 6; i++) {
            this.addSlot(
                    new MachineSlot(getMachineInventory(), ImpulseHopperBlockEntity.INPUT.slot(i), 8 + 36 + i * 18, 9));
            this.addSlot(new GhostMachineSlot(getMachineInventory(), ImpulseHopperBlockEntity.GHOST.slot(i),
                    8 + 36 + i * 18, 9 + 27));
            this.addSlot(new MachineSlot(getMachineInventory(), ImpulseHopperBlockEntity.OUTPUT.slot(i), 8 + 36 + i * 18,
                    9 + 54));
        }

        this.addSlot(new MachineSlot(getMachineInventory(), 18, 11, 60));

        addPlayerInventorySlots(8, 84);
    }
}
