package com.enderio.machines.common.machine.soul_engine;

import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.machine.base.fluid.FluidStorageInfo;
import com.enderio.machines.common.machine.base.fluid.FluidStorageSyncSlot;
import com.enderio.machines.common.machine.base.menu.MachineSlot;
import com.enderio.machines.common.machine.base.menu.NewPoweredMachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class SoulEngineMenu extends NewPoweredMachineMenu<SoulEngineBlockEntity> {

    private final FluidStorageSyncSlot fluidTankSlot;

    public SoulEngineMenu(int containerId, Inventory inventory, SoulEngineBlockEntity blockEntity) {
        super(MachineMenus.SOUL_ENGINE.get(), containerId, inventory, blockEntity);
        addSlots();

        fluidTankSlot = addSyncSlot(FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidTank())));
    }

    public SoulEngineMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.SOUL_ENGINE.get(), MachineBlockEntities.SOUL_ENGINE.get(), containerId, playerInventory, buf);
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
