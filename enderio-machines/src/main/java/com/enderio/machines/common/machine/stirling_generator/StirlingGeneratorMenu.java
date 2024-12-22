package com.enderio.machines.common.machine.stirling_generator;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.machine.base.blockentity.NewPoweredMachineBlockEntity;
import com.enderio.machines.common.machine.base.menu.NewPoweredMachineMenu;
import com.enderio.machines.common.menu.MachineSlot;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class StirlingGeneratorMenu extends NewPoweredMachineMenu<StirlingGeneratorBlockEntity> {

    private FloatSyncSlot burnProgressSlot;

    public StirlingGeneratorMenu(int pContainerId, Inventory inventory, StirlingGeneratorBlockEntity blockEntity) {
        super(MachineMenus.STIRLING_GENERATOR.get(), pContainerId, inventory, blockEntity);
        addSlots();

        burnProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getBurnProgress));
    }

    public StirlingGeneratorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.STIRLING_GENERATOR.get(), MachineBlockEntities.STIRLING_GENERATOR.get(), containerId, playerInventory, buf);
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
