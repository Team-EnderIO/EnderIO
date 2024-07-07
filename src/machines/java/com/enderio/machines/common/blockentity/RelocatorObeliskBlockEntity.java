package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.filter.EntityFilter;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.common.blockentity.base.ObeliskBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.RelocatorObeliskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import org.jetbrains.annotations.Nullable;

public class RelocatorObeliskBlockEntity extends ObeliskBlockEntity {

    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.RELOCATOR_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.RELOCATOR_USAGE);

    public RelocatorObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, MachineBlockEntities.RELOCATOR_OBELISK.get(), worldPosition, blockState);

        NeoForge.EVENT_BUS.addListener(this::spawnEvent);
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot((integer, itemStack) -> itemStack.getCapability(EIOCapabilities.Filter.ITEM) instanceof EntityFilter)
            .slotAccess(FILTER)
            .capacitor()
            .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new RelocatorObeliskMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    public int getMaxRange() {
        return 255;
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.RELOCATOR_RANGE_COLOR.get();
    }

    @SubscribeEvent
    public void spawnEvent(FinalizeSpawnEvent event) {
        if (level.isClientSide || event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }
        if (FILTER.getItemStack(this).getCapability(EIOCapabilities.Filter.ITEM) instanceof EntityFilter entityFilter) {
            if (!entityFilter.test(event.getEntity())) {
                return;
            }
        }
        if (isActive() && getAABB().contains(event.getX(), event.getY(), event.getZ())) {
            int cost = ENERGY_USAGE.base().get(); //TODO scale on entity and range? The issue is that it needs the energy "now" and can't wait for it like other machines
            int energy = getEnergyStorage().consumeEnergy(cost, true);
            if (energy == cost) {
                RandomSource randomsource = level.getRandom(); //TODO proper checks for valid spawn?
                double x = getBlockPos().getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * 5 + 0.5D;
                double y = getBlockPos().getY() + randomsource.nextInt(3) - 1;
                double z = getBlockPos().getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * 5 + 0.5D;
                EntityTeleportEvent telEvent = new EntityTeleportEvent(event.getEntity(), x, y, z);
                if (!NeoForge.EVENT_BUS.post(telEvent).isCanceled()) {
                    event.getEntity().teleportTo(x, y, z);
                    getEnergyStorage().consumeEnergy(cost, false);
                }
            }
        }
    }
}
