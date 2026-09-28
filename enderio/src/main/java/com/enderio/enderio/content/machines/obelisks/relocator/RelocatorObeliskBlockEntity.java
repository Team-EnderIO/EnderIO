package com.enderio.enderio.content.machines.obelisks.relocator;

import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.scaling.LinearIntScalable;
import com.enderio.enderio.api.capacitor.scaling.QuadraticIntScalable;
import com.enderio.enderio.api.filter.SoulFilter;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.machines.obelisks.ObeliskAreaManager;
import com.enderio.enderio.content.machines.obelisks.ObeliskBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public class RelocatorObeliskBlockEntity extends ObeliskBlockEntity<RelocatorObeliskBlockEntity> {

    private static final QuadraticIntScalable ENERGY_CAPACITY = new QuadraticIntScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.RELOCATOR_CAPACITY);
    private static final LinearIntScalable ENERGY_USAGE = new LinearIntScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.RELOCATOR_USAGE);
    private static final LinearIntScalable RANGE = new LinearIntScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.RELOCATOR_RANGE);

    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    public RelocatorObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.RELOCATOR_OBELISK.get(), worldPosition, blockState, false, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);
    }

    @Override
    protected @Nullable ObeliskAreaManager<RelocatorObeliskBlockEntity> getAreaManager(ServerLevel level) {
        return RelocatorObeliskManager.getManager(level);
    }

    @Override
    public @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(FILTER, SlotTemplates.input(64), b -> b
                .filter((_, itemResource) -> itemResource.toStack().getCapability(EnderIOCapabilities.SOUL_FILTER) != null))
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RelocatorObeliskMenu(containerId, playerInventory, this);
    }

    @Override
    public int getMaxRange() {
        return RANGE.scaled(this::getCapacitorData).get();
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.RELOCATOR_RANGE_COLOR.get();
    }

    public boolean handleSpawnEvent(FinalizeSpawnEvent event) {
        if (!isActive()) {
            return false;
        }
        SoulFilter filter = getSoulFilter();
        if (filter == null || !filter.test(event.getEntity())) {
            return false;
        }
        if (getAABB() != null && getAABB().contains(event.getX(), event.getY(), event.getZ())) {
            RandomSource randomsource = level.getRandom(); // TODO proper checks for valid spawn?
            double x = getBlockPos().getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * 5 + 0.5D;
            double y = getBlockPos().getY() + randomsource.nextInt(3) - 1;
            double z = getBlockPos().getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * 5 + 0.5D;
            EntityTeleportEvent telEvent = new EntityTeleportEvent(event.getEntity(), event.getLevel().getLevel(), x, y, z);
            if (!NeoForge.EVENT_BUS.post(telEvent).isCanceled()) {
                event.getEntity().teleportTo(x, y, z);
                return true;
            }
        }
        return false;
    }
}
