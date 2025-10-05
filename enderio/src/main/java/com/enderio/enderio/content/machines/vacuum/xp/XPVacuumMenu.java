package com.enderio.enderio.content.machines.vacuum.xp;

import com.enderio.enderio.content.machines.vacuum.VacuumMenu;
import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import com.enderio.enderio.foundation.fluid.FluidStorageSyncSlot;
import com.enderio.enderio.init.MachineBlockEntities;
import com.enderio.enderio.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class XPVacuumMenu extends VacuumMenu<XPVacuumBlockEntity> {

    private final FluidStorageSyncSlot fluidTankSlot;

    public XPVacuumMenu(int containerId, Inventory inventory, XPVacuumBlockEntity blockEntity) {
        super(MachineMenus.XP_VACUUM.get(), containerId, inventory, blockEntity);
        addSlots();

        fluidTankSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidTank())));
    }

    public XPVacuumMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.XP_VACUUM.get(), containerId, playerInventory, buf, MachineBlockEntities.XP_VACUUM.get());
        addSlots();

        fluidTankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
    }

    private void addSlots() {
        addPlayerInventorySlots(8, 70);
    }

    public FluidStorageInfo getFluidTank() {
        return fluidTankSlot.get();
    }
}
