package com.enderio.enderio.content.machines.powered_spawner;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.particle.RangeParticleData;
import com.enderio.enderio.foundation.souldata.SpawnerSoul;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.state.MachineStateType;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.foundation.task.MachineTask;
import com.enderio.enderio.foundation.task.host.MachineTaskHost;
import com.enderio.enderio.init.EIOAttachments;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOItems;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.extensions.IOwnedSpawner;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PoweredSpawnerBlockEntity extends PoweredMachineBlockEntity implements IOwnedSpawner, SoulBindable {
    public static final SingleSlotAccess INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_USAGE);
    public static final ResourceLocation NO_MOB = EnderIO.rl("no_mob");

    private static final PoweredSpawnerMode DEFAULT_MODE = PoweredSpawnerMode.SPAWN;
    private PoweredSpawnerMode mode = DEFAULT_MODE;

    // TODO: Config value?
    public static final int ACTION_RANGE = 4;

    private Soul boundSoul = Soul.EMPTY;
    private SpawnerBlockedReason reason = SpawnerBlockedReason.NONE;
    private final MachineTaskHost taskHost;

    private boolean isRangeVisible = false;
    private boolean mindKiller = false;

    public PoweredSpawnerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.POWERED_SPAWNER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
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
        var entityType = getEntityType();
        if (entityType == null) {
            setReason(SpawnerBlockedReason.UNKNOWN_MOB);
            return null;
        }

        // Whitelist takes precedence over all
        // This allows easier allowing of restricted mobs than removing from tags.
        if (entityType.is(EIOTags.EntityTypes.SPAWNER_BLACKLIST) && !entityType.is(EIOTags.EntityTypes.SPAWNER_WHITELIST)) {
            setReason(SpawnerBlockedReason.DISABLED);
            return null;
        }

        // Ensure output is free in capture mode
        if (mode == PoweredSpawnerMode.CAPTURE) {
            if (!INPUT.getItemStack(this).is(EIOItems.SOUL_VIAL)) {
                setReason(SpawnerBlockedReason.INPUT_EMPTY);
                return null;
            }

            var outputSlotStack = OUTPUT.getItemStack(this);
            if (!outputSlotStack.isEmpty()) {
                var potentialSoulVial = SoulVialItem.forSoul(getSoulForCapture());
                if (!ItemStack.isSameItemSameComponents(potentialSoulVial, outputSlotStack)) {
                    setReason(SpawnerBlockedReason.OUTPUT_FULL);
                    return null;
                }
            }
        }

        // Gather spawn data
        int energyCost = MachinesConfig.COMMON.DEFAULT_SPAWN_ENERGY_COST.get();
        MobSpawnMode spawnType = MachinesConfig.COMMON.SPAWN_TYPE.get();

        var spawnDataOpt = SpawnerSoul.SPAWNER.matches(entityType);
        if (spawnDataOpt.isPresent()) {
            var data = spawnDataOpt.get();
            energyCost = data.power();
            spawnType = data.spawnType();
        }

        return switch (mode) {
        case SPAWN -> new MobSpawnTask(this, energyCost, entityType, spawnType);
        case CAPTURE -> new MobCaptureTask(this, energyCost, entityType, spawnType);
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PoweredSpawnerMenu(containerId, playerInventory, this);
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
        mindKiller = level.getBlockState(worldPosition.above()).is(EIOTags.Blocks.MIND_KILLER);
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
                    var soulHandler = stack.getCapability(EnderIOCapabilities.SOUL_HANDLER_ITEM);
                    return soulHandler != null && soulHandler.tryInsertSoul(getSoulForCapture(), true);
                })
                .slotAccess(INPUT)
                .outputSlot()
                .slotAccess(OUTPUT)
                .build();
    }

    private Soul getSoulForCapture() {
        var entityType = getEntityType();
        if (entityType == null) {
            return Soul.EMPTY;
        }

        MobSpawnMode spawnType = MachinesConfig.COMMON.SPAWN_TYPE.get();

        var spawnDataOpt = SpawnerSoul.SPAWNER.matches(entityType);
        if (spawnDataOpt.isPresent()) {
            spawnType = spawnDataOpt.get().spawnType();
        }

        return switch (spawnType) {
            case NEW -> Soul.of(entityType);
            case COPY -> getBoundSoul().copy();
        };
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

    @Nullable
    public EntityType<?> getEntityType() {
        return boundSoul.hasEntity() ? boundSoul.entityType() : null;
    }

    @Override
    public Soul getBoundSoul() {
        return boundSoul;
    }

    @Override
    public boolean canBind() {
        return true;
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        return SpawnerSoul.SPAWNER.matches(soul.entityTypeId()).isPresent();
    }

    @Override
    public void bindSoul(Soul newSoul) {
        this.boundSoul = newSoul;
        taskHost.newTaskAvailable();
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
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        taskHost.save(lookupProvider, tag);
    }

    @Override
    protected void saveAdditionalSynced(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalSynced(tag, registries);

        // Sync entity storage in case we want to render the entity or something in
        // future :)
        tag.put(MachineNBTKeys.ENTITY_STORAGE, boundSoul.saveOptional(registries));

        if (mode != DEFAULT_MODE) {
            tag.put(MachineNBTKeys.MACHINE_MODE, mode.save(registries));
        }

        if (isRangeVisible) {
            tag.putBoolean(MachineNBTKeys.IS_RANGE_VISIBLE, isRangeVisible);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        boundSoul = Soul.parseOptional(lookupProvider, tag.getCompound(MachineNBTKeys.ENTITY_STORAGE));

        if (tag.contains(MachineNBTKeys.MACHINE_MODE)) {
            this.mode = PoweredSpawnerMode.parse(lookupProvider,
                    Objects.requireNonNull(tag.get(MachineNBTKeys.MACHINE_MODE)));
        }

        // TODO: Ender IO 8 - remove support for old attachment loading
        if (hasData(EIOAttachments.ACTION_RANGE)) {
            var actionRange = getData(EIOAttachments.ACTION_RANGE);
            isRangeVisible = actionRange.isVisible();
            removeData(EIOAttachments.ACTION_RANGE);
        }

        isRangeVisible = tag.contains(MachineNBTKeys.IS_RANGE_VISIBLE)
                && tag.getBoolean(MachineNBTKeys.IS_RANGE_VISIBLE);

        // Load task host last
        taskHost.load(lookupProvider, tag);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);
        boundSoul = components.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY);

        // TODO: Ender IO 8 - remove.
        var actionRange = components.get(EIODataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.isRangeVisible = actionRange.isVisible();
        }

        Boolean isVisible = components.get(EIODataComponents.IS_RANGE_VISIBLE);
        if (isVisible != null) {
            this.isRangeVisible = isVisible;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        // Only if unchanged.
        if (isRangeVisible) {
            components.set(EIODataComponents.IS_RANGE_VISIBLE, true);
        }

        if (boundSoul.hasEntity()) {
            components.set(EIODataComponents.SOUL, boundSoul);
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(MachineNBTKeys.IS_RANGE_VISIBLE);
        tag.remove(MachineNBTKeys.ENTITY_STORAGE);
    }

    @Override
    public void neighborChanged(Block neighborBlock, BlockPos neighborPos) {
        super.neighborChanged(neighborBlock, neighborPos);
        if (level != null && !level.isClientSide() && getBlockPos().above().equals(neighborPos)) {
            mindKiller = level.getBlockState(neighborPos).is(EIOTags.Blocks.MIND_KILLER);
        }
    }

    public boolean hasMindKiller() {
        return this.mindKiller;
    }

    // endregion

    public enum SpawnerBlockedReason {
        TOO_MANY_MOB(MachinesLang.POWERED_SPAWNER_STATUS_OVERCROWDED_MOBS),
        TOO_MANY_SPAWNER(MachinesLang.POWERED_SPAWNER_STATUS_OVERCROWDED_SPAWNERS),
        UNKNOWN_MOB(MachinesLang.POWERED_SPAWNER_STATUS_UNKNOWN_MOB),
        OTHER_MOD(MachinesLang.POWERED_SPAWNER_STATUS_OTHER_MOD),
        DISABLED(MachinesLang.POWERED_SPAWNER_STATUS_DISABLED),
        INPUT_EMPTY(MachinesLang.STATUS_INPUT_EMPTY), OUTPUT_FULL(MachinesLang.STATUS_OUTPUT_FULL),
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
