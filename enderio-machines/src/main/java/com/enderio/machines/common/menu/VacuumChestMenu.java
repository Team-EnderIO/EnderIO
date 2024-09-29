package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.VacuumChestBlockEntity;
import com.enderio.machines.common.blockentity.base.VacuumMachineBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class VacuumChestMenu extends MachineMenu<VacuumChestBlockEntity> {

    public VacuumChestMenu(int pContainerId, @Nullable VacuumChestBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.VACUUM_CHEST.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            this.addSlot(new MachineSlot(getMachineInventory(), VacuumMachineBlockEntity.FILTER, 8, 86));

            for (int j = 0; j < 3; ++j) {
                for (int k = 0; k < 9; ++k) {
                    this.addSlot(new MachineSlot(getMachineInventory(), k + j * 9, 8 + k * 18, 18 + j * 18));
                }
            }
        }

        addPlayerInventorySlots(8, 124);
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

    public static VacuumChestMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof VacuumChestBlockEntity castBlockEntity) {
            return new VacuumChestMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new VacuumChestMenu(pContainerId, null, inventory);
    }
}
