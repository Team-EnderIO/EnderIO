package com.enderio.conduits.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.ConduitShape;
import com.enderio.conduits.common.blockentity.action.RightClickAction;
import com.enderio.conduits.common.network.ConduitSavedData;
import com.enderio.conduits.common.network.NodeIdentifier;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.sync.NBTSerializableDataSlot;
import com.enderio.core.common.sync.SyncMode;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConduitBlockEntity extends EnderBlockEntity {

    public static final ModelProperty<ConduitBundle> BUNDLE_MODEL_PROPERTY = new ModelProperty<>();

    private ConduitShape shape = new ConduitShape();

    private final ConduitBundle bundle;
    @UseOnly(LogicalSide.CLIENT)
    private ConduitBundle clientBundle;

    public ConduitBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        bundle = new ConduitBundle(this::scheduleTick, worldPosition);
        clientBundle = bundle.deepCopy();
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

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(BUNDLE_MODEL_PROPERTY, clientBundle).build();
    }

    public RightClickAction addType(IConduitType type) {
        RightClickAction action = bundle.addType(type);
        //something has changed
        if (action.hasChanged()) {
            List<GraphObject<Mergeable.Dummy>> nodes = new ArrayList<>();
            for (Direction dir: Direction.values()) {
                BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(dir));
                if (blockEntity != null) {
                    //add possible connections if you are upgrading or inserting
                    if (blockEntity instanceof ConduitBlockEntity conduit && conduit.connectTo(dir.getOpposite(), type)) {
                        nodes.add(conduit.bundle.getNodeFor(type));
                        connect(dir, type);
                    } else if(blockEntity.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
                        connectEnd(dir, type);
                    }
                }
            }
            if (level instanceof ServerLevel serverLevel) {
                Graph.integrate(bundle.getNodeFor(type), nodes);
                ConduitSavedData.addPotentialGraph(type, Objects.requireNonNull(bundle.getNodeFor(type).getGraph()), serverLevel);
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
        NodeIdentifier nodeFor = bundle.getNodeFor(type);
        for (Direction dir: Direction.values()) {
            BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(dir));
            if (blockEntity instanceof ConduitBlockEntity conduit) {
                if (conduit.disconnect(dir.getOpposite(), type)) {
                    conduit.updateShape();
                }
            }
        }
        if (level instanceof ServerLevel serverLevel) {
            if (nodeFor.getGraph() != null) {
                nodeFor.getGraph().remove(nodeFor);

                for (Direction dir: Direction.values()) {
                    BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(dir));
                    if (blockEntity instanceof ConduitBlockEntity conduit) {
                        @Nullable
                        Graph<Mergeable.Dummy> graph = conduit.bundle.getNodeFor(type).getGraph();
                        if (graph != null) {
                            ConduitSavedData.addPotentialGraph(type, graph, serverLevel);
                        }
                    }
                }
            }
        }
        bundle.removeNodeFor(type);
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

    private boolean disconnect(Direction direction, IConduitType type) {
        if (bundle.disconnectFrom(direction, type)) {
            updateClient();
            return true;
        }
        return false;
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
