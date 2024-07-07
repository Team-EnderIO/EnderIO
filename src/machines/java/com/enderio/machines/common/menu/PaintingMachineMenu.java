package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.PaintingMachineBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class PaintingMachineMenu extends PoweredMachineMenu<PaintingMachineBlockEntity> {
    public PaintingMachineMenu(@Nullable PaintingMachineBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(MachineMenus.PAINTING_MACHINE.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            addSlot(new MachineSlot(getMachineInventory(), getCapacitorSlotIndex(), 12, 60));

            addSlot(new MachineSlot(getMachineInventory(), PaintingMachineBlockEntity.INPUT, 67, 34));
            addSlot(new MachineSlot(getMachineInventory(), PaintingMachineBlockEntity.PAINT, 38, 34));
            addSlot(new MachineSlot(getMachineInventory(), PaintingMachineBlockEntity.OUTPUT, 120, 34));
        }

        addPlayerInventorySlots(8,84);
    }

    public float getCraftingProgress() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getCraftingProgress();
    }

    public static PaintingMachineMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof PaintingMachineBlockEntity castBlockEntity) {
            return new PaintingMachineMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new PaintingMachineMenu(null, inventory, pContainerId);
    }
}
