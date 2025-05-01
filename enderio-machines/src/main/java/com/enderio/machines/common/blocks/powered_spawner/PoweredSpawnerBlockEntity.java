package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.UseOnly;
import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.particle.RangeParticleData;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blocks.base.blockentity.PoweredMachineBlockEntity;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.blocks.base.state.MachineState;
import com.enderio.machines.common.blocks.base.state.MachineStateType;
import com.enderio.machines.common.blocks.base.task.MachineTask;
import com.enderio.machines.common.blocks.base.task.host.MachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.souldata.SpawnerSoul;
import com.enderio.machines.common.tag.MachineTags;
import com.mojang.datafixers.util.Either;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
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

public class PoweredSpawnerBlockEntity extends PoweredMachineBlockEntity implements IOwnedSpawner {

    public static final SingleSlotAccess INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_USAGE);
    public static final ResourceLocation NO_MOB = EnderIO.loc("no_mob");

    private static final PoweredSpawnerMode DEFAULT_MODE = PoweredSpawnerMode.SPAWN;
    private PoweredSpawnerMode mode = DEFAULT_MODE;

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
                return createNewTask();
            }

            @Override
            protected @Nullable MachineTask loadTask(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
                var task = switch (mode) {
                case SPAWN -> new MobSpawnTask(PoweredSpawnerBlockEntity.this);
                case CAPTURE -> new MobCaptureTask(PoweredSpawnerBlockEntity.this);
                };

                task.deserializeNBT(lookupProvider, nbt);

                return task;
            }
        };

        updateMachineState(new MachineState(MachineStateType.ERROR, this.reason.component), false);
    }

    public PoweredSpawnerMode getMode() {
        return mode;
    }

    public void setMode(PoweredSpawnerMode mode) {
        this.mode = mode;

        if (level != null && !level.isClientSide()) {
            taskHost.newTaskAvailable();
        }
    }

    @Nullable
    private PoweredSpawnerTask createNewTask() {
        // Ensure we have a valid entity type.
        if (getEntityType().isEmpty()) {
            setReason(SpawnerBlockedReason.UNKNOWN_MOB);
            return null;
        }

        var entityTypeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(getEntityType().get());
        if (entityTypeOpt.isEmpty()) {
            setReason(SpawnerBlockedReason.UNKNOWN_MOB);
            return null;
        }

        if (entityTypeOpt.get().is(MachineTags.EntityTypes.SPAWNER_BLACKLIST)) {
            setReason(SpawnerBlockedReason.DISABLED);
            return null;
        }

        // Ensure output is free in capture mode
        if (mode == PoweredSpawnerMode.CAPTURE) {
            if (!INPUT.getItemStack(this).is(EIOItems.SOUL_VIAL)) {
                setReason(SpawnerBlockedReason.INPUT_EMPTY);
                return null;
            }

            if (!OUTPUT.getItemStack(this).isEmpty()) {
                setReason(SpawnerBlockedReason.OUTPUT_FULL);
                return null;
            }
        }

        // Gather spawn data
        int energyCost = MachinesConfig.COMMON.DEFAULT_SPAWN_ENERGY_COST.get();
        MobSpawnMode spawnType = MachinesConfig.COMMON.SPAWN_TYPE.get();

        var spawnDataOpt = SpawnerSoul.SPAWNER.matches(getEntityType().get());
        if (spawnDataOpt.isPresent()) {
            var data = spawnDataOpt.get();
            energyCost = data.power();
            spawnType = data.spawnType();
        }

        return switch (mode) {
        case SPAWN -> new MobSpawnTask(this, energyCost, entityTypeOpt.get(), spawnType);
        case CAPTURE -> new MobCaptureTask(this, energyCost, entityTypeOpt.get(), spawnType);
        };
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

            // Blocked reason is powered by the task when one is running
            if (taskHost.hasTask()) {
                if (taskHost.getCurrentTask() instanceof PoweredSpawnerTask poweredSpawnerTask) {
                    setReason(poweredSpawnerTask.getBlockedReason());
                }
            }
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
        return MachineInventoryLayout.builder()
                .capacitor()
                .inputSlot((i, stack) -> {
                    var soulStorage = stack.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
                    return soulStorage != null && !soulStorage.hasSoul();
                })
                .slotAccess(INPUT)
                .outputSlot()
                .slotAccess(OUTPUT)
                .build();
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

    // TODO: I want a better way to handle this, but unsure what that could be.
    private void setReason(SpawnerBlockedReason reason) {
        if (this.reason != SpawnerBlockedReason.NONE) {
            updateMachineState(new MachineState(MachineStateType.ERROR, this.reason.component), false);
        }

        if (reason != SpawnerBlockedReason.NONE) {
            updateMachineState(new MachineState(MachineStateType.ERROR, reason.component), true);
        }

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

        if (mode != DEFAULT_MODE) {
            tag.put(MachineNBTKeys.MACHINE_MODE, mode.save(registries));
        }

        if (isRangeVisible) {
            tag.putBoolean(MachineNBTKeys.IS_RANGE_VISIBLE, isRangeVisible);
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        entityData = StoredEntityData.parseOptional(lookupProvider, pTag.getCompound(MachineNBTKeys.ENTITY_STORAGE));

        if (pTag.contains(MachineNBTKeys.MACHINE_MODE)) {
            this.mode = PoweredSpawnerMode.parse(lookupProvider,
                    Objects.requireNonNull(pTag.get(MachineNBTKeys.MACHINE_MODE)));
        }

        // TODO: Ender IO 8 - remove support for old attachment loading
        if (hasData(MachineAttachments.ACTION_RANGE)) {
            var actionRange = getData(MachineAttachments.ACTION_RANGE);
            isRangeVisible = actionRange.isVisible();
            removeData(MachineAttachments.ACTION_RANGE);
        }

        isRangeVisible = pTag.contains(MachineNBTKeys.IS_RANGE_VISIBLE)
                && pTag.getBoolean(MachineNBTKeys.IS_RANGE_VISIBLE);

        // Load task host last
        taskHost.load(lookupProvider, pTag);
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
        INPUT_EMPTY(MachineLang.TOOLTIP_INPUT_EMPTY), OUTPUT_FULL(MachineLang.TOOLTIP_OUTPUT_FULL),
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
