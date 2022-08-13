package com.enderio.conduits.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.core.common.blockentity.EnderBlockEntity;
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

    private final ConduitBundle bundle = new ConduitBundle(this::scheduleTick);
    @UseOnly(LogicalSide.CLIENT)
    private ConduitBundle clientBundle = bundle.deepCopy();

    public ConduitBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        bundle.gatherDataSlots().forEach(this::addDataSlot);
        addAfterSyncRunnable(() -> {
            clientBundle = bundle.deepCopy();
            level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());
        });
    }

    private void scheduleTick() {
        if (!level.isClientSide())
            level.scheduleTick(getBlockPos(), ConduitBlocks.CONDUIT.get(), 0);

    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    //TODO: Make this async compatible as this might cause crashes, further investigation required (Like Concurrent ModificationException or something like that. Maybe return a synchronized Copy of bundle)
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(BUNDLE_MODEL_PROPERTY, bundle).build();
    }

    public Optional<IConduitType> addType(IConduitType type) {
        var returnType =  bundle.addType(type);
        //something has changed
        if (isDifferent(returnType, type)) {
            for (Direction dir: Direction.values()) {
                BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(dir));
                if (blockEntity != null) {
                    if (blockEntity instanceof ConduitBlockEntity conduit && conduit.connectTo(dir.getOpposite(), type)) {
                        connect(dir, type);
                    } else if(blockEntity.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
                        connectEnd(dir, type);
                    }
                }
            }
        }
        return returnType;
    }

    public static boolean isDifferent(Optional<IConduitType> first, IConduitType second) {
        return first.map(conduit -> conduit != second).orElse(true);
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
    }

    private void connectEnd(Direction direction, IConduitType type) {
        bundle.connectTo(direction, type, true);
    }
}
