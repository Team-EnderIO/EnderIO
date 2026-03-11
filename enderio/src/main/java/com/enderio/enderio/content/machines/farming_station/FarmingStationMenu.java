package com.enderio.enderio.content.machines.farming_station;

import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class FarmingStationMenu extends PoweredMachineMenu<FarmingStationBlockEntity> {

    public static final int VISIBILITY_BUTTON_ID = 0;

    public FarmingStationMenu(int containerId, Inventory inventory, FarmingStationBlockEntity blockEntity) {
        super(EIOMenus.FARMING_STATION.get(), containerId, inventory, blockEntity);
        addSlots();

    }

    public FarmingStationMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        super(EIOMenus.FARMING_STATION.get(), containerId, inventory, buf,
            EIOBlockEntities.FARMING_STATION.get());
        addSlots();

    }

    private void addSlots() {
        addCapacitorSlot(12, 63);

        var inventory = getMachineInventory();
        // Tool inputs TODO: Shadow slots to show compatible tools?
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.TOOLS.get(0), 44, 19));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.TOOLS.get(1), 44 + 18, 19));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.TOOLS.get(2), 44 + 18 * 2, 19));

        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.AREAS.get(0), 53, 44));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.AREAS.get(1), 53 + 18, 44));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.AREAS.get(2), 53, 44 + 18));
        addSlot(new MachineSlot(inventory, FarmingStationBlockEntity.AREAS.get(3), 53 + 18, 44 + 18));

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
