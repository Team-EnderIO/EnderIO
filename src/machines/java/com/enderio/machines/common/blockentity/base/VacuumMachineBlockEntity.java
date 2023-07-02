package com.enderio.machines.common.blockentity.base;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.base.common.particle.RangeParticleData;
import com.enderio.base.common.util.AttractionUtil;
import com.enderio.core.common.sync.BooleanDataSlot;
import com.enderio.core.common.sync.IntegerDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.io.FixedIOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
    private static final int MAX_RANGE = 6;
    private int range = 6;
    protected float rCol = 1;
    protected float gCol = 0;
    protected float bCol = 0;

    private boolean rangeVisible = false;
    private List<WeakReference<T>> entities = new ArrayList<>();
    private Class<T> clazz;

    public VacuumMachineBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, Class<T> clazz) {
        super(pType, pWorldPosition, pBlockState);
        this.clazz = clazz;
        add2WayDataSlot(new IntegerDataSlot(this::getRange, this::setRange, SyncMode.GUI));
        add2WayDataSlot(new BooleanDataSlot(this::isShowingRange, this::shouldShowRange, SyncMode.GUI));
    }

    @Override
    public void serverTick() {
        if (this.getRedstoneControl().isActive(level.hasNeighborSignal(worldPosition))) {
            this.attractEntities(this.getLevel(), this.getBlockPos(), this.range);
        }

        if (this.isShowingRange()) {
            generateParticle(new RangeParticleData(getRange(), this.rCol, this.gCol, this.bCol),
                new Vec3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()));
        }
        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (this.getRedstoneControl().isActive(level.hasNeighborSignal(worldPosition))) {
            this.attractEntities(this.getLevel(), this.getBlockPos(), this.range);
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
        for (T ie : level.getEntitiesOfClass(clazz, area, filter)) {
            this.entities.add(new WeakReference<>(ie));
        }
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public boolean isShowingRange() {
        return this.rangeVisible;
    }

    public void shouldShowRange(boolean show) {
        this.rangeVisible = show;
    }

    public void decreaseRange() {
        if (this.range > 0) {
            this.range--;
        }
    }

    public void increaseRange() {
        if (this.range < MAX_RANGE) {
            this.range++;
        }
    }

    @Override
    public void onLoad() {
        if (this.entities.isEmpty()) {
            getEntities(getLevel(), getBlockPos(), getRange(), getFilter());
        }
        super.onLoad();
    }

    private void generateParticle(RangeParticleData data, Vec3 pos) {
        if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                serverLevel.sendParticles(player, data, true, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
            }
        }
    }
}
