package com.enderio.enderio.content.machines.stirling_generator;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class StirlingGeneratorMenu extends PoweredMachineMenu<StirlingGeneratorBlockEntity> {

    private final FloatSyncSlot burnProgressSlot;

    public StirlingGeneratorMenu(int containerId, Inventory inventory, StirlingGeneratorBlockEntity blockEntity) {
        super(EIOMenus.STIRLING_GENERATOR.get(), containerId, inventory, blockEntity);
        addSlots();

        burnProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getBurnProgress));
    }

    public StirlingGeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(EIOMenus.STIRLING_GENERATOR.get(), containerId, playerInventory, buf,
            EIOBlockEntities.STIRLING_GENERATOR.get());
        addSlots();

        burnProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);

        addSlot(new MachineSlot(getMachineInventory(), StirlingGeneratorBlockEntity.FUEL, 80, 34));

        addPlayerInventorySlots(8, 84);
    }

    public float getBurnProgress() {
        return burnProgressSlot.get();
    }
}
