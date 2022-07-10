package com.enderio.decoration.client.model.painted;

import com.enderio.decoration.common.blockentity.SinglePaintedBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PaintedSimpleModel extends PaintedModel implements IDynamicBakedModel {

    private final Block referenceModel;

    public PaintedSimpleModel(Block referenceModel, ItemTransforms transforms, @Nullable Direction itemTextureRotation) {
        super(transforms, itemTextureRotation);
        this.referenceModel = referenceModel;
    }

    @Override
    protected Block copyModelFromBlock() {
        return referenceModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
        RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> shape = getModelFromOwn(state).getQuads(copyBlockState(state), side, rand);
        Direction direction = null;
        if (state != null) {
            for (Property<?> property : state.getProperties()) {
                if (property instanceof DirectionProperty directionProperty) {
                    direction = state.getValue(directionProperty).getOpposite();
                }
            }
        }
        return getQuadsUsingShape(extraData.get(SinglePaintedBlockEntity.PAINT), shape, side, rand, direction, renderType);
    }
}
