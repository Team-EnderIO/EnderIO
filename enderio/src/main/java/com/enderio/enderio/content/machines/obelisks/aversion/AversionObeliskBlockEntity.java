package com.enderio.enderio.content.machines.obelisks.aversion;

import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.LinearScalable;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.filter.SoulFilter;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.machines.obelisks.ObeliskAreaManager;
import com.enderio.enderio.content.machines.obelisks.ObeliskBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public class AversionObeliskBlockEntity extends ObeliskBlockEntity<AversionObeliskBlockEntity> {

    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.AVERSION_CAPACITY);
    private static final LinearScalable ENERGY_USAGE = new LinearScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.AVERSION_USAGE);
    private static final LinearScalable RANGE = new LinearScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.AVERSION_RANGE);

    public AversionObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.AVERSION_OBELISK.get(), worldPosition, blockState, false, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);
    }

    @Override
    protected @Nullable ObeliskAreaManager<AversionObeliskBlockEntity> getAreaManager(ServerLevel level) {
        return AversionObeliskManager.getManager(level);
    }

    @Override
    public @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .slot(FILTER, SlotTemplates.input(), b -> b
                .filter((_, itemResource) -> itemResource.toStack().getCapability(EnderIOCapabilities.SOUL_FILTER) != null))
            .slot(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AversionObeliskMenu(containerId, playerInventory, this);
    }

    @Override
    public int getMaxRange() {
        return RANGE.scaleI(this::getCapacitorData).get();
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.AVERSION_RANGE_COLOR.get();
    }

    public boolean handleSpawnEvent(FinalizeSpawnEvent event) {
        if (!isActive() || getAABB() == null) {
            return false;
        }
        SoulFilter filter = getSoulFilter();
        if (filter == null || !filter.test(event.getEntity())) {
            return false;
        }
        if (getAABB().contains(event.getX(), event.getY(), event.getZ())) {
            event.setSpawnCancelled(true);
            return true;
        }
        return false;
    }
}
