package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.MobGeneratorBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class MobGeneratorMenu extends MachineMenu<MobGeneratorBlockEntity>{

    public MobGeneratorMenu(@Nullable MobGeneratorBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.MOB_GENERATOR.get(), pContainerId);
        if (blockEntity != null) {
            // Capacitor slot
            if (blockEntity.requiresCapacitor()) {
                addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));
            }
        }
        addInventorySlots(8, 84);
    }

    public static MobGeneratorMenu factory(@Nullable MenuType<MobGeneratorMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof MobGeneratorBlockEntity castBlockEntity)
            return new MobGeneratorMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new MobGeneratorMenu(null, inventory, pContainerId);
    }
}
