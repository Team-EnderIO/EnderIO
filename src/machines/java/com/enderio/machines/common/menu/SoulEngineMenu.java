package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.SoulEngineBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class SoulEngineMenu extends PoweredMachineMenu<SoulEngineBlockEntity> {

    public SoulEngineMenu(int pContainerId, @Nullable SoulEngineBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.SOUL_ENGINE.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            // Capacitor slot
            if (blockEntity.requiresCapacitor()) {
                addSlot(new MachineSlot(getMachineInventory(), getCapacitorSlotIndex(), 12, 60));
            }
        }

        addPlayerInventorySlots(8, 84);
    }

    public MachineFluidTank getFluidTank() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getFluidTank();
    }

    public static SoulEngineMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof SoulEngineBlockEntity castBlockEntity) {
            return new SoulEngineMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new SoulEngineMenu(pContainerId, null, inventory);
    }
}
