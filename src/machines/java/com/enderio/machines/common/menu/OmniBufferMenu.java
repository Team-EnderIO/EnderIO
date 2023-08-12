package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.OmniBufferBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class OmniBufferMenu extends MachineMenu<OmniBufferBlockEntity> {

    public OmniBufferMenu(OmniBufferBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.OMNI_BUFFER.get(), pContainerId);

        addSlot(new MachineSlot(blockEntity.getInventory(), blockEntity.getCapacitorSlot(), 12, 60));

        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 3; ++k) {
                this.addSlot(new MachineSlot(blockEntity.getInventory(), k + j * 3, 90 + k * 18, 14 + j * 18));
            }
        }

        addInventorySlots(8,84);
    }

    public static OmniBufferMenu factory(@Nullable MenuType<OmniBufferMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());

        if (entity instanceof OmniBufferBlockEntity castBlockEntity)
            return new OmniBufferMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");

        return new OmniBufferMenu(null, inventory, pContainerId);
    }
}
