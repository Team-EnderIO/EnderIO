package com.enderio.machines.common.blocks.obelisks.weather;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageInfo;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineMenu;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class WeatherObeliskMenu extends MachineMenu<WeatherObeliskBlockEntity> {

    public static final int INPUTS_INDEX = 0;
    public static final int INPUT_COUNT = 1 + 1; //item + fluid
    public static final int LAST_INDEX = 0;
    private final FluidStorageSyncSlot tank;
    private final FloatSyncSlot craftingProgressSlot;

    protected WeatherObeliskMenu(int containerId, Inventory playerInventory, WeatherObeliskBlockEntity blockEntity) {
        super(MachineMenus.WEATHER_OBELISK.get(), containerId, playerInventory, blockEntity);

        tank = addSyncSlot(FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getTank())));
        craftingProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));

        addSlots();
    }

    public WeatherObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.WEATHER_OBELISK.get(), containerId, playerInventory, buf,
                MachineBlockEntities.WEATHER_OBELISK.get());

        tank = addSyncSlot(FluidStorageSyncSlot.standalone());
        craftingProgressSlot = addSyncSlot(FloatSyncSlot.standalone());

        addSlots();
    }

    private void addSlots() {
        addSlot(new MachineSlot(getMachineInventory(), WeatherObeliskBlockEntity.ROCKET, 80, 11));
        addPlayerInventorySlots(8, 84);
    }

    public float getCraftingProgress() {
        return craftingProgressSlot.get();
    }

    public FluidStorageInfo getFluidTank() {
        return tank.get();
    }
}
