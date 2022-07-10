package com.enderio.decoration.client.model.painted;

import com.enderio.core.data.model.EIOModel;
import com.enderio.decoration.common.blockentity.DoublePaintedBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PaintedSlabModel extends PaintedModel implements IDynamicBakedModel {

    private final Block referenceModel;

    public PaintedSlabModel(Block referenceModel, ItemTransforms transforms, @Nullable Direction itemTextureRotation) {
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
        List<BakedQuad> quads = new ArrayList<>();
        if (state != null && state.hasProperty(SlabBlock.TYPE)) {
            SlabType slabType = state.getValue(SlabBlock.TYPE);
            if (slabType == SlabType.BOTTOM || slabType == SlabType.DOUBLE) {
                Block paint = extraData.get(DoublePaintedBlockEntity.PAINT);
                // @formatter:off
                List<BakedQuad> shape = getModel(referenceModel.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM))
                    .getQuads(state, side, rand, ModelData.EMPTY, renderType);
                // @formatter:on
                quads.addAll(getQuadsUsingShape(paint, shape, side, rand, null, renderType));
            }
            if (slabType == SlabType.TOP || slabType == SlabType.DOUBLE) {
                Block paint = extraData.get(DoublePaintedBlockEntity.PAINT2);
                // @formatter:off
                List<BakedQuad> shape = getModel(referenceModel.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP))
                    .getQuads(state, side, rand, ModelData.EMPTY, renderType);
                // @formatter:on
                quads.addAll(getQuadsUsingShape(paint, shape, side, rand, null, renderType));
            }
        }
        return quads;
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        TextureAtlasSprite sprite = super.getParticleIcon(data);
        if (!sprite.getName().getPath().equals("missingno"))
            return sprite;
        Block paint = data.get(DoublePaintedBlockEntity.PAINT2);
        if (paint != null) {
            BakedModel model = getModel(paint.defaultBlockState());
            return model.getParticleIcon(ModelData.EMPTY);
        }
        return EIOModel.getMissingTexture();
    }
}
