package com.enderio.machines.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.capability.StoredEntityData;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.particle.RangeParticleData;
import com.enderio.core.common.sync.BooleanDataSlot;
import com.enderio.core.common.sync.EnumDataSlot;
import com.enderio.core.common.sync.ResourceLocationDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.blockentity.task.IMachineTask;
import com.enderio.machines.common.blockentity.task.SpawnTask;
import com.enderio.machines.common.blockentity.task.host.MachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// TODO: I want to revisit the powered spawner and task
//       But there's not enough time before alpha, so just porting as-is.
public class PoweredSpawnerBlockEntity extends PoweredMachineEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.POWERED_SPAWNER_USAGE);
    public static final ResourceLocation NO_MOB = EnderIO.loc("no_mob");
    private StoredEntityData entityData = StoredEntityData.empty();
    private int range = 3;
    private boolean rangeVisible;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    private SpawnerBlockedReason reason = SpawnerBlockedReason.NONE;

    private final MachineTaskHost taskHost;

    public PoweredSpawnerBlockEntity(BlockEntityType type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, type, worldPosition, blockState);
        add2WayDataSlot(new BooleanDataSlot(this::isShowingRange, this::shouldShowRange, SyncMode.GUI));
        addDataSlot(new ResourceLocationDataSlot(() -> this.getEntityType().orElse(NO_MOB),this::setEntityType, SyncMode.GUI));
        addDataSlot(new EnumDataSlot<>(this::getReason, this::setReason, SyncMode.GUI));

        String color = MachinesConfig.CLIENT.BLOCKS.POWERED_SPAWNER_RANGE_COLOR.get();
        this.rCol = (float)Integer.parseInt(color.substring(0,2), 16) / 255;
        this.gCol = (float)Integer.parseInt(color.substring(2,4), 16) / 255;
        this.bCol = (float)Integer.parseInt(color.substring(4,6), 16) / 255;

        taskHost = new MachineTaskHost(this, () -> energyStorage.getEnergyStored() > 0) {
            @Override
            protected @Nullable IMachineTask getNewTask() {
                return createTask();
            }

            @Override
            protected @Nullable IMachineTask loadTask(CompoundTag nbt) {
                SpawnTask task = createTask();
                task.deserializeNBT(nbt);
                return task;
            }
        };
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

        if (this.isShowingRange()) {
            generateParticle(new RangeParticleData(getRange(), this.rCol, this.gCol, this.bCol),
                new Vec3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()));
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

    // region Task

    public float getSpawnProgress() {
        return taskHost.getProgress();
    }

    private SpawnTask createTask() {
        return new SpawnTask(this, this.getEnergyStorage());
    }

    // endregion

    public int getRange() {
        return range;
    }

    public Optional<ResourceLocation> getEntityType() {
        return entityData.getEntityType();
    }

    public void setEntityType(ResourceLocation entityType) {
        entityData = StoredEntityData.of(entityType);
    }

    public StoredEntityData getEntityData() {
        return entityData;
    }

    public boolean isShowingRange() {
        return this.rangeVisible;
    }

    public void shouldShowRange(Boolean show) {
        this.rangeVisible = show;
    }

    private void generateParticle(RangeParticleData data, Vec3 pos) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                serverLevel.sendParticles(player, data, true, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
            }
        }
    }

    public SpawnerBlockedReason getReason() {
        return this.reason;
    }

    public void setReason(SpawnerBlockedReason reason) {
        this.reason = reason;
    }

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("EntityStorage", entityData.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        entityData.deserializeNBT(pTag.getCompound("EntityStorage"));
    }

    // endregion

    public enum SpawnerBlockedReason {
        TOO_MANY_MOB(MachineLang.TOO_MANY_MOB),
        TOO_MANY_SPAWNER(MachineLang.TOO_MANY_SPAWNER),
        UNKOWN_MOB(MachineLang.UNKNOWN),
        OTHER_MOD(MachineLang.OTHER_MOD),
        NONE(Component.literal("NONE"));

        private final Component component;

        SpawnerBlockedReason(Component component) {
            this.component = component;
        }

        public Component getComponent() {
            return component;
        }
    }
}
