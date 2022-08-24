package com.enderio.conduits.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.ConduitShape;
import com.enderio.conduits.common.blockentity.action.RightClickAction;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.sync.NBTSerializableDataSlot;
import com.enderio.core.common.sync.SyncMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.LogicalSide;

import java.util.Optional;

public class ConduitBlockEntity extends EnderBlockEntity {

    public static final ModelProperty<ConduitBundle> BUNDLE_MODEL_PROPERTY = new ModelProperty<>();

    private ConduitShape shape = new ConduitShape();

    private final ConduitBundle bundle = new ConduitBundle(this::scheduleTick);
    @UseOnly(LogicalSide.CLIENT)
    private ConduitBundle clientBundle = bundle.deepCopy();

    public ConduitBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        addDataSlot(new NBTSerializableDataSlot<>(this::getBundle, SyncMode.WORLD));
        addAfterSyncRunnable(this::updateClient);
    }

    public void updateClient() {
        clientBundle = bundle.deepCopy();
        updateShape();
        requestModelDataUpdate();
        level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());
    }

    private void scheduleTick() {
        if (!level.isClientSide())
        //    level.scheduleTick(getBlockPos(), ConduitBlocks.CONDUIT.get(), 0);
        setChanged();
    }

    @Override
    public void onLoad() {
        if (!level.isClientSide())
            sync();
    }

    public void everyTick() {
        serverTick();
    }

    public void doNothing() {}
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("conduit", bundle.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        bundle.deserializeNBT(tag.getCompound("conduit"));
    }

    //TODO: Make this async compatible as this might cause crashes, further investigation required (Like Concurrent ModificationException or something like that. Maybe return a synchronized Copy of bundle)
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(BUNDLE_MODEL_PROPERTY, clientBundle).build();
    }

    public RightClickAction addType(IConduitType type) {
        RightClickAction action = bundle.addType(type);
        //something has changed
        if (action.hasChanged()) {
            for (Direction dir: Direction.values()) {
                BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(dir));
                if (blockEntity != null) {
                    //add possible connections if you are upgrading or inserting
                    if (blockEntity instanceof ConduitBlockEntity conduit && conduit.connectTo(dir.getOpposite(), type)) {
                        connect(dir, type);
                    } else if(blockEntity.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
                        connectEnd(dir, type);
                    }
                }
            }

            if (action instanceof RightClickAction.Upgrade upgrade) {
                removeNeighborConnections(upgrade.getNotInConduit());
            }
            updateShape();
        }
        return action;
    }

    public boolean removeType(IConduitType type) {
        boolean shouldRemove =  bundle.removeType(type);
        //something has changed
        removeNeighborConnections(type);
        updateShape();
        return shouldRemove;
    }

    public void removeNeighborConnections(IConduitType type) {
        for (Direction dir: Direction.values()) {
            BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(dir));
            if (blockEntity instanceof ConduitBlockEntity conduit) {
                conduit.disconnect(dir.getOpposite(), type);
                conduit.updateShape();
            }
        }
    }

    private void updateShape() {
        shape.updateConduit(bundle);
    }

    public static boolean isDifferent(IConduitType first, IConduitType second) {
        return first != second;
    }

    /**
     *
     * @param direction the Direction to connect to
     * @param type the type to be connected
     * @return true if a connection happens
     */
    private boolean connectTo(Direction direction, IConduitType type) {
        if (!bundle.getTypes().contains(type))
            return false;
        connect(direction, type);
        return true;
    }

    private void connect(Direction direction, IConduitType type) {
        bundle.connectTo(direction, type, false);
        updateClient();
    }

    private void connectEnd(Direction direction, IConduitType type) {
        bundle.connectTo(direction, type, true);
        updateClient();
    }

    private void disconnect(Direction direction, IConduitType type) {
        bundle.disconnectFrom(direction, type);
        updateClient();
    }

    public ConduitBundle getBundle() {
        return bundle;
    }

    public ConduitBundle getClientBundle() {
        return clientBundle;
    }

    public ConduitShape getShape() {
        return shape;
    }
}
