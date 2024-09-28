package com.enderio.machines.common.blockentity.base;

import com.enderio.base.api.io.IOMode;
import com.enderio.base.common.util.AttractionUtil;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.attachment.ActionRange;
import com.enderio.machines.common.attachment.RangedActor;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import net.minecraft.client.multiplayer.ClientLevel;
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
public abstract class VacuumMachineBlockEntity<T extends Entity> extends MachineBlockEntity implements RangedActor {
    private static final double COLLISION_DISTANCE_SQ = 1 * 1;
    protected static final double SPEED = 0.025;
    protected static final double SPEED_4 = SPEED * 4;
    private List<WeakReference<T>> entities = new ArrayList<>();
    private Class<T> targetClass;
    public static SingleSlotAccess FILTER = new SingleSlotAccess();

    private NetworkDataSlot<ActionRange> actionRangeDataSlot;

    public VacuumMachineBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, Class<T> targetClass) {
        super(pType, pWorldPosition, pBlockState);
        this.targetClass = targetClass;

        actionRangeDataSlot = addDataSlot(ActionRange.DATA_SLOT_TYPE.create(this::getActionRange, this::internalSetActionRange));
    }

    public abstract String getColor();

    @Override
    public void serverTick() {
        if (!this.isRedstoneBlocked()) {
            this.attractEntities(this.getLevel(), this.getBlockPos(), this.getRange());
        }

        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (!this.isRedstoneBlocked()) {
            this.attractEntities(this.getLevel(), this.getBlockPos(), this.getRange());
        }

        if (level.isClientSide && level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getBlockPos(), getColor());
        }

        super.clientTick();
    }

    @Override
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(IOMode.PUSH);
    }

    @Override
    public boolean isIOConfigMutable() {
        return false;
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
    public void onLoad() {
        if (this.entities.isEmpty()) {
            getEntities(getLevel(), getBlockPos(), getRange(), getFilter());
        }
        super.onLoad();
    }
}
