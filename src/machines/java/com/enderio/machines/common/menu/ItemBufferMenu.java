package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.ItemBufferBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class ItemBufferMenu extends MachineMenu<ItemBufferBlockEntity> {

    public ItemBufferMenu(ItemBufferBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.ITEM_BUFFER.get(), pContainerId);

        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 3; ++k) {
                this.addSlot(new MachineSlot(blockEntity.getInventory(), k + j * 3, 62 + k * 18, 17 + j * 18));
            }
        }

        addInventorySlots(8,84);
    }

    public static ItemBufferMenu factory(@Nullable MenuType<ItemBufferMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());

        if (entity instanceof ItemBufferBlockEntity castBlockEntity)
            return new ItemBufferMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");

        return new ItemBufferMenu(null, inventory, pContainerId);
    }
}
