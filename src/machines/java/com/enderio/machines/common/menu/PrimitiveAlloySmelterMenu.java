package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class PrimitiveAlloySmelterMenu extends MachineMenu<AlloySmelterBlockEntity.Primitive> {
    public PrimitiveAlloySmelterMenu(@Nullable AlloySmelterBlockEntity.Primitive blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.PRIMITIVE_ALLOY_SMELTER.get(), pContainerId);
        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getInventory(), 0, 20, 17));
            addSlot(new MachineSlot(blockEntity.getInventory(), 1, 40, 17));
            addSlot(new MachineSlot(blockEntity.getInventory(), 2, 60, 17));
            addSlot(new MachineSlot(blockEntity.getInventory(), 3, 40, 53));
            addSlot(new MachineSlot(blockEntity.getInventory(), 4, 116, 35));
        }
        addInventorySlots(8,84);
    }

    public static PrimitiveAlloySmelterMenu factory(@Nullable MenuType<PrimitiveAlloySmelterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof AlloySmelterBlockEntity.Primitive castBlockEntity)
            return new PrimitiveAlloySmelterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new PrimitiveAlloySmelterMenu(null, inventory, pContainerId);
    }
}
