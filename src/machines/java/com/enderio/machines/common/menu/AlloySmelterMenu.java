package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.blockentity.AlloySmelterMode;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class AlloySmelterMenu extends PoweredMachineMenu<AlloySmelterBlockEntity> {
    public static int INPUTS_INDEX = 1;
    public static int INPUT_COUNT = 3;
    public static int LAST_INDEX = 4;

    public AlloySmelterMenu(int pContainerId, @Nullable AlloySmelterBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.ALLOY_SMELTER.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            // Capacitor slot
            addSlot(new MachineSlot(getMachineInventory(), getCapacitorSlotIndex(), 7, 79))
                .setBackground(InventoryMenu.BLOCK_ATLAS, EMPTY_CAPACITOR_SLOT);

            addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.INPUTS.get(0), 55, 28));
            addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.INPUTS.get(1), 80, 18));
            addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.INPUTS.get(2), 104, 28));
            addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.OUTPUT, 80, 69));
        }

        addPlayerInventorySlots(8,115);
    }

    public float getCraftingProgress() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getCraftingProgress();
    }

    public AlloySmelterMode getMode() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getMode();
    }

    public void setMode(AlloySmelterMode mode) {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().setMode(mode);
    }

    public static AlloySmelterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof AlloySmelterBlockEntity castBlockEntity) {
            return new AlloySmelterMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new AlloySmelterMenu(pContainerId, null, inventory);
    }
}
