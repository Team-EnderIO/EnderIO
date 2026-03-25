package com.enderio.enderio.content.machines.slicer;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class SlicerMenu extends PoweredMachineMenu<SlicerBlockEntity> {
    public static final int INPUTS_INDEX = 3;
    public static final int INPUT_COUNT = 6;
    public static final int LAST_INDEX = 9;

    private final FloatSyncSlot craftingProgressSlot;

    public SlicerMenu(int containerId, Inventory inventory, SlicerBlockEntity blockEntity) {
        super(EIOMenus.SLICE_N_SPLICE.get(), containerId, inventory, blockEntity);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
    }

    public SlicerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.SLICE_N_SPLICE.get(), containerId, playerInventory, buf,
            EIOBlockEntities.SLICE_AND_SPLICE.get());
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
    }

    private void addSlots() {
        addCapacitorSlot(8, 89);

        // Tool inputs TODO: Shadow slots to show compatible tools?
        addSlot(new MachineSlot(getMachineInventory(), SlicerBlockEntity.AXE, 48, 28));
        addSlot(new MachineSlot(getMachineInventory(), SlicerBlockEntity.SHEARS, 66, 28));

        for (int i = 0; i < 6; i++) {
            addSlot(new MachineSlot(getMachineInventory(), SlicerBlockEntity.INPUTS.slot(i), 38 + 18 * (i % 3),
                    i < 3 ? 52 : 70));
        }
        addSlot(new MachineSlot(getMachineInventory(), SlicerBlockEntity.OUTPUT, 128, 61));

        addPlayerInventorySlots(8, 126);
    }

    public float getCraftingProgress() {
        return craftingProgressSlot.get();
    }
}
