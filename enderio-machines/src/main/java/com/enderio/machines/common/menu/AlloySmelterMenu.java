package com.enderio.machines.common.menu;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class AlloySmelterMenu extends MachineMenu<AlloySmelterBlockEntity> {
    public AlloySmelterMenu(@Nullable AlloySmelterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.ALLOY_SMELTER.get(), pContainerId);
        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getItemHandler(), 0, 54, 17));
            addSlot(new MachineSlot(blockEntity.getItemHandler(), 1, 79, 7));
            addSlot(new MachineSlot(blockEntity.getItemHandler(), 2, 103, 17));
            addSlot(new MachineSlot(blockEntity.getItemHandler(), 3, 79, 58));

            // Capacitor slot
            if (blockEntity.getTier() != MachineTier.SIMPLE) {
                addSlot(new MachineSlot(blockEntity.getItemHandler(), 4, 12, 60));
            }
        }
        addInventorySlots(8,84);
    }

    public static AlloySmelterMenu factory(@javax.annotation.Nullable MenuType<AlloySmelterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof AlloySmelterBlockEntity castBlockEntity)
            return new AlloySmelterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new AlloySmelterMenu(null, inventory, pContainerId);
    }
}
