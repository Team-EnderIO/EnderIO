package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.AversionObeliskBlockEntity;
import com.enderio.machines.common.blockentity.InhibitorObeliskBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class AversionObeliskMenu extends MachineMenu<AversionObeliskBlockEntity> {

    public AversionObeliskMenu(@Nullable AversionObeliskBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.AVERSION_OBELISK.get(), pContainerId);

        if (blockEntity != null && blockEntity.requiresCapacitor()) {
            addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));
        }

        addSlot(new MachineSlot(blockEntity.getInventory(), AversionObeliskBlockEntity.FILTER, 40, 60));

        addInventorySlots(8, 84);
    }

    public static AversionObeliskMenu factory(int pContainerId, Inventory inventory,
                                              FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof AversionObeliskBlockEntity castBlockEntity) {
            return new AversionObeliskMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new AversionObeliskMenu(null, inventory, pContainerId);
    }
}
