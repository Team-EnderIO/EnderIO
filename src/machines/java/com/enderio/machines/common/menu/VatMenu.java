package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.VatBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class VatMenu extends MachineMenu<VatBlockEntity> {
    public VatMenu(@Nullable VatBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.VAT.get(), pContainerId);
        addInventorySlots(8, 84);
    }

    public static VatMenu factory(@Nullable MenuType<VatMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof VatBlockEntity castBlockEntity) {
            return new VatMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new VatMenu(null, inventory, pContainerId);
    }
}
