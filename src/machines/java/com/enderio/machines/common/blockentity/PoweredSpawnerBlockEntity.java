package com.enderio.machines.common.blockentity;

import com.enderio.api.capability.StoredEntityData;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.base.common.particle.RangeParticleData;
import com.enderio.core.common.sync.BooleanDataSlot;
import com.enderio.core.common.sync.EnumDataSlot;
import com.enderio.core.common.sync.ResourceLocationDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.PoweredTaskMachineEntity;
import com.enderio.machines.common.blockentity.task.SpawnTask;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

public class PoweredSpawnerBlockEntity extends PoweredTaskMachineEntity<SpawnTask> {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 200f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 160f);
    private StoredEntityData entityData = StoredEntityData.empty();
    private int range = 3;
    private boolean rangeVisible;
    protected float rCol = 1;
    protected float gCol = 0;
    protected float bCol = 0;
    private SpawnerBlockedReason reason = SpawnerBlockedReason.NONE;

    public PoweredSpawnerBlockEntity(BlockEntityType type, BlockPos worldPosition, BlockState blockState) {
        super(CAPACITY, TRANSFER, USAGE, type, worldPosition, blockState);
        add2WayDataSlot(new BooleanDataSlot(this::isShowingRange, this::shouldShowRange, SyncMode.GUI));
        addDataSlot(new ResourceLocationDataSlot(() -> this.getEntityType().orElse(new ResourceLocation("pig")),this::setEntityType, SyncMode.GUI));
        addDataSlot(new EnumDataSlot<>(this::getReason, this::setReason, SyncMode.GUI));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PoweredSpawnerMenu(this, pPlayerInventory, pContainerId);
    }

    @Nullable
    @Override
    protected  SpawnTask getNewTask() {
        return createTask();
    }

    @Nullable
    @Override
    protected SpawnTask loadTask(CompoundTag nbt) {
        SpawnTask task = createTask();
        task.deserializeNBT(nbt);
        return task;
    }

    private SpawnTask createTask() {
        return new SpawnTask(this, this.getEnergyStorage());
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder().capacitor().build();
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (this.isShowingRange()) {
            generateParticle(new RangeParticleData(getRange(), this.rCol, this.gCol, this.bCol),
                new Vec3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()));
        }
    }

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
        if (!isClientSide() && level instanceof ServerLevel level) {
            for (ServerPlayer player : level.players()) {
                level.sendParticles(player, data, true, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
            }
        }
    }

    public SpawnerBlockedReason getReason() {
        return this.reason;
    }

    public void setReason(SpawnerBlockedReason reason) {
        this.reason = reason;
    }

    public enum SpawnerBlockedReason {
        TOO_MANY_MOB("mob"),
        UNKOWN_MOB("unknown"),
        OTHER_MOD("other"),
        NONE("none");

        private final String name;

        SpawnerBlockedReason(String name) {
            this.name = name;
        }
    }
}
