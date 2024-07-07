package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class PoweredSpawnerMenu extends PoweredMachineMenu<PoweredSpawnerBlockEntity> {

    public PoweredSpawnerMenu(int pContainerId, @Nullable PoweredSpawnerBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.POWERED_SPAWNER.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null && blockEntity.requiresCapacitor()) {
            addSlot(new MachineSlot(getMachineInventory(), blockEntity.getCapacitorSlot(), 12, 60));
        }

        addPlayerInventorySlots(8,84);
    }

    public int getRange() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getRange();
    }

    public boolean isRangeVisible() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().isRangeVisible();
    }

    public void setRangeVisible(boolean isRangeVisible) {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().setRangeVisible(isRangeVisible);
    }

    public void increaseRange() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().increaseRange();
    }

    public void decreaseRange() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().decreaseRange();
    }

    public static PoweredSpawnerMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof PoweredSpawnerBlockEntity castBlockEntity) {
            return new PoweredSpawnerMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new PoweredSpawnerMenu(pContainerId, null, inventory);
    }
}
