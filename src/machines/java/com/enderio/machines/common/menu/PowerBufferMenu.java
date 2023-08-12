package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.PowerBufferBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class PowerBufferMenu extends MachineMenu<PowerBufferBlockEntity> {

    public PowerBufferMenu(PowerBufferBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.POWER_BUFFER.get(), pContainerId);

        addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));

        addInventorySlots(8,84);
    }

    public static PowerBufferMenu factory(@Nullable MenuType<PowerBufferMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());

        if (entity instanceof PowerBufferBlockEntity castBlockEntity)
            return new PowerBufferMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");

        return new PowerBufferMenu(null, inventory, pContainerId);
    }
}
