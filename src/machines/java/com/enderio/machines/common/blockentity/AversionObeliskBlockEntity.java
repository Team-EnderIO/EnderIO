package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.filter.EntityFilter;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.common.blockentity.base.ObeliskBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.AversionObeliskMenu;
import com.enderio.machines.common.obelisk.AversionObeliskManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.jetbrains.annotations.Nullable;

public class AversionObeliskBlockEntity extends ObeliskBlockEntity {
    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.AVERSION_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.AVERSION_USAGE);

    public AversionObeliskBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, type, worldPosition, blockState);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        if (level instanceof ServerLevel serverLevel) {
            AversionObeliskManager.getManager(serverLevel).ifPresent(obeliskManager -> {
                obeliskManager.register(this);
            });
        }
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            AversionObeliskManager.getManager(serverLevel).ifPresent(obeliskManager -> {
                obeliskManager.unregister(this);
            });
        }

        super.setRemoved();
    }

    @Override
    protected void updateLocations() {
        super.updateLocations();

        // Update range in obelisk manager
        if (level instanceof ServerLevel serverLevel) {
            AversionObeliskManager.getManager(serverLevel).ifPresent(obeliskManager -> {
                obeliskManager.update(this);
            });
        }
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot((integer, itemStack) -> itemStack.getCapability(EIOCapabilities.FILTER).orElse(null) instanceof EntityFilter)
            .slotAccess(FILTER)
            .capacitor()
            .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AversionObeliskMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    public int getMaxRange() {
        return 32;
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.AVERSION_RANGE_COLOR.get();
    }

    public boolean handleSpawnEvent(MobSpawnEvent.FinalizeSpawn event) {
        if (!isActive()) {
            return false;
        }

        if (FILTER.getItemStack(this).getCapability(EIOCapabilities.FILTER).orElse(null) instanceof EntityFilter entityFilter) {
            if (entityFilter.test(event.getEntity())) { // This check was the exact opposite in 1.21, but it has to be a logic error since whitelisted entities SHOULD spawn.
                return false;
            }
        }

        if (isActive() && getAABB().contains(event.getX(), event.getY(), event.getZ())) {
            int cost = ENERGY_USAGE.base().get(); //TODO scale on entity and range? The issue is that it needs the energy "now" and can't wait for it like other machines
            int energy = getEnergyStorage().consumeEnergy(cost, true);
            if (energy == cost) {
                event.setSpawnCancelled(true);
                getEnergyStorage().consumeEnergy(cost, true);
                return true;
            }
        }

        return false;
    }
}
