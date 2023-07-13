package com.enderio.base.client.model;

import com.enderio.core.client.RenderUtil;
import com.enderio.core.data.model.EIOModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public record PaintingQuadTransformer(BlockState paint, RenderType type) implements IQuadTransformer {

    @Override
    public void processInPlace(BakedQuad quad) {
        TextureAtlasSprite sprite = getSpriteForDirection(quad.getDirection());
        for (int i = 0; i < 4; i++) {
            float[] uv0 = RenderUtil.unpackVertices(quad.getVertices(), i, IQuadTransformer.UV0, 2);
            uv0[0] = (uv0[0] - quad.getSprite().getU0()) * sprite.contents().width() / quad.getSprite().contents().width() + sprite.getU0();
            uv0[1] = (uv0[1] - quad.getSprite().getV0()) * sprite.contents().height() / quad.getSprite().contents().height() + sprite.getV0();
            int[] packedTextureData = RenderUtil.packUV(uv0[0], uv0[1]);
            quad.getVertices()[4 + i * STRIDE] = packedTextureData[0];
            quad.getVertices()[5 + i * STRIDE] = packedTextureData[1];
        }
    }

    private TextureAtlasSprite getSpriteForDirection(Direction direction) {
        List<BakedQuad> quads = getModel(paint).getQuads(paint, direction, RandomSource.create(), ModelData.EMPTY, type);
        return quads.isEmpty() ? EIOModel.getMissingTexture() :quads.get(0).getSprite();
    }

    private BakedModel getModel(BlockState state) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    }
}
