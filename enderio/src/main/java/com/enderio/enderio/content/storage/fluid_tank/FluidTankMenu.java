package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import com.enderio.enderio.foundation.fluid.FluidStorageSyncSlot;
import com.enderio.enderio.foundation.menu.MachineMenu;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class FluidTankMenu extends MachineMenu<FluidTankBlockEntity> {

    private final FluidStorageSyncSlot fluidTankSlot;

    public FluidTankMenu(int containerId, Inventory inventory, FluidTankBlockEntity blockEntity) {
        super(EIOMenus.FLUID_TANK.get(), containerId, inventory, blockEntity);
        addSlots();

        fluidTankSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidTank())));
    }

    public FluidTankMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(EIOMenus.FLUID_TANK.get(), containerId, playerInventory, buf, EIOBlockEntities.FLUID_TANK.get(),
                EIOBlockEntities.PRESSURIZED_FLUID_TANK.get());
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
