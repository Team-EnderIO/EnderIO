package com.enderio.machines.common.blocks.soul_binder;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageInfo;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class SoulBinderMenu extends PoweredMachineMenu<SoulBinderBlockEntity> {
    public static int INPUTS_INDEX = 1;
    public static int INPUT_COUNT = 2;
    public static int LAST_INDEX = 4;

    private final FloatSyncSlot craftingProgressSlot;
    private final FluidStorageSyncSlot tankSyncSlot;

    public SoulBinderMenu(int containerId, Inventory inventory, SoulBinderBlockEntity blockEntity) {
        super(MachineMenus.SOUL_BINDER.get(), containerId, inventory, blockEntity);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
        tankSyncSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidTank())));
    }

    public SoulBinderMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.SOUL_BINDER.get(), MachineBlockEntities.SOUL_BINDER.get(), containerId, playerInventory,
                buf);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
        tankSyncSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);

        addSlot(new MachineSlot(getMachineInventory(), SoulBinderBlockEntity.INPUT_SOUL, 38, 34));
        addSlot(new MachineSlot(getMachineInventory(), SoulBinderBlockEntity.INPUT_OTHER, 59, 34));
        addSlot(new MachineSlot(getMachineInventory(), SoulBinderBlockEntity.OUTPUT.get(0), 112, 34));
        addSlot(new MachineSlot(getMachineInventory(), SoulBinderBlockEntity.OUTPUT.get(1), 134, 34));

        addPlayerInventorySlots(8, 84);
    }

    public float getCraftingProgress() {
        return craftingProgressSlot.get();
    }

    public FluidStorageInfo getFluidTank() {
        return tankSyncSlot.get();
    }

    public int getExperience() {
        // TODO: This should be done in the menu probably?
        // We can sync the recipe info over a sync slot if we must.
        return getBlockEntity().getClientExp();
    }
}
