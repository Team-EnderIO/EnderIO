package com.enderio.machines.common.blocks.alloy;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineMenu;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class PrimitiveAlloySmelterMenu extends MachineMenu<PrimitiveAlloySmelterBlockEntity> {
    public static int INPUTS_INDEX = 0;
    public static int INPUT_COUNT = 3;
    public static int LAST_INDEX = 4;

    private final FloatSyncSlot craftingProgressSlot;
    private final FloatSyncSlot burnProgressSlot;

    public PrimitiveAlloySmelterMenu(int pContainerId, Inventory inventory,
            PrimitiveAlloySmelterBlockEntity blockEntity) {
        super(MachineMenus.PRIMITIVE_ALLOY_SMELTER.get(), pContainerId, inventory, blockEntity);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
        burnProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getBurnProgress));
    }

    public PrimitiveAlloySmelterMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.PRIMITIVE_ALLOY_SMELTER.get(), MachineBlockEntities.PRIMITIVE_ALLOY_SMELTER.get(),
                containerId, playerInventory, buf);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
        burnProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
    }

    private void addSlots() {
        addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.FUEL, 40, 53));
        addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.INPUTS.get(0), 20, 17));
        addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.INPUTS.get(1), 40, 17));
        addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.INPUTS.get(2), 60, 17));
        addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.OUTPUT, 116, 35));

        addPlayerInventorySlots(8, 84);
    }

    public float getBurnProgress() {
        return burnProgressSlot.get();
    }

    public float getCraftingProgress() {
        return craftingProgressSlot.get();
    }
}
