package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.LegacyPoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class CapacitorBankMenu extends LegacyPoweredMachineMenu<CapacitorBankBlockEntity> {
    public CapacitorBankMenu(int pContainerId, @Nullable CapacitorBankBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.CAPACITOR_BANK.get(), pContainerId, blockEntity, inventory);
        addPlayerInventorySlots(8, 84);
    }

    public static CapacitorBankMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof CapacitorBankBlockEntity castBlockEntity) {
            return new CapacitorBankMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new CapacitorBankMenu(pContainerId, null, inventory);
    }
}
