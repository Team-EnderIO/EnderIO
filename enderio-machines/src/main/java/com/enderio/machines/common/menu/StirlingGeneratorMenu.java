package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.StirlingGeneratorBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class StirlingGeneratorMenu extends PoweredMachineMenu<StirlingGeneratorBlockEntity> {
    public StirlingGeneratorMenu(int pContainerId, @Nullable StirlingGeneratorBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.STIRLING_GENERATOR.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            // Capacitor slot
            if (blockEntity.requiresCapacitor()) {
                addSlot(new MachineSlot(getMachineInventory(), blockEntity.getCapacitorSlot(), 12, 60));
            }

            addSlot(new MachineSlot(getMachineInventory(), StirlingGeneratorBlockEntity.FUEL, 80, 34));
        }

        addPlayerInventorySlots(8, 84);
    }

    public float getBurnProgress() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getBurnProgress();
    }

    public static StirlingGeneratorMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof StirlingGeneratorBlockEntity castBlockEntity) {
            return new StirlingGeneratorMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new StirlingGeneratorMenu(pContainerId, null, inventory);
    }
}
