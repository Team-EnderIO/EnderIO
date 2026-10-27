package com.enderio.enderio.content.machines.killer_joe;

import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import com.enderio.enderio.foundation.fluid.FluidStorageSyncSlot;
import com.enderio.enderio.foundation.menu.MachineMenu;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class KillerJoeMenu extends MachineMenu<KillerJoeBlockEntity> {

    public static final int INCREASE_BUTTON_ID = 0;
    public static final int DECREASE_BUTTON_ID = 1;
    public static final int VISIBILITY_BUTTON_ID = 2;

    private final FluidStorageSyncSlot fluidTankSlot;

    // Server constructor
    public KillerJoeMenu(int containerId, Inventory inventory, KillerJoeBlockEntity blockEntity) {
        super(EIOMenus.KILLER_JOE.get(), containerId, inventory, blockEntity);
        addSlots();

        // Sync fluid tank
        fluidTankSlot = addSyncSlot(
            FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getFluidStorage(), KillerJoeBlockEntity.TANK)));
    }

    // Client constructor (network)
    public KillerJoeMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.KILLER_JOE.get(), containerId, playerInventory, buf,
            EIOBlockEntities.KILLER_JOE.get());
        addSlots();

        // Standalone slots for client
        fluidTankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
    }

    private void addSlots() {
        // Weapon slot (centered)
        addSlot(new MachineSlot(getMachineInventory(), KillerJoeBlockEntity.WEAPON_SLOT, 80, 35));

        // Player inventory
        addPlayerInventorySlots(8, 105);
    }

    public FluidStorageInfo getFluidTank() {
        return fluidTankSlot.get();
    }

    public int getRange() {
        // This is synced via the block entity.
        return getBlockEntity().getRange();
    }

    public boolean isRangeVisible() {
        // This is synced via the block entity.
        return getBlockEntity().isRangeVisible();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
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
            return super.clickMenuButton(player, id);
        }
    }
}
