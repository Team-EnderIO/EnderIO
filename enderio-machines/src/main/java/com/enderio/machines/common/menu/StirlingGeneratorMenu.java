package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.StirlingGeneratorBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class StirlingGeneratorMenu extends MachineMenu<StirlingGeneratorBlockEntity> {
    public StirlingGeneratorMenu(@Nullable StirlingGeneratorBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.STIRLING_GENERATOR.get(), pContainerId);
        if (blockEntity != null) {
            // Capacitor slot
            if (blockEntity.requiresCapacitor()) {
                addSlot(new MachineSlot(blockEntity.getInventory(), 1, 12, 60));
            }

            addSlot(new MachineSlot(blockEntity.getInventory(), 0, 80, 34));
        }
        addInventorySlots(8, 84);
    }

    public static StirlingGeneratorMenu factory(@Nullable MenuType<StirlingGeneratorMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof StirlingGeneratorBlockEntity castBlockEntity)
            return new StirlingGeneratorMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new StirlingGeneratorMenu(null, inventory, pContainerId);
    }
}
