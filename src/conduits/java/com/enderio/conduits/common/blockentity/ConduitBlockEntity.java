package com.enderio.conduits.common.blockentity;

import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class ConduitBlockEntity extends EnderBlockEntity {

    private static final ModelProperty<ConduitBundle> BUNDLE_MODEL_PROPERTY = new ModelProperty<>();

    //TODO:  check if 0 is a viable ticksetting
    private final ConduitBundle bundle = new ConduitBundle(() -> {
        if (!level.isClientSide())
            level.scheduleTick(getBlockPos(), ConduitBlocks.CONDUIT.get(), 0);
    });

    public ConduitBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        bundle.gatherDataSlots().forEach(this::addDataSlot);
        addAfterSyncRunnable(this::requestModelDataUpdate);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(BUNDLE_MODEL_PROPERTY, bundle).build();
    }
}
