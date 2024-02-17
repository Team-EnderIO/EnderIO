package com.enderio.machines.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.attachment.StoredEntityData;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
import com.enderio.core.common.network.slot.ResourceLocationNetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.task.IMachineTask;
import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import com.enderio.machines.common.blockentity.task.host.MachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
// TODO: I want to revisit the powered spawner and task
//       But there's not enough time before alpha, so just porting as-is.
public class PoweredSpawnerBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_USAGE);
    public static final ResourceLocation NO_MOB = EnderIO.loc("no_mob");
    private StoredEntityData entityData = StoredEntityData.empty();
    private SpawnerBlockedReason reason = SpawnerBlockedReason.NONE;
    private final MachineTaskHost taskHost;


    public PoweredSpawnerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, MachineBlockEntities.POWERED_SPAWNER.get(), worldPosition, blockState);

        rangeVisibleDataSlot = new BooleanNetworkDataSlot(this::isRangeVisible, b -> this.rangeVisible = b);
        addDataSlot(rangeVisibleDataSlot);

        addDataSlot(new ResourceLocationNetworkDataSlot(() -> this.getEntityType().orElse(NO_MOB), rl -> {
            setEntityType(rl);
            EnderIO.LOGGER.info("UPDATED ENTITY TYPE.");
        }));
        range = 4;

        taskHost = new MachineTaskHost(this, this::hasEnergy) {
            @Override
            protected @Nullable IMachineTask getNewTask() {
                return createTask();
            }

            @Override
            protected @Nullable IMachineTask loadTask(CompoundTag nbt) {
                SpawnerMachineTask task = createTask();
                task.deserializeNBT(nbt);
                return task;
            }
        };

        updateMachineState(new MachineState(MachineStateType.ERROR, this.reason.component), false);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PoweredSpawnerMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            taskHost.tick();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        taskHost.onLevelReady();
    }

    // region Inventory

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder().capacitor().build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        taskHost.newTaskAvailable();
    }

    // endregion

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.POWERED_SPAWNER_RANGE_COLOR.get();
    }

    @Override
    public int getMaxRange() {
        return 3;
    }

    // region Task

    public float getSpawnProgress() {
        return taskHost.getProgress();
    }

    @Override
    protected boolean isActive() {
        return canAct() && hasEnergy() && taskHost.hasTask();
    }

    private SpawnerMachineTask createTask() {
        return new SpawnerMachineTask(this, this.getEnergyStorage(), this.getEntityType());
    }

    // endregion

    public Optional<ResourceLocation> getEntityType() {
        return entityData.getEntityType();
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
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(MachineNBTKeys.ENTITY_STORAGE, entityData.serializeNBT());
        taskHost.save(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        entityData.deserializeNBT(pTag.getCompound(MachineNBTKeys.ENTITY_STORAGE));
        taskHost.load(pTag);
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
