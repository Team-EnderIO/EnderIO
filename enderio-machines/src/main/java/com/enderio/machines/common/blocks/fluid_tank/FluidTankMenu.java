package com.enderio.machines.common.blocks.fluid_tank;

import com.enderio.machines.common.blocks.base.fluid.FluidStorageInfo;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineMenu;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class FluidTankMenu extends MachineMenu<FluidTankBlockEntity> {

    private final FluidStorageSyncSlot fluidTankSlot;

    public FluidTankMenu(int containerId, Inventory inventory, FluidTankBlockEntity blockEntity) {
        super(MachineMenus.FLUID_TANK.get(), containerId, inventory, blockEntity);
        addSlots();

        fluidTankSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidTank())));
    }

    public FluidTankMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.FLUID_TANK.get(), MachineBlockEntities.FLUID_TANK.get(), containerId, playerInventory, buf);
        addSlots();

        fluidTankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
    }

    private void addSlots() {
        addSlot(new MachineSlot(getMachineInventory(), FluidTankBlockEntity.FLUID_FILL_INPUT, 44, 21));
        addSlot(new MachineSlot(getMachineInventory(), FluidTankBlockEntity.FLUID_FILL_OUTPUT, 44, 52));
        addSlot(new MachineSlot(getMachineInventory(), FluidTankBlockEntity.FLUID_DRAIN_INPUT, 116, 21));
        addSlot(new MachineSlot(getMachineInventory(), FluidTankBlockEntity.FLUID_DRAIN_OUTPUT, 116, 52));

        addPlayerInventorySlots(8, 84);
    }

    public FluidStorageInfo getFluidTank() {
        return fluidTankSlot.get();
    }
}
