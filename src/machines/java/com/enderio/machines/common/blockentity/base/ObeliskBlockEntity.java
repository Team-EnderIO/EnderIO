package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.ICapacitorScalable;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.particle.RangeParticleData;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.MachineState;
import com.enderio.machines.common.io.SidedFixedIOConfig;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public abstract class ObeliskBlockEntity extends PoweredMachineBlockEntity {

    private @Nullable AABB aabb;
    public static SingleSlotAccess FILTER = new SingleSlotAccess();

    public ObeliskBlockEntity(EnergyIOMode energyIOMode, ICapacitorScalable capacity, ICapacitorScalable usageRate, BlockEntityType<?> type,
        BlockPos worldPosition, BlockState blockState) {
        super(energyIOMode, capacity, usageRate, type, worldPosition, blockState);

        rangeVisibleDataSlot = new BooleanNetworkDataSlot(this::isRangeVisible, b -> this.rangeVisible = b);
        addDataSlot(rangeVisibleDataSlot);
        range = 4;
        rangeDataSlot = new IntegerNetworkDataSlot(this::getRange, r -> this.range = r) {
            @Override
            public void updateServerCallback() {
                updateLocations();
                setChanged();
            }
        };
        addDataSlot(rangeDataSlot);
    }

    @Override
    protected boolean isActive() {
        return canAct();
    }

    @SuppressWarnings("unused")
    public void setRangedActionData(int range, boolean rangeVisible) {
        if (level != null && level.isClientSide) {
            clientUpdateSlot(rangeVisibleDataSlot, rangeVisible);
            clientUpdateSlot(rangeDataSlot, range);
        } else {
            internalSetRangedActionData(range, rangeVisible);
        }
    }

    private void internalSetRangedActionData(int actionRange, boolean actionRangeVisible) {
        range = actionRange;
        rangeVisible = actionRangeVisible;
        updateLocations();
        setChanged();
    }

    @Override
    public void serverTick() {
        updateMachineState(MachineState.ACTIVE, isActive());
        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (level instanceof ClientLevel clientLevel) {
            if (!rangeVisible) {
                return;
            }

            BlockPos pos = getBlockPos();
            if (clientLevel.isClientSide()) {
                clientLevel.addAlwaysVisibleParticle(new RangeParticleData(range, getColor()), true, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
            }
        }

        super.clientTick();
    }

    public abstract String getColor();

    public @Nullable AABB getAABB() {
        return aabb;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLocations();
    }

    protected void updateLocations() {
        aabb = new AABB(getBlockPos()).inflate(getRange());
    }

    @Override
    protected IIOConfig createIOConfig() {
        return new SidedFixedIOConfig(dir -> IOMode.PULL);
    }
}
