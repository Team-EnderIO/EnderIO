package com.enderio.machines.common.blockentity;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.filter.EntityFilter;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.common.blockentity.base.ObeliskBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.AversionObeliskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AversionObeliskBlockEntity extends ObeliskBlockEntity {

    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.AVERSION_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.AVERSION_USAGE);

    public AversionObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, MachineBlockEntities.AVERSION_OBELISK.get(), worldPosition, blockState);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        NeoForge.EVENT_BUS.addListener(this::spawnEvent);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        NeoForge.EVENT_BUS.unregister((Consumer<FinalizeSpawnEvent>)this::spawnEvent);
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
        return new AversionObeliskMenu(pContainerId, this, pPlayerInventory);
    }

    @Override
    public int getMaxRange() {
        return 32;
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.AVERSION_RANGE_COLOR.get();
    }

    @SubscribeEvent
    public void spawnEvent(FinalizeSpawnEvent event) {
        // TODO: Check dimension!
        if (level == null || level.isClientSide || event.getSpawnType() != MobSpawnType.NATURAL) {
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
                event.setSpawnCancelled(true);
                getEnergyStorage().consumeEnergy(cost, true);
            }
        }
    }
}
