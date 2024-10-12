package com.enderio.machines.common.blockentity.base;

import com.enderio.base.api.capacitor.CapacitorScalable;
import com.enderio.base.api.io.IOMode;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.attachment.ActionRange;
import com.enderio.machines.common.attachment.RangedActor;
import com.enderio.machines.common.blockentity.MachineState;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public abstract class ObeliskBlockEntity extends PoweredMachineBlockEntity implements RangedActor {

    private @Nullable AABB aabb;
    private final NetworkDataSlot<ActionRange> actionRangeDataSlot;
    public static SingleSlotAccess FILTER = new SingleSlotAccess();

    public ObeliskBlockEntity(EnergyIOMode energyIOMode, CapacitorScalable capacity, CapacitorScalable usageRate, BlockEntityType<?> type,
        BlockPos worldPosition, BlockState blockState) {
        super(energyIOMode, capacity, usageRate, type, worldPosition, blockState);

        actionRangeDataSlot = addDataSlot(ActionRange.DATA_SLOT_TYPE.create(this::getActionRange, this::internalSetActionRange));

        // TODO: rubbish way of having a default. use an interface instead?
        if (!hasData(MachineAttachments.ACTION_RANGE)) {
            setData(MachineAttachments.ACTION_RANGE, new ActionRange(5, false));
        }
    }

    @Override
    protected boolean isActive() {
        return canAct();
    }

    @Override
    public ActionRange getActionRange() {
        return getData(MachineAttachments.ACTION_RANGE);
    }

    @Override
    public void setActionRange(ActionRange actionRange) {
        if (level != null && level.isClientSide) {
            clientUpdateSlot(actionRangeDataSlot, actionRange);
        } else {
            internalSetActionRange(actionRange);
        }
    }

    private void internalSetActionRange(ActionRange actionRange) {
        setData(MachineAttachments.ACTION_RANGE, actionRange);
        updateLocations();
        setChanged();
    }

    @Override
    public void serverTick() {
        updateMachineState(MachineState.ACTIVE, isActive()); //No powered model state, so it needs to be done manually
        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getBlockPos(), getColor());
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
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(IOMode.PULL);
    }

    @Override
    public boolean isIOConfigMutable() {
        return false;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        var actionRange = components.get(MachineDataComponents.ACTION_RANGE);
        if (actionRange != null) {
            setData(MachineAttachments.ACTION_RANGE, actionRange);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(MachineDataComponents.ACTION_RANGE, getData(MachineAttachments.ACTION_RANGE));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        removeData(MachineAttachments.ACTION_RANGE);
    }
}
