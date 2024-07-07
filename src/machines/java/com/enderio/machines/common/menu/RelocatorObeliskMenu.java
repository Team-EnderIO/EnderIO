package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.AversionObeliskBlockEntity;
import com.enderio.machines.common.blockentity.RelocatorObeliskBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class RelocatorObeliskMenu extends MachineMenu<RelocatorObeliskBlockEntity> {

    public RelocatorObeliskMenu(@Nullable RelocatorObeliskBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.RELOCATOR_OBELISK.get(), pContainerId);

        if (blockEntity != null && blockEntity.requiresCapacitor()) {
            addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));
        }

        addSlot(new MachineSlot(blockEntity.getInventory(), RelocatorObeliskBlockEntity.FILTER, 40, 60));

        addInventorySlots(8, 84);
    }

    public static RelocatorObeliskMenu factory(int pContainerId, Inventory inventory,
                                               FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof RelocatorObeliskBlockEntity castBlockEntity) {
            return new RelocatorObeliskMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new RelocatorObeliskMenu(null, inventory, pContainerId);
    }
}
