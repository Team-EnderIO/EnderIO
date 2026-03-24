package com.enderio.enderio.content.machines.powered_spawner;

import com.enderio.core.common.network.menu.EnumSyncSlot;
import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class PoweredSpawnerMenu extends PoweredMachineMenu<PoweredSpawnerBlockEntity> {

    public static final int VISIBILITY_BUTTON_ID = 0;

    private final FloatSyncSlot spawnProgressSlot;
    private final EnumSyncSlot<PoweredSpawnerMode> modeSlot;

    public PoweredSpawnerMenu(int containerId, Inventory inventory, PoweredSpawnerBlockEntity blockEntity) {
        super(EIOMenus.POWERED_SPAWNER.get(), containerId, inventory, blockEntity);
        addSlots();

        spawnProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getSpawnProgress));
        modeSlot = addUpdatableSyncSlot(
                EnumSyncSlot.simple(PoweredSpawnerMode.class, blockEntity::getMode, blockEntity::setMode));
    }

    public PoweredSpawnerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.POWERED_SPAWNER.get(), containerId, playerInventory, buf,
            EIOBlockEntities.POWERED_SPAWNER.get());
        addSlots();

        spawnProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
        modeSlot = addUpdatableSyncSlot(EnumSyncSlot.standalone(PoweredSpawnerMode.class));
    }

    private void addSlots() {
        addCapacitorSlot(8, 67);

        addSlot(new SpawnerSlot(getMachineInventory(), PoweredSpawnerBlockEntity.INPUT, 54, 42));
        addSlot(new SpawnerSlot(getMachineInventory(), PoweredSpawnerBlockEntity.OUTPUT, 105, 42));

        addPlayerInventorySlots(8, 105);
    }

    public boolean isRangeVisible() {
        // This is synced via the block entity.
        return getBlockEntity().isRangeVisible();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == VISIBILITY_BUTTON_ID) {
            var blockEntity = getBlockEntity();
            blockEntity.setIsRangeVisible(!isRangeVisible());
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    public float getSpawnProgress() {
        return spawnProgressSlot.get();
    }

    public PoweredSpawnerMode getMode() {
        return modeSlot.get();
    }

    public void setMode(PoweredSpawnerMode mode) {
        modeSlot.set(mode);
        updateSlot(modeSlot);
    }

    private class SpawnerSlot extends MachineSlot {
        public SpawnerSlot(ItemStorage itemHandler, ResourceSlotId<ItemResource> access, int xPosition, int yPosition) {
            super(itemHandler, access, xPosition, yPosition);
        }

        @Override
        public boolean isActive() {
            return getMode() == PoweredSpawnerMode.CAPTURE;
        }

        @Override
        public boolean isHighlightable() {
            return getMode() == PoweredSpawnerMode.CAPTURE;
        }
    }
}
