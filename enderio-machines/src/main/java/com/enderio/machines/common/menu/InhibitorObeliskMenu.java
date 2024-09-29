package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.InhibitorObeliskBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class InhibitorObeliskMenu extends PoweredMachineMenu<InhibitorObeliskBlockEntity> {

    public InhibitorObeliskMenu(int pContainerId, @Nullable InhibitorObeliskBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.INHIBITOR_OBELISK.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null && blockEntity.requiresCapacitor()) {
            addSlot(new MachineSlot(getMachineInventory(), blockEntity.getCapacitorSlot(), 12, 60));
        }

        addPlayerInventorySlots(8, 84);
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

    public static InhibitorObeliskMenu factory(int pContainerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof InhibitorObeliskBlockEntity castBlockEntity) {
            return new InhibitorObeliskMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new InhibitorObeliskMenu(pContainerId, null, inventory);
    }
}
