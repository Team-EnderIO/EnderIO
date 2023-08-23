package com.enderio.machines.common.menu;

import com.enderio.machines.common.block.XPObeliskBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class XPObeliskMenu extends MachineMenu<XPObeliskBlockEntity> {
    public XPObeliskMenu(@Nullable XPObeliskBlockEntity blockEntity, Inventory inventory,  int pContainerId) {
        super(blockEntity, inventory, MachineMenus.XP_OBELISK.get(), pContainerId);
    }


    public static XPObeliskMenu factory(@Nullable MenuType<XPObeliskMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof XPObeliskBlockEntity castBlockEntity)
            return new XPObeliskMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new XPObeliskMenu(null, inventory, pContainerId);
    }
}
