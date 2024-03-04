package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.SoulEngineBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class SoulEngineMenu extends MachineMenu<SoulEngineBlockEntity>{

    public SoulEngineMenu(@Nullable SoulEngineBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.SOUL_ENGINE.get(), pContainerId);
        if (blockEntity != null) {
            // Capacitor slot
            if (blockEntity.requiresCapacitor()) {
                addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));
            }
        }
        addInventorySlots(8, 84);
    }

    public static SoulEngineMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof SoulEngineBlockEntity castBlockEntity) {
            return new SoulEngineMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new SoulEngineMenu(null, inventory, pContainerId);
    }
}
