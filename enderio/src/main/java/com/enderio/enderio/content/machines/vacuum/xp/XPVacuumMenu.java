package com.enderio.enderio.content.machines.vacuum.xp;

import com.enderio.enderio.content.machines.vacuum.VacuumMenu;
import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import com.enderio.enderio.foundation.fluid.FluidStorageSyncSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class XPVacuumMenu extends VacuumMenu<XPVacuumBlockEntity> {

    private final FluidStorageSyncSlot fluidTankSlot;

    public XPVacuumMenu(int containerId, Inventory inventory, XPVacuumBlockEntity blockEntity) {
        super(EIOMenus.XP_VACUUM.get(), containerId, inventory, blockEntity);
        addSlots();

        fluidTankSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> new FluidStorageInfo(blockEntity.getStoredFluid(), XPVacuumBlockEntity.CAPACITY)));
    }

    public XPVacuumMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.XP_VACUUM.get(), containerId, playerInventory, buf, EIOBlockEntities.XP_VACUUM.get());
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
