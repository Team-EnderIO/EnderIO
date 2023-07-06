package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class CapacitorBankMenu extends MachineMenu<CapacitorBankBlockEntity> {
    public CapacitorBankMenu(@Nullable CapacitorBankBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.CAPACITOR_BANK.get(), pContainerId);
        addInventorySlots(8,84);
    }

    public static CapacitorBankMenu factory(@Nullable MenuType<CapacitorBankMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof CapacitorBankBlockEntity castBlockEntity)
            return new CapacitorBankMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new CapacitorBankMenu(null, inventory, pContainerId);
    }
}
