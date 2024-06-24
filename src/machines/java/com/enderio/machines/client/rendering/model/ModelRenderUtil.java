package com.enderio.machines.client.rendering.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Vector3f;

public class ModelRenderUtil {
    /**
     * Helper to create vertices for a quad face. Simplifies a lot.
     */
    // TODO: Make these correspond with the center 0.5,0.5,0.5
    public static Vector3f[] createQuadVerts(Direction face, float leftEdge, float rightEdge, float elevation) {
        return switch (face) {
        case DOWN -> new Vector3f[] {
            new Vector3f(leftEdge, 1 - elevation, leftEdge),
            new Vector3f(rightEdge, 1 - elevation, leftEdge),
            new Vector3f(rightEdge, 1 - elevation, rightEdge),
            new Vector3f(leftEdge, 1 - elevation, rightEdge)
        };
        case UP -> new Vector3f[] {
            new Vector3f(leftEdge, elevation, leftEdge),
            new Vector3f(leftEdge, elevation, rightEdge),
            new Vector3f(rightEdge, elevation, rightEdge),
            new Vector3f(rightEdge, elevation, leftEdge)
        };
        case NORTH -> new Vector3f[] {
            new Vector3f(rightEdge, rightEdge, 1 - elevation),
            new Vector3f(rightEdge, leftEdge, 1 - elevation),
            new Vector3f(leftEdge, leftEdge, 1 - elevation),
            new Vector3f(leftEdge, rightEdge, 1 - elevation)
        };
        case SOUTH -> new Vector3f[] {
            new Vector3f(leftEdge, rightEdge, elevation),
            new Vector3f(leftEdge, leftEdge, elevation),
            new Vector3f(rightEdge, leftEdge, elevation),
            new Vector3f(rightEdge, rightEdge, elevation)
        };
        case WEST -> new Vector3f[] {
            new Vector3f(1 - elevation, rightEdge, leftEdge),
            new Vector3f(1 - elevation, leftEdge, leftEdge),
            new Vector3f(1 - elevation, leftEdge, rightEdge),
            new Vector3f(1 - elevation, rightEdge, rightEdge)
        };
        case EAST -> new Vector3f[] {
            new Vector3f(elevation, rightEdge, rightEdge),
            new Vector3f(elevation, leftEdge, rightEdge),
            new Vector3f(elevation, leftEdge, leftEdge),
            new Vector3f(elevation, rightEdge, leftEdge)
        };
        };
    }

    public static BakedQuad createQuad(Vector3f[] verts, TextureAtlasSprite sprite) {
        return createQuad(verts[0], verts[1], verts[2], verts[3], sprite);
    }

    public static BakedQuad createQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, TextureAtlasSprite sprite) {
        return createQuad(v1, v2, v3, v4, sprite, 0xFFFFFF, 1.0f);
    }

    public static BakedQuad createQuad(Vector3f[] verts, TextureAtlasSprite sprite, int color) {
        return createQuad(verts[0], verts[1], verts[2], verts[3], sprite, color, 1.0f);
    }

    public static BakedQuad createQuad(Vector3f[] verts, TextureAtlasSprite sprite, int color, float alpha) {
        return createQuad(verts[0], verts[1], verts[2], verts[3], sprite, color, alpha);
    }

    public static BakedQuad createQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, TextureAtlasSprite sprite, int color, float alpha) {
        Vector3f normal = new Vector3f(v3).sub(v2).cross(new Vector3f(v1).sub(v2)).normalize();

        float nx = normal.x;
        float ny = normal.y;
        float nz = normal.z;

        float tw = sprite.contents().width() / 16f;
        float th = sprite.contents().height() / 16f;

        float r = FastColor.ARGB32.red(color) / 255.0f;
        float g = FastColor.ARGB32.green(color) / 255.0f;
        float b = FastColor.ARGB32.blue(color) / 255.0f;

        QuadBakingVertexConsumer baker = new QuadBakingVertexConsumer();
        baker.setSprite(sprite);
        baker.setDirection(Direction.getNearest(normal.x, normal.y, normal.z));
        baker.addVertex(v1.x, v1.y, v1.z).setNormal(nx, ny, nz).setUv(sprite.getU(0), sprite.getV(0)).setColor(r, g, b, alpha);
        baker.addVertex(v2.x, v2.y, v2.z).setNormal(nx, ny, nz).setUv(sprite.getU(0), sprite.getV(th)).setColor(r, g, b, alpha);
        baker.addVertex(v3.x, v3.y, v3.z).setNormal(nx, ny, nz).setUv(sprite.getU(tw), sprite.getV(th)).setColor(r, g, b, alpha);
        baker.addVertex(v4.x, v4.y, v4.z).setNormal(nx, ny, nz).setUv(sprite.getU(tw), sprite.getV(0)).setColor(r, g, b, alpha);
        return baker.bakeQuad();
    }
}
