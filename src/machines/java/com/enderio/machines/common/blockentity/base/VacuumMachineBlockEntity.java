package com.enderio.machines.common.blockentity.base;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.base.common.util.AttractionUtil;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.io.FixedIOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

// TODO: I want to review the vacuum stuff too.
public abstract class VacuumMachineBlockEntity<T extends Entity> extends MachineBlockEntity {
    private static final double COLLISION_DISTANCE_SQ = 1 * 1;
    protected static final double SPEED = 0.025;
    protected static final double SPEED_4 = SPEED * 4;
    private List<WeakReference<T>> entities = new ArrayList<>();
    private Class<T> targetClass;

    public VacuumMachineBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, Class<T> targetClass) {
        super(pType, pWorldPosition, pBlockState);
        this.targetClass = targetClass;

        rangeDataSlot = new IntegerNetworkDataSlot(this::getRange, r -> this.range = r);
        addDataSlot(rangeDataSlot);

        rangeVisibleDataSlot = new BooleanNetworkDataSlot(this::isRangeVisible, b -> this.rangeVisible = b);
        addDataSlot(rangeVisibleDataSlot);
    }

    @Override
    public void serverTick() {
        if (this.getRedstoneControl().isActive(level.hasNeighborSignal(worldPosition))) {
            this.attractEntities(this.getLevel(), this.getBlockPos(), this.getRange());
        }

        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (this.getRedstoneControl().isActive(level.hasNeighborSignal(worldPosition))) {
            this.attractEntities(this.getLevel(), this.getBlockPos(), this.getRange());
        }

        super.clientTick();
    }

    @Override
    protected IIOConfig createIOConfig() {
        return new FixedIOConfig(IOMode.PUSH);
    }

    public Predicate<T> getFilter() {
        return (e -> true);
    }

    private void attractEntities(Level level, BlockPos pos, int range) {
        if ((level.getGameTime() + pos.asLong()) % 5 == 0) {
            getEntities(level, pos, range, getFilter());
        }
        Iterator<WeakReference<T>> iterator = entities.iterator();
        while (iterator.hasNext()) {
            WeakReference<T> ref = iterator.next();
            if (ref.get() == null) { //If the entity no longer exists, remove from the list
                iterator.remove();
                continue;
            }
            T entity = ref.get();
            if (entity.isRemoved()) { //If the entity no longer exists, remove from the list
                iterator.remove();
                continue;
            }
            if (AttractionUtil.moveToPos(entity, pos, SPEED, SPEED_4, COLLISION_DISTANCE_SQ)) {
                handleEntity(entity);
            }
        }
    }

    public abstract void handleEntity(T entity);

    private void getEntities(Level level, BlockPos pos, int range, Predicate<T> filter) {
        this.entities.clear();
        AABB area = new AABB(pos).inflate(range);
        for (T ie : level.getEntitiesOfClass(targetClass, area, filter)) {
            this.entities.add(new WeakReference<>(ie));
        }
    }

    @Override
    public int getMaxRange() {
        return 6;
    }

    @Override
    public void onLoad() {
        if (this.entities.isEmpty()) {
            getEntities(getLevel(), getBlockPos(), getRange(), getFilter());
        }
        super.onLoad();
    }
}
