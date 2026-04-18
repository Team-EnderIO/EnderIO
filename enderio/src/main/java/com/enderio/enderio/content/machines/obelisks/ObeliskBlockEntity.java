package com.enderio.enderio.content.machines.obelisks;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorScalable;
import com.enderio.enderio.api.filter.SoulFilter;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.foundation.attachment.RangedActor;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.IOConfig;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.state.MachineStateType;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;

public abstract class ObeliskBlockEntity<T extends ObeliskBlockEntity<T>> extends PoweredMachineBlockEntity
        implements RangedActor {

    private @Nullable AABB aabb;
    public static final SingleSlotAccess FILTER = new SingleSlotAccess();

    private static final ActionRange DEFAULT_RANGE = new ActionRange(0, false);

    private ActionRange actionRange = DEFAULT_RANGE;

    public ObeliskBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState,
                              boolean isIoConfigMutable, CapacitorSupport capacitorSupport, EnergyIOMode energyIOMode,
                              CapacitorScalable scalableEnergyCapacity, CapacitorScalable scalableMaxEnergyUse) {
        super(type, worldPosition, blockState, isIoConfigMutable, capacitorSupport, energyIOMode,
                scalableEnergyCapacity, scalableMaxEnergyUse);
    }

    @Nullable
    protected abstract ObeliskAreaManager<T> getAreaManager(ServerLevel level);

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        if (level instanceof ServerLevel serverLevel) {
            var manager = getAreaManager(serverLevel);
            if (manager != null) {
                // noinspection unchecked
                manager.register((T) this);
            }
        }
        updateFilterState();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        var manager = getAreaManager((ServerLevel) level);
        if (manager != null) {
            // noinspection unchecked
            manager.unregister((T) this);
        }
    }

    protected void updateLocations() {
        aabb = new AABB(getBlockPos()).inflate(getRange());

        if (level instanceof ServerLevel serverLevel) {
            var manager = getAreaManager(serverLevel);
            if (manager != null) {
                // noinspection unchecked
                manager.update((T) this);
            }
        }
    }

    @Override
    public boolean isActive() {
        return canAct() && getEnergyStorage().getAmountAsInt() >= getPerTickEnergyCost()
                && (!requiresFilter() || getSoulFilter() != null);
    }

    @Nullable
    protected SoulFilter getSoulFilter() {
        return FILTER.getItemStack(this).getCapability(EnderIOCapabilities.SOUL_FILTER);
    }

    @Override
    public ActionRange getActionRange() {
        return actionRange;
    }

    /**
     * Internal method that updates the action range without triggering setChanged() or block updates.
     * This is safe to call during chunk loading (e.g. from setLevel/updateCapacitorData)
     * where calling setChanged() would cause a deadlock by trying to load neighboring chunks.
     */
    private void setActionRangeInternal(ActionRange actionRange) {
        this.actionRange = actionRange.clamp(0, getMaxRange());
        updateLocations();
    }

    @Override
    @UseOnly(LogicalSide.SERVER)
    public void setActionRange(ActionRange actionRange) {
        setActionRangeInternal(actionRange);
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    protected void updateFilterState() {
        updateMachineState(new MachineState(MachineStateType.ERROR, MachinesLang.OBELISK_NO_SOUL_FILTER),
                requiresFilter() && getSoulFilter() == null);
    }

    @Override
    protected void updateCapacitorData() {
        if (level == null || level.isClientSide()) {
            super.updateCapacitorData();
            return;
        }

        int oldMaxRange = getMaxRange();
        super.updateCapacitorData();
        int newMaxRange = getMaxRange();
        if (newMaxRange != oldMaxRange) {
            int newRange = getRange();
            if (newRange <= 0 || getRange() == oldMaxRange) {
                // If the range was 0 (not cap) or was already at max auto increase to the new
                // max
                newRange = newMaxRange;
            } else {
                // Either reduced the max or had a custom range so just ensure it's in bounds
                newRange = Math.min(getRange(), newMaxRange);
            }
            // Use internal method to avoid setChanged() which would cause a deadlock
            // during chunk loading by trying to load neighboring chunks via
            // Level.updateNeighbourForOutputSignal().
            setActionRangeInternal(new ActionRange(newRange, getActionRange().isVisible()));
        }
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        updateFilterState();
    }

    @Override
    public int getMaxRange() {
        return 32;
    }

    @Override
    public void serverTick() {
        updateMachineState(MachineState.ACTIVE, isActive()); // No powered model state, so it needs to be done manually
        super.serverTick();
        if (isActive()) {
            int amount = getPerTickEnergyCost();
            getEnergyStorage().consume(amount, null);
        }
    }

    public int getPerTickEnergyCost() {
        return Mth.ceil(getMaxEnergyUse() * ((float) getRange() / getMaxRange()));
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

    public boolean requiresFilter() {
        return true;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLocations();
    }

    @Override
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(IOMode.PULL);
    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);
        if (!actionRange.equals(DEFAULT_RANGE)) {
            output.store(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC, this.actionRange);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        var range = input.read(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC);
        range.ifPresent(actionRange -> this.actionRange = actionRange);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        var actionRange = components.get(EIODataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.actionRange = actionRange;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(EIODataComponents.ACTION_RANGE, actionRange);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.ACTION_RANGE);
    }
}
