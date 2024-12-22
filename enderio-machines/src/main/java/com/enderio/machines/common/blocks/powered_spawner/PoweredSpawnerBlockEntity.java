package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.EnderIOBase;
import com.enderio.base.api.UseOnly;
import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.particle.RangeParticleData;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blocks.base.blockentity.PoweredMachineBlockEntity;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.state.MachineState;
import com.enderio.machines.common.blocks.base.state.MachineStateType;
import com.enderio.machines.common.blocks.base.task.MachineTask;
import com.enderio.machines.common.blocks.base.task.host.MachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.lang.MachineLang;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.extensions.IOwnedSpawner;
import org.jetbrains.annotations.Nullable;

// TODO: I want to revisit the powered spawner and task
//       But there's not enough time before alpha, so just porting as-is.
public class PoweredSpawnerBlockEntity extends PoweredMachineBlockEntity implements IOwnedSpawner {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_USAGE);
    public static final ResourceLocation NO_MOB = EnderIOBase.loc("no_mob");

    // TODO: Config value?
    public static final int ACTION_RANGE = 4;

    private StoredEntityData entityData = StoredEntityData.EMPTY;
    private SpawnerBlockedReason reason = SpawnerBlockedReason.NONE;
    private final MachineTaskHost taskHost;

    private boolean isRangeVisible = false;

    public PoweredSpawnerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.POWERED_SPAWNER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);

        taskHost = new MachineTaskHost(this, this::hasEnergy) {
            @Override
            protected @Nullable MachineTask getNewTask() {
                return createTask();
            }

            @Override
            protected @Nullable MachineTask loadTask(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
                SpawnerMachineTask task = createTask();
                task.deserializeNBT(lookupProvider, nbt);
                return task;
            }
        };

        updateMachineState(new MachineState(MachineStateType.ERROR, this.reason.component), false);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PoweredSpawnerMenu(pContainerId, pPlayerInventory, this);
    }

    public int getRange() {
        return ACTION_RANGE;
    }

    public boolean isRangeVisible() {
        return isRangeVisible;
    }

    @UseOnly(LogicalSide.SERVER)
    public void setIsRangeVisible(boolean isRangeVisible) {
        this.isRangeVisible = isRangeVisible;
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            taskHost.tick();
        }
    }

    @Override
    public void clientTick() {
        if (level != null && isRangeVisible()) {
            var pos = getBlockPos();
            level.addAlwaysVisibleParticle(
                    new RangeParticleData(ACTION_RANGE, MachinesConfig.CLIENT.BLOCKS.POWERED_SPAWNER_RANGE_COLOR.get()),
                    true, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
        }

        super.clientTick();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        taskHost.onLevelReady();
    }

    @Override
    public @Nullable Either<BlockEntity, Entity> getOwner() {
        return Either.left(this);
    }

    // region Inventory

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder().capacitor().build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        taskHost.newTaskAvailable();
    }

    // endregion

    // region Task

    public float getSpawnProgress() {
        return taskHost.getProgress();
    }

    @Override
    public boolean isActive() {
        return canAct() && hasEnergy() && taskHost.hasTask();
    }

    private SpawnerMachineTask createTask() {
        return new SpawnerMachineTask(this, this.getEnergyStorage(), this.getEntityType().orElse(null));
    }

    // endregion

    public Optional<ResourceLocation> getEntityType() {
        return entityData.entityType();
    }

    public void setEntityType(ResourceLocation entityType) {
        entityData = StoredEntityData.of(entityType);
    }

    public StoredEntityData getEntityData() {
        return entityData;
    }

    public void setReason(SpawnerBlockedReason reason) {
        updateMachineState(new MachineState(MachineStateType.ERROR, this.reason.component), false);
        updateMachineState(new MachineState(MachineStateType.ERROR, reason.component), true);
        this.reason = reason;
    }

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        taskHost.save(lookupProvider, pTag);
    }

    @Override
    protected void saveAdditionalSynced(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalSynced(tag, registries);

        // Sync entity storage in case we want to render the entity or something in
        // future :)
        tag.put(MachineNBTKeys.ENTITY_STORAGE, entityData.saveOptional(registries));

        if (isRangeVisible) {
            tag.putBoolean(MachineNBTKeys.IS_RANGE_VISIBLE, isRangeVisible);
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        entityData = StoredEntityData.parseOptional(lookupProvider, pTag.getCompound(MachineNBTKeys.ENTITY_STORAGE));
        taskHost.load(lookupProvider, pTag);

        // TODO: Ender IO 8 - remove support for old attachment loading
        if (hasData(MachineAttachments.ACTION_RANGE)) {
            var actionRange = getData(MachineAttachments.ACTION_RANGE);
            isRangeVisible = actionRange.isVisible();
            removeData(MachineAttachments.ACTION_RANGE);
        } else if (pTag.contains(MachineNBTKeys.IS_RANGE_VISIBLE)) {
            isRangeVisible = pTag.getBoolean(MachineNBTKeys.IS_RANGE_VISIBLE);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);
        entityData = components.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY);

        // TODO: Ender IO 8 - remove.
        var actionRange = components.get(MachineDataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.isRangeVisible = actionRange.isVisible();
        }

        Boolean isVisible = components.get(MachineDataComponents.IS_RANGE_VISIBLE);
        if (isVisible != null) {
            this.isRangeVisible = isVisible;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        // Only if unchanged.
        if (isRangeVisible) {
            components.set(MachineDataComponents.IS_RANGE_VISIBLE, true);
        }

        if (entityData.hasEntity()) {
            components.set(EIODataComponents.STORED_ENTITY, entityData);
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(MachineNBTKeys.IS_RANGE_VISIBLE);
        tag.remove(MachineNBTKeys.ENTITY_STORAGE);
    }

    // endregion

    public enum SpawnerBlockedReason {
        TOO_MANY_MOB(MachineLang.TOO_MANY_MOB), TOO_MANY_SPAWNER(MachineLang.TOO_MANY_SPAWNER),
        UNKNOWN_MOB(MachineLang.UNKNOWN), OTHER_MOD(MachineLang.OTHER_MOD), DISABLED(MachineLang.DISABLED),
        NONE(Component.literal("NONE"));

        private final MutableComponent component;

        SpawnerBlockedReason(MutableComponent component) {
            this.component = component;
        }

        public MutableComponent getComponent() {
            return component;
        }
    }
}
