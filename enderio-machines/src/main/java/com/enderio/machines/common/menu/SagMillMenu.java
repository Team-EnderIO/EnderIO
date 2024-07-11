package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class SagMillMenu extends PoweredMachineMenu<SagMillBlockEntity> {
    public static int INPUTS_INDEX = 1;
    public static int INPUT_COUNT = 1;
    public static int LAST_INDEX = 6;

    public SagMillMenu(int pContainerId, @Nullable SagMillBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.SAG_MILL.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            addSlot(new MachineSlot(getMachineInventory(), blockEntity.getCapacitorSlot(), 12, 60));

            addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.INPUT, 80, 12));

            addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.OUTPUT.get(0), 49, 59));
            addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.OUTPUT.get(1), 70, 59));
            addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.OUTPUT.get(2), 91, 59));
            addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.OUTPUT.get(3), 112, 59));

            addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.GRINDING_BALL, 122, 23));
        }

        addPlayerInventorySlots(8,84);
    }

    public float getCraftingProgress() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getCraftingProgress();
    }

    public static SagMillMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof SagMillBlockEntity castBlockEntity) {
            return new SagMillMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new SagMillMenu(pContainerId, null, inventory);
    }
}
