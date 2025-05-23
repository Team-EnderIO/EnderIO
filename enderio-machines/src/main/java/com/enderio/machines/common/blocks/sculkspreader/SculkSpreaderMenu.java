package com.enderio.machines.common.blocks.sculkspreader;

import com.enderio.machines.common.blocks.base.fluid.FluidStorageInfo;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineMenu;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class SculkSpreaderMenu extends MachineMenu<SculkSpreaderBlockEntity> {

    private final FluidStorageSyncSlot tankSlot;

    public SculkSpreaderMenu(int containerId, Inventory playerInventory, SculkSpreaderBlockEntity blockEntity) {
        super(MachineMenus.SCULK_SPREADER.get(), containerId, playerInventory, blockEntity);
        addSlots();

        tankSlot = addSyncSlot(FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidTank())));
    }

    public SculkSpreaderMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.SCULK_SPREADER.get(), containerId, playerInventory, buf, MachineBlockEntities.SCULK_SPREADER.get());
        addSlots();

        tankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
    }

    private void addSlots() {
        addSlot(new MachineSlot(getMachineInventory(), SculkSpreaderBlockEntity.INPUT, 56, 12));
        addSlot(new MachineSlot(getMachineInventory(), SculkSpreaderBlockEntity.OUTPUT, 105, 12));

        addPlayerInventorySlots(8, 84);
    }

    public FluidStorageInfo getTank() {
        return tankSlot.get();
    }

}
