package com.enderio.machines.common.blocks.sag_mill;

import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class SagMillMenu extends PoweredMachineMenu<SagMillBlockEntity> {
    public static final int INPUTS_INDEX = 1;
    public static final int INPUT_COUNT = 1;
    public static final int LAST_INDEX = 6;

    private final FloatSyncSlot craftingProgressSlot;
    private final FloatSyncSlot grindingBallDamageSlot;
    private final GrindingBallDataSyncSlot grindingBallDataSlot;

    public SagMillMenu(int pContainerId, Inventory inventory, SagMillBlockEntity blockEntity) {
        super(MachineMenus.SAG_MILL.get(), pContainerId, inventory, blockEntity);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
        grindingBallDamageSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getGrindingBallDamage));
        grindingBallDataSlot = addSyncSlot(GrindingBallDataSyncSlot.readOnly(blockEntity::getGrindingBallData));
    }

    public SagMillMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.SAG_MILL.get(), containerId, playerInventory, buf, MachineBlockEntities.SAG_MILL.get());
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
        grindingBallDamageSlot = addSyncSlot(FloatSyncSlot.standalone());
        grindingBallDataSlot = addSyncSlot(GrindingBallDataSyncSlot.standalone());
    }

    private void addSlots() {
        addCapacitorSlot(8, 89);

        addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.INPUT, 80, 28));

        addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.OUTPUT.get(0), 49, 75));
        addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.OUTPUT.get(1), 70, 75));
        addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.OUTPUT.get(2), 91, 75));
        addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.OUTPUT.get(3), 112, 75));

        addSlot(new MachineSlot(getMachineInventory(), SagMillBlockEntity.GRINDING_BALL, 122, 39));

        addPlayerInventorySlots(8, 126);
    }

    public float getCraftingProgress() {
        return craftingProgressSlot.get();
    }

    public float getGrindingBallDamage() {
        return grindingBallDamageSlot.get();
    }

    public GrindingBallData getGrindingBallData() {
        return grindingBallDataSlot.get();
    }
}
