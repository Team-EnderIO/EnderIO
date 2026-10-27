package com.enderio.enderio.content.machines.killer_joe;

import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import com.enderio.enderio.foundation.fluid.FluidStorageSyncSlot;
import com.enderio.enderio.foundation.menu.MachineMenu;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class KillerJoeMenu extends MachineMenu<KillerJoeBlockEntity> {

    private final FluidStorageSyncSlot fluidTankSlot;

    // Server constructor
    public KillerJoeMenu(int containerId, Inventory inventory, KillerJoeBlockEntity blockEntity) {
        super(EIOMenus.KILLER_JOE.get(), containerId, inventory, blockEntity);
        addSlots();

        // Sync fluid tank
        fluidTankSlot = addSyncSlot(
            FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidStorage(), KillerJoeBlockEntity.TANK)));
    }

    // Client constructor (network)
    public KillerJoeMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.KILLER_JOE.get(), containerId, playerInventory, buf,
            EIOBlockEntities.KILLER_JOE.get());
        addSlots();

        // Standalone slots for client
        fluidTankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
    }

    private void addSlots() {
        // Weapon slot (centered)
        addSlot(new MachineSlot(getMachineInventory(), KillerJoeBlockEntity.WEAPON_SLOT, 48, 24));

        // Player inventory
        addPlayerInventorySlots(8, 84);
    }

    public FluidStorageInfo getFluidTank() {
        return fluidTankSlot.get();
    }
}
