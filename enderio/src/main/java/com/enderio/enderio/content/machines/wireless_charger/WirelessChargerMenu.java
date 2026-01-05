package com.enderio.enderio.content.machines.wireless_charger;

import com.enderio.core.common.network.menu.IntSyncSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class WirelessChargerMenu extends PoweredMachineMenu<WirelessChargerBlockEntity> {

    public static final int INCREASE_BUTTON_ID = 0;
    public static final int DECREASE_BUTTON_ID = 1;
    public static final int VISIBILITY_BUTTON_ID = 2;

    private final IntSyncSlot maxRange;

    public WirelessChargerMenu(int containerId, Inventory inventory, WirelessChargerBlockEntity blockEntity) {
        super(EIOMenus.WIRELESS_CHARGER.get(), containerId, inventory, blockEntity);
        addSlots();

        maxRange = addSyncSlot(IntSyncSlot.readOnly(blockEntity::getMaxRange));
    }

    public WirelessChargerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.WIRELESS_CHARGER.get(), containerId, playerInventory, buf,
            EIOBlockEntities.WIRELESS_CHARGER.get());
        addSlots();

        maxRange = addSyncSlot(IntSyncSlot.standalone());
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addPlayerInventorySlots(8, 84);
    }

    public boolean isRangeVisible() {
        // This is synced via the block entity.
        return getBlockEntity().isRangeVisible();
    }

    public int getMaxRange() {
        return maxRange.get();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        var blockEntity = getBlockEntity();
        return switch (id) {
        case INCREASE_BUTTON_ID -> {
            blockEntity.increaseRange();
            yield true;
        }
        case DECREASE_BUTTON_ID -> {
            blockEntity.decreaseRange();
            yield true;
        }
        case VISIBILITY_BUTTON_ID -> {
            blockEntity.setRangeVisible(!isRangeVisible());
            yield true;
        }
        default -> false;
        };
    }

}
