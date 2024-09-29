package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.DrainBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class DrainMenu extends PoweredMachineMenu<DrainBlockEntity> {
    public DrainMenu(int pContainerId, @Nullable DrainBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.DRAIN.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null && blockEntity.requiresCapacitor()) {
            addSlot(new MachineSlot(getMachineInventory(), getCapacitorSlotIndex(), 12, 60));
        }

        addPlayerInventorySlots(8, 84);
    }

    public MachineFluidTank getFluidTank() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getFluidTank();
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

    public static DrainMenu factory(int pContainerId, Inventory inventory,
        FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof DrainBlockEntity castBlockEntity) {
            return new DrainMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new DrainMenu(pContainerId, null, inventory);
    }
}
