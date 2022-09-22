package com.enderio.machines.common.blockentity;

import com.enderio.api.capability.StoredEntityData;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.machines.common.blockentity.base.PoweredTaskMachineEntity;
import com.enderio.machines.common.blockentity.task.SpawnTask;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PoweredSpawnerBlockEntity extends PoweredTaskMachineEntity<SpawnTask> {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 30f);
    private StoredEntityData entityData = StoredEntityData.empty();

    private int range = 3;

    public PoweredSpawnerBlockEntity(BlockEntityType type, BlockPos worldPosition, BlockState blockState) {
        super(CAPACITY, TRANSFER, USAGE, type, worldPosition, blockState);
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
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("entitystorage", entityData.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        entityData.deserializeNBT(pTag.getCompound("entitystorage"));
    }

    public AABB getRange() {
        return new AABB(this.getBlockPos()).inflate(range);
    }

    public EntityType<? extends Entity> getEntityType() {
        return Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getValue(entityData.getEntityType().orElse(new ResourceLocation("pig"))));
    }

    public StoredEntityData getEntityData() {
        return entityData;
    }
}
