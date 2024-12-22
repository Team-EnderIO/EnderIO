package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class PoweredSpawnerMenu extends PoweredMachineMenu<PoweredSpawnerBlockEntity> {

    public static final int VISIBILITY_BUTTON_ID = 0;

    public PoweredSpawnerMenu(int pContainerId, Inventory inventory, PoweredSpawnerBlockEntity blockEntity) {
        super(MachineMenus.POWERED_SPAWNER.get(), pContainerId, inventory, blockEntity);
        addSlots();
    }

    public PoweredSpawnerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.POWERED_SPAWNER.get(), MachineBlockEntities.POWERED_SPAWNER.get(), containerId,
                playerInventory, buf);
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addPlayerInventorySlots(8, 84);
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
        case VISIBILITY_BUTTON_ID:
            blockEntity.setIsRangeVisible(!isRangeVisible());
            return true;
        default:
            return false;
        }
    }
}
