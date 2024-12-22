package com.enderio.machines.common.blocks.vacuum.xp;

import com.enderio.machines.common.blocks.base.fluid.FluidStorageInfo;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageSyncSlot;
import com.enderio.machines.common.blocks.vacuum.VacuumMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
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
        super(MachineMenus.XP_VACUUM.get(), MachineBlockEntities.XP_VACUUM.get(), containerId, playerInventory, buf);
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
