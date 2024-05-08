package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.TravelAnchorBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class TravelAnchorMenu extends MachineMenu<TravelAnchorBlockEntity> {
    public TravelAnchorMenu(@Nullable TravelAnchorBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.TRAVEL_ANCHOR.get(), pContainerId);
        if (blockEntity != null) {
            addSlot(new GhostMachineSlot(blockEntity.getInventory(), TravelAnchorBlockEntity.GHOST, 125, 10));
        }
        addInventorySlots(8, 103);
    }

    public static TravelAnchorMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof TravelAnchorBlockEntity castBlockEntity) {
            return new TravelAnchorMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new TravelAnchorMenu(null, inventory, pContainerId);
    }

}
