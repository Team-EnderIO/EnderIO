package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.TravelAnchorBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class TravelAnchorMenu extends MachineMenu<TravelAnchorBlockEntity> {
    public TravelAnchorMenu(int pContainerId, @Nullable TravelAnchorBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.TRAVEL_ANCHOR.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            addSlot(new GhostMachineSlot(getMachineInventory(), TravelAnchorBlockEntity.GHOST, 125, 10));
        }

        addPlayerInventorySlots(8, 103);
    }

    public String getName() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getName();
    }

    public void setName(String name) {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().setName(name);
    }

    public boolean isVisible() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().isVisible();
    }

    public void setVisible(boolean visible) {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().setIsVisible(visible);
    }

    public static TravelAnchorMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof TravelAnchorBlockEntity castBlockEntity) {
            return new TravelAnchorMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new TravelAnchorMenu(pContainerId, null, inventory);
    }

}
