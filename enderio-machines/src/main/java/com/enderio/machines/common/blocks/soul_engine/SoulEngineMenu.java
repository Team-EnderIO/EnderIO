package com.enderio.machines.common.blocks.soul_engine;

import com.enderio.machines.common.blocks.base.fluid.FluidStorageInfo;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageSyncSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class SoulEngineMenu extends PoweredMachineMenu<SoulEngineBlockEntity> {

    private final FluidStorageSyncSlot fluidTankSlot;

    public SoulEngineMenu(int containerId, Inventory inventory, SoulEngineBlockEntity blockEntity) {
        super(MachineMenus.SOUL_ENGINE.get(), containerId, inventory, blockEntity);
        addSlots();

        fluidTankSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidTank())));
    }

    public SoulEngineMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.SOUL_ENGINE.get(), MachineBlockEntities.SOUL_ENGINE.get(), containerId, playerInventory,
                buf);
        addSlots();

        fluidTankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addPlayerInventorySlots(8, 84);
    }

    public FluidStorageInfo getFluidTank() {
        return fluidTankSlot.get();
    }
}
