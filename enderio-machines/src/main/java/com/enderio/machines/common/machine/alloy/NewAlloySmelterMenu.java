package com.enderio.machines.common.machine.alloy;

import com.enderio.core.common.network.menu.EnumSyncSlot;
import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.machines.common.blockentity.AlloySmelterMode;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.MachineSlot;
import com.enderio.machines.common.rewrite.menu.NewPoweredMachineMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.fml.LogicalSide;

public class NewAlloySmelterMenu extends NewPoweredMachineMenu<NewAlloySmelterBlockEntity> {
    public static int INPUTS_INDEX = 1;
    public static int INPUT_COUNT = 3;
    public static int LAST_INDEX = 4;

    private final FloatSyncSlot progressSlot;
    private final EnumSyncSlot<AlloySmelterMode> modeSlot;

    // Server constructor
    public NewAlloySmelterMenu(int pContainerId, Inventory inventory, NewAlloySmelterBlockEntity blockEntity) {
        super(MachineMenus.ALLOY_SMELTER.get(), pContainerId, inventory, blockEntity);
        addSlots();

        progressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
        modeSlot = addUpdatableSyncSlot(EnumSyncSlot.simple(AlloySmelterMode.class, blockEntity::getMode, blockEntity::setMode));
    }

    // Client constructor
    public NewAlloySmelterMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.ALLOY_SMELTER.get(), MachineBlockEntities.ALLOY_SMELTER.get(), containerId, playerInventory, buf);
        addSlots();

        progressSlot = addSyncSlot(FloatSyncSlot.standalone());
        modeSlot = addUpdatableSyncSlot(EnumSyncSlot.standalone(AlloySmelterMode.class));
    }

    private void addSlots() {
        // Capacitor slot
        addSlot(new MachineSlot(getMachineInventory(), getCapacitorSlotIndex(), 8, 89))
            .setBackground(InventoryMenu.BLOCK_ATLAS, EMPTY_CAPACITOR_SLOT);

        addSlot(new MachineSlot(getMachineInventory(), NewAlloySmelterBlockEntity.INPUTS.get(0), 55, 38));
        addSlot(new MachineSlot(getMachineInventory(), NewAlloySmelterBlockEntity.INPUTS.get(1), 80, 28));
        addSlot(new MachineSlot(getMachineInventory(), NewAlloySmelterBlockEntity.INPUTS.get(2), 104, 38));
        addSlot(new MachineSlot(getMachineInventory(), NewAlloySmelterBlockEntity.OUTPUT, 80, 79));

        addPlayerInventorySlots(8,126);
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
