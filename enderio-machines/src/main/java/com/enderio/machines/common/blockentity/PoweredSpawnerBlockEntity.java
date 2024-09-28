package com.enderio.machines.common.blockentity;

import com.enderio.EnderIOBase;
import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.attachment.ActionRange;
import com.enderio.machines.common.attachment.RangedActor;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.task.MachineTask;
import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import com.enderio.machines.common.blockentity.task.host.MachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IOwnedSpawner;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
// TODO: I want to revisit the powered spawner and task
//       But there's not enough time before alpha, so just porting as-is.
public class PoweredSpawnerBlockEntity extends PoweredMachineBlockEntity implements RangedActor, IOwnedSpawner {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_USAGE);
    public static final ResourceLocation NO_MOB = EnderIOBase.loc("no_mob");
    private StoredEntityData entityData = StoredEntityData.EMPTY;
    private SpawnerBlockedReason reason = SpawnerBlockedReason.NONE;
    private final MachineTaskHost taskHost;

    private final NetworkDataSlot<ActionRange> actionRangeDataSlot;

    public PoweredSpawnerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, MachineBlockEntities.POWERED_SPAWNER.get(), worldPosition, blockState);

        // TODO: rubbish way of having a default. use an interface instead?
        if (!hasData(MachineAttachments.ACTION_RANGE)) {
            setData(MachineAttachments.ACTION_RANGE, new ActionRange(4, false));
        }

        actionRangeDataSlot = addDataSlot(ActionRange.DATA_SLOT_TYPE.create(this::getActionRange, this::internalSetActionRange));
        addDataSlot(NetworkDataSlot.RESOURCE_LOCATION.create(() -> this.getEntityType().orElse(NO_MOB), this::setEntityType));

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
        return new PoweredSpawnerMenu(pContainerId, this, pPlayerInventory);
    }

    public int getMaxRange() {
        return 3;
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
        setChanged();
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
        if (level.isClientSide && level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getBlockPos(), MachinesConfig.CLIENT.BLOCKS.POWERED_SPAWNER_RANGE_COLOR.get());
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
    protected boolean isActive() {
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
        pTag.put(MachineNBTKeys.ENTITY_STORAGE, entityData.saveOptional(lookupProvider));
        taskHost.save(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        entityData = StoredEntityData.parseOptional(lookupProvider, pTag.getCompound(MachineNBTKeys.ENTITY_STORAGE));
        taskHost.load(lookupProvider, pTag);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);
        entityData = components.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (entityData.hasEntity()) {
            components.set(EIODataComponents.STORED_ENTITY, entityData);
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(MachineNBTKeys.ENTITY_STORAGE);
    }

    // endregion

    public enum SpawnerBlockedReason {
        TOO_MANY_MOB(MachineLang.TOO_MANY_MOB),
        TOO_MANY_SPAWNER(MachineLang.TOO_MANY_SPAWNER),
        UNKNOWN_MOB(MachineLang.UNKNOWN),
        OTHER_MOD(MachineLang.OTHER_MOD),
        DISABLED(MachineLang.DISABLED),
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
