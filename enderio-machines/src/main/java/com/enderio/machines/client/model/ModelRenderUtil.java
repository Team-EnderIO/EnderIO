package com.enderio.machines.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

public class ModelRenderUtil {
    /**
     * Helper to create vertices for a quad face. Simplifies a lot.
     */
    // TODO: Make these correspond with the center 0.5,0.5,0.5
    public static Vec3[] createQuadVerts(Direction face, double leftEdge, double rightEdge, double elevation) {
        return switch (face) {
        case DOWN -> new Vec3[] {
            new Vec3(leftEdge, 1 - elevation, leftEdge),
            new Vec3(rightEdge, 1 - elevation, leftEdge),
            new Vec3(rightEdge, 1 - elevation, rightEdge),
            new Vec3(leftEdge, 1 - elevation, rightEdge)
        };
        case UP -> new Vec3[] {
            new Vec3(leftEdge, elevation, leftEdge),
            new Vec3(leftEdge, elevation, rightEdge),
            new Vec3(rightEdge, elevation, rightEdge),
            new Vec3(rightEdge, elevation, leftEdge)
        };
        case NORTH -> new Vec3[] {
            new Vec3(rightEdge, rightEdge, 1 - elevation),
            new Vec3(rightEdge, leftEdge, 1 - elevation),
            new Vec3(leftEdge, leftEdge, 1 - elevation),
            new Vec3(leftEdge, rightEdge, 1 - elevation)
        };
        case SOUTH -> new Vec3[] {
            new Vec3(leftEdge, rightEdge, elevation),
            new Vec3(leftEdge, leftEdge, elevation),
            new Vec3(rightEdge, leftEdge, elevation),
            new Vec3(rightEdge, rightEdge, elevation)
        };
        case WEST -> new Vec3[] {
            new Vec3(1 - elevation, rightEdge, leftEdge),
            new Vec3(1 - elevation, leftEdge, leftEdge),
            new Vec3(1 - elevation, leftEdge, rightEdge),
            new Vec3(1 - elevation, rightEdge, rightEdge)
        };
        case EAST -> new Vec3[] {
            new Vec3(elevation, rightEdge, rightEdge),
            new Vec3(elevation, leftEdge, rightEdge),
            new Vec3(elevation, leftEdge, leftEdge),
            new Vec3(elevation, rightEdge, leftEdge)
        };
        };
    }

    public static BakedQuad createQuad(Vec3[] verts, TextureAtlasSprite sprite) {
        return createQuad(verts[0], verts[1], verts[2], verts[3], sprite);
    }

    public static BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();
        int tw = sprite.getWidth();
        int th = sprite.getHeight();

        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(Direction.getNearest(normal.x, normal.y, normal.z));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, th, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v3.x, v3.y, v3.z, tw, th, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v4.x, v4.y, v4.z, tw, 0, sprite, 1.0f, 1.0f, 1.0f, 1.0f);
        return builder.build();
    }

    public static BakedQuad createQuad(Vec3[] verts, TextureAtlasSprite sprite, int color) {
        return createQuad(verts[0], verts[1], verts[2], verts[3], sprite, color, 1.0f);
    }

    public static BakedQuad createQuad(Vec3[] verts, TextureAtlasSprite sprite, int color, float alpha) {
        return createQuad(verts[0], verts[1], verts[2], verts[3], sprite, color, alpha);
    }

    public static BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite, int color, float alpha) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();
        int tw = sprite.getWidth();
        int th = sprite.getHeight();

        float r = FastColor.ARGB32.red(color) / 255.0f;
        float g = FastColor.ARGB32.green(color) / 255.0f;
        float b = FastColor.ARGB32.blue(color) / 255.0f;

        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(Direction.getNearest(normal.x, normal.y, normal.z));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, r, g, b, alpha);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, th, sprite, r, g, b, alpha);
        putVertex(builder, normal, v3.x, v3.y, v3.z, tw, th, sprite, r, g, b, alpha);
        putVertex(builder, normal, v4.x, v4.y, v4.z, tw, 0, sprite, r, g, b, alpha);
        return builder.build();
    }

    // Thanks McJty
    private static void putVertex(BakedQuadBuilder builder, Vec3 normal,
        double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b, float a) {

        ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements().asList();
        for (int j = 0 ; j < elements.size() ; j++) {
            VertexFormatElement e = elements.get(j);
            switch (e.getUsage()) {
            case POSITION:
                builder.put(j, (float) x, (float) y, (float) z, 1.0f);
                break;
            case COLOR:
                builder.put(j, r, g, b, a);
                break;
            case UV:
                switch (e.getIndex()) {
                case 0:
                    float iu = sprite.getU(u);
                    float iv = sprite.getV(v);
                    builder.put(j, iu, iv);
                    break;
                case 2:
                    builder.put(j, (short) 0, (short) 0);
                    break;
                default:
                    builder.put(j);
                    break;
                }
                break;
            case NORMAL:
                builder.put(j, (float) normal.x, (float) normal.y, (float) normal.z);
                break;
            default:
                builder.put(j);
                break;
            }
        }
    }
}
