package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.XPObeliskBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class XPObeliskMenu extends MachineMenu<XPObeliskBlockEntity> {

    final Player player;

    public XPObeliskMenu(@Nullable XPObeliskBlockEntity blockEntity, Inventory inventory,  int pContainerId, Player player) {
        super(blockEntity, inventory, MachineMenus.XP_OBELISK.get(), pContainerId);
        this.player = player;
    }

    public static XPObeliskMenu factory(@Nullable MenuType<XPObeliskMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof XPObeliskBlockEntity castBlockEntity)
            return new XPObeliskMenu(castBlockEntity, inventory, pContainerId, inventory.player);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new XPObeliskMenu(null, inventory, pContainerId, inventory.player);
    }

    public void addLevelToPlayer(int levelDiff){
        getBlockEntity().addLevelToPlayer(levelDiff, player);

    }
}
