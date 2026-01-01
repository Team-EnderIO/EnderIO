package com.enderio.enderio.client.content.conduits.model.bundle.port;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.client.model.quad.BakedColors;
import org.joml.Vector3f;

public final class MutableQuad {
    final BakedQuad quad;
    final Vector3f[] vertexPositions = new Vector3f[4];
    long[] vertexUV = new long[4];
    TextureAtlasSprite sprite;
    BakedColors colors;

    public MutableQuad(BakedQuad quad) {
        this.quad = quad;
        for (var i = 0; i < 4; i++) {
            //We need a new instance, otherwise we would pollute the original instances with changes
            this.vertexPositions[i] = new Vector3f(quad.position(i));
            this.vertexUV[i] = quad.packedUV(i);
        }
        this.sprite = quad.sprite();
        this.colors = quad.bakedColors();
    }

    public void withColor(BakedColors color) {
        this.colors = color;
    }

    public void withColor(int color) {
        this.colors = BakedColors.of(color);
    }

    public BakedQuad toBakedQuad() {
        return new BakedQuad(
            vertexPositions[0], vertexPositions[1], vertexPositions[2], vertexPositions[3],
            vertexUV[0], vertexUV[1], vertexUV[2], vertexUV[3],
            quad.tintIndex(),
            quad.direction(),
            sprite,
            quad.shade(),
            quad.lightEmission(),
            quad.bakedNormals(),
            colors,
            quad.hasAmbientOcclusion()
        );
    }
}
