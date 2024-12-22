package com.enderio.machines.common.machine.powered_spawner;

import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.machine.base.menu.NewPoweredMachineMenu;
import com.enderio.machines.common.menu.MachineSlot;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class PoweredSpawnerMenu extends NewPoweredMachineMenu<PoweredSpawnerBlockEntity> {

    public static final int VISIBILITY_BUTTON_ID = 0;

    public PoweredSpawnerMenu(int pContainerId, Inventory inventory, PoweredSpawnerBlockEntity blockEntity) {
        super(MachineMenus.POWERED_SPAWNER.get(), pContainerId, inventory, blockEntity);
        addSlots();
    }

    public PoweredSpawnerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.POWERED_SPAWNER.get(), MachineBlockEntities.POWERED_SPAWNER.get(), containerId, playerInventory, buf);
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
        //noinspection DuplicatedCode
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
