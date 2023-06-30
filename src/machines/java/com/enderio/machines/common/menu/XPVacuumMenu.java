package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.XPVacuumBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class XPVacuumMenu extends MachineMenu<XPVacuumBlockEntity> {

    public XPVacuumMenu(XPVacuumBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.XP_VACUUM.get(), pContainerId);
        addInventorySlots(8, 70);
    }

    public static XPVacuumMenu factory(@Nullable MenuType<XPVacuumMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof XPVacuumBlockEntity castBlockEntity)
            return new XPVacuumMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new XPVacuumMenu(null, inventory, pContainerId);
    }
}
