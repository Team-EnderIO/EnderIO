package com.enderio.machines.common.blocks.farming_station;

import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class FarmingStationMenu extends PoweredMachineMenu<FarmingStationBlockEntity> {

    public static final int VISIBILITY_BUTTON_ID = 0;

    public FarmingStationMenu(int pContainerId, Inventory inventory, FarmingStationBlockEntity blockEntity) {
        super(MachineMenus.FARMING_STATION.get(), pContainerId, inventory, blockEntity);
        addSlots();

    }

    public FarmingStationMenu(int pContainerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.FARMING_STATION.get(), pContainerId, inventory, buf,
                MachineBlockEntities.FARMING_STATION.get());
        addSlots();

    }

    private void addSlots() {
        addCapacitorSlot(12, 63);

        var inventory = getMachineInventory();
        // Tool inputs TODO: Shadow slots to show compatible tools?
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.AXE, 44, 19));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.HOE, 44 + 18, 19));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.SHEAR, 44 + 18 * 2, 19));

        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.NE, 53, 44));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.SE, 53 + 18, 44));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.SW, 53, 44 + 18));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.NW, 53 + 18, 44 + 18));

        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.BONEMEAL.get(0), 116, 19));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.BONEMEAL.get(1), 116 + 18, 19));

        for (int i = 0; i < 6; i++) {
            addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.OUTPUT.get(i), 107 + 18 * (i % 3),
                    i < 3 ? 44 : 44 + 18));
        }

        addPlayerInventorySlots(8, 87);
    }

    public boolean isRangeVisible() {
        return getBlockEntity().isRangeVisible();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        var blockEntity = getBlockEntity();
        if (id == VISIBILITY_BUTTON_ID) {
            blockEntity.setRangeVisible(!isRangeVisible());
            return true;
        }
        return false;
    }

}
