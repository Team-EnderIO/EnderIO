package com.enderio.conduits.client.model;

import com.enderio.core.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;

public record ConduitTextureEmissiveQuadTransformer(TextureAtlasSprite newSprite, int lightLevel) implements IQuadTransformer {

    @Override
    public void processInPlace(BakedQuad quad) {
        if (lightLevel != 0) {
            QuadTransformers.settingEmissivity(lightLevel).processInPlace(quad);
        }

        for (int i = 0; i < 4; i++) {
            float[] uv0 = RenderUtil.unpackVertices(quad.getVertices(), i, IQuadTransformer.UV0, 2);
            uv0[0] = (uv0[0] - quad.getSprite().getU0()) * newSprite.contents().width() / quad.getSprite().contents().width() + newSprite.getU0();
            uv0[1] = (uv0[1] - quad.getSprite().getV0()) * newSprite.contents().height() / quad.getSprite().contents().height() + newSprite.getV0();
            int[] packedTextureData = RenderUtil.packUV(uv0[0], uv0[1]);
            quad.getVertices()[IQuadTransformer.UV0 + i * IQuadTransformer.STRIDE] = packedTextureData[0];
            quad.getVertices()[IQuadTransformer.UV0 + 1 + i * IQuadTransformer.STRIDE] = packedTextureData[1];
        }
        quad.sprite = newSprite;
    }

    private static TextureAtlas blockAtlas() {
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
    }

}
