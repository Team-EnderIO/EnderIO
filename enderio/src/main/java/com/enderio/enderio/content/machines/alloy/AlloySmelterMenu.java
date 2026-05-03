package com.enderio.enderio.content.machines.alloy;

import com.enderio.core.common.network.menu.EnumSyncSlot;
import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class AlloySmelterMenu extends PoweredMachineMenu<AlloySmelterBlockEntity> {
    public static final int INPUTS_INDEX = 1;
    public static final int INPUT_COUNT = 3;
    public static final int LAST_INDEX = 4;

    private final FloatSyncSlot progressSlot;
    private final EnumSyncSlot<AlloySmelterMode> modeSlot;

    // Server constructor
    public AlloySmelterMenu(int containerId, Inventory inventory, AlloySmelterBlockEntity blockEntity) {
        super(EIOMenus.ALLOY_SMELTER.get(), containerId, inventory, blockEntity);
        addSlots();

        progressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
        modeSlot = addUpdatableSyncSlot(
                EnumSyncSlot.simple(AlloySmelterMode.class, blockEntity::getMode, blockEntity::setMode));
    }

    // Client constructor
    public AlloySmelterMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.ALLOY_SMELTER.get(), containerId, playerInventory, buf,
            EIOBlockEntities.ALLOY_SMELTER.get());
        addSlots();

        progressSlot = addSyncSlot(FloatSyncSlot.standalone());
        modeSlot = addUpdatableSyncSlot(EnumSyncSlot.standalone(AlloySmelterMode.class));
    }

    private void addSlots() {
        // Capacitor slot
        addCapacitorSlot(8, 89);

        addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.INPUTS.slot(0), 55, 38));
        addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.INPUTS.slot(1), 80, 28));
        addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.INPUTS.slot(2), 104, 38));
        addSlot(new MachineSlot(getMachineInventory(), AlloySmelterBlockEntity.OUTPUT, 80, 79));

        addPlayerInventorySlots(8, 126);
    }

    public float getCraftingProgress() {
        return progressSlot.get();
    }

    public AlloySmelterMode getMode() {
        return modeSlot.get();
    }

    public void setMode(AlloySmelterMode mode) {
        modeSlot.set(mode);
        updateSlot(modeSlot);
    }
}
