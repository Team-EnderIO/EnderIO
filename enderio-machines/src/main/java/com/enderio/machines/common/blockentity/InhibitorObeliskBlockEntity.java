package com.enderio.machines.common.blockentity;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.ObeliskBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.InhibitorObeliskMenu;
import com.enderio.machines.common.obelisk.InhibitorObeliskManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.Nullable;

public class InhibitorObeliskBlockEntity extends ObeliskBlockEntity {

    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.INHIBITOR_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.INHIBITOR_USAGE);

    public InhibitorObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, MachineBlockEntities.INHIBITOR_OBELISK.get(), worldPosition, blockState);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        if (level instanceof ServerLevel serverLevel) {
            InhibitorObeliskManager.getManager(serverLevel).register(this);
        }
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            InhibitorObeliskManager.getManager(serverLevel).unregister(this);
        }

        super.setRemoved();
    }

    @Override
    protected void updateLocations() {
        super.updateLocations();

        // Update range in obelisk manager
        if (level instanceof ServerLevel serverLevel) {
            InhibitorObeliskManager.getManager(serverLevel).update(this);
        }
    }

    @Override
    public @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
            .capacitor()
            .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new InhibitorObeliskMenu(pContainerId, this, pPlayerInventory);
    }

    @Override
    public int getMaxRange() {
        return 32;
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.INHIBITOR_RANGE_COLOR.get();
    }

    public boolean handleTeleportEvent(EntityTeleportEvent event) {
        if (isActive() && (getAABB().contains(event.getTargetX(), event.getTargetY(), event.getTargetZ())
            || getAABB().contains(event.getPrevX(), event.getPrevY(), event.getPrevZ()))) {
            int cost = ENERGY_USAGE.base().get(); //TODO scale on entity and range? The issue is that it needs the energy "now" and can't wait for it like other machines
            int energy = getEnergyStorage().consumeEnergy(cost, false);
            if (energy == cost) {
                event.setCanceled(true);
                return true;
            }
        }

        return false;
    }
}
