package com.enderio.enderio.content.machines.niard;

import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import com.enderio.enderio.foundation.fluid.FluidStorageSyncSlot;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class NiardMenu extends PoweredMachineMenu<NiardBlockEntity> {

    public static final int INCREASE_BUTTON_ID = 0;
    public static final int DECREASE_BUTTON_ID = 1;
    public static final int VISIBILITY_BUTTON_ID = 2;

    private final FluidStorageSyncSlot fluidSlot;

    public NiardMenu(int containerId, Inventory inventory, NiardBlockEntity blockEntity) {
        super(EIOMenus.NIARD.get(), containerId, inventory, blockEntity);
        addSlots();

        fluidSlot = addSyncSlot(FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidTank())));
    }

    public NiardMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.NIARD.get(), containerId, playerInventory, buf, EIOBlockEntities.NIARD.get());
        addSlots();

        fluidSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
    }

    private void addSlots() {
        addSlot(new MachineSlot(getMachineInventory(), NiardBlockEntity.FLUID_FILL_INPUT, 44, 21));
        addSlot(new MachineSlot(getMachineInventory(), NiardBlockEntity.FLUID_FILL_OUTPUT, 44, 52));
        addCapacitorSlot(12, 60);
        addPlayerInventorySlots(8, 84);
    }

    public FluidStorageInfo getFluidTank() {
        return fluidSlot.get();
    }

    public boolean isRangeVisible() {
        // This is synced via the block entity.
        return getBlockEntity().isRangeVisible();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        // noinspection DuplicatedCode
        var blockEntity = getBlockEntity();
        switch (id) {
        case INCREASE_BUTTON_ID:
            blockEntity.increaseRange();
            return true;
        case DECREASE_BUTTON_ID:
            blockEntity.decreaseRange();
            return true;
        case VISIBILITY_BUTTON_ID:
            blockEntity.setRangeVisible(!isRangeVisible());
            return true;
        default:
            return false;
        }
    }
}
