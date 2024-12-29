package com.enderio.base.client.paint.model;

import com.enderio.core.client.RenderUtil;
import com.enderio.core.data.model.ModelHelper;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public record PaintingQuadTransformer(BlockState paint, @Nullable RenderType type) implements IQuadTransformer {

    @Override
    public void processInPlace(BakedQuad quad) {
        TextureAtlasSprite sprite = getSpriteForDirection(quad.getDirection());
        for (int i = 0; i < 4; i++) {
            float[] uv0 = RenderUtil.unpackVertices(quad.getVertices(), i, IQuadTransformer.UV0, 2);
            uv0[0] = (uv0[0] - quad.getSprite().getU0()) * sprite.contents().width()
                    / quad.getSprite().contents().width() + sprite.getU0();
            uv0[1] = (uv0[1] - quad.getSprite().getV0()) * sprite.contents().height()
                    / quad.getSprite().contents().height() + sprite.getV0();
            int[] packedTextureData = RenderUtil.packUV(uv0[0], uv0[1]);
            quad.getVertices()[IQuadTransformer.UV0 + i * IQuadTransformer.STRIDE] = packedTextureData[0];
            quad.getVertices()[IQuadTransformer.UV0 + 1 + i * IQuadTransformer.STRIDE] = packedTextureData[1];
        }
        quad.sprite = sprite;
    }

    private TextureAtlasSprite getSpriteForDirection(Direction direction) {
        List<BakedQuad> quads = getModel(paint).getQuads(paint, direction, RandomSource.create(), ModelData.EMPTY,
                type);
        return quads.isEmpty() ? ModelHelper.getMissingTexture() : quads.get(0).getSprite();
    }

    private BakedModel getModel(BlockState state) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    }
}
