package com.enderio.core.client;

import static net.neoforged.neoforge.client.model.IQuadTransformer.COLOR;
import static net.neoforged.neoforge.client.model.IQuadTransformer.STRIDE;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.joml.Vector3f;

public class RenderUtil {
    /**
     * Render a face with its texture with local face coordinates.
     * Note: Up and Down UVs may not be accurate, please PR with a fix (and test all uses of this method) to modify it.
     * todo; is this confusing?
     */
    public static void renderFace(Direction face, PoseStack.Pose pose, VertexConsumer consumer,
            TextureAtlasSprite texture, float x, float y, float z, float w, float h, int color) {
        renderFace(face, pose, consumer, texture, x, y, z, w, h, color, LightTexture.FULL_BRIGHT);
    }

    public static void renderFace(Direction face, PoseStack.Pose pose, VertexConsumer consumer,
            TextureAtlasSprite texture, float x, float y, float z, float w, float h, int color, int light) {
        // Normals are taken from Direction enum. They are necessary for proper lighting
        // and block breaking textures
        switch (face) {
        case DOWN -> renderFace(pose, consumer, texture, color, light, x, x + w, 1.0f - z, 1.0f - z, y, y, y + h, y + h,
                x, x + w, y, y + h, 0, -1, 0);
        case UP -> renderFace(pose, consumer, texture, color, light, x, x + w, z, z, y + h, y + h, y, y, x, x + w, y,
                y + h, 0, 1, 0);
        case NORTH -> renderFace(pose, consumer, texture, color, light, x, x + w, y + h, y, z, z, z, z, x, x + w, y,
                y + h, 0, 0, -1);
        case SOUTH -> renderFace(pose, consumer, texture, color, light, x, x + w, y, y + h, 1.0f - z, 1.0f - z,
                1.0f - z, 1.0f - z, x + w, x, y + h, y, 0, 0, 1);
        case EAST -> renderFace(pose, consumer, texture, color, light, 1.0f - z, 1.0f - z, y + h, y, x, x + w, x + w, x,
                x, x + w, y, y + h, 1, 0, 0);
        case WEST -> renderFace(pose, consumer, texture, color, light, z, z, y, y + h, x, x + w, x + w, x, x + w, x,
                y + h, y, -1, 0, 0);
        default -> throw new IllegalStateException("Unexpected value: " + face);
        }
    }

    private static void renderFace(PoseStack.Pose pose, VertexConsumer consumer, TextureAtlasSprite texture, int color,
            int light, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, float u0,
            float u1, float v0, float v1, float normalX, float normalY, float normalZ) {
        float minU = u0 * texture.contents().width() / 16f;
        float maxU = u1 * texture.contents().width() / 16f;
        float minV = v0 * texture.contents().height() / 16f;
        float maxV = v1 * texture.contents().height() / 16f;

        consumer.addVertex(pose, x0, y0, z0)
                .setColor(color)
                .setUv(texture.getU(minU), texture.getV(minV))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normalX, normalY, normalZ);
        consumer.addVertex(pose, x1, y0, z1)
                .setColor(color)
                .setUv(texture.getU(maxU), texture.getV(minV))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normalX, normalY, normalZ);
        consumer.addVertex(pose, x1, y1, z2)
                .setColor(color)
                .setUv(texture.getU(maxU), texture.getV(maxV))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normalX, normalY, normalZ);
        consumer.addVertex(pose, x0, y1, z3)
                .setColor(color)
                .setUv(texture.getU(minU), texture.getV(maxV))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normalX, normalY, normalZ);
    }

    public static float[] unpackVertices(int[] vertices, int vertexIndex, int position, int count) {
        float[] floats = new float[count];
        int startIndex = vertexIndex * IQuadTransformer.STRIDE + position;
        for (int i = 0; i < count; i++) {
            floats[i] = Float.intBitsToFloat(vertices[startIndex + i]);
        }
        return floats;
    }

    public static Vector3f getNormalData(int[] vertices, int vertexIndex) {
        float x;
        float y;
        float z;

        int vertexData = vertices[vertexIndex * STRIDE + IQuadTransformer.NORMAL];
        x = (vertexData & 0x000000FF) / 127f;
        y = ((vertexData & 0x0000FF00) >> 8) / 127f;
        z = ((vertexData & 0x00FF0000) >> 16) / 127f;
        return new Vector3f(x, y, z);
    }

    /**
     * {@see net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer}
     * @return packedUV Data
     */
    public static int[] packUV(float u, float v) {
        int[] quadData = new int[2];
        quadData[0] = Float.floatToRawIntBits(u);
        quadData[1] = Float.floatToRawIntBits(v);
        return quadData;
    }

    private static int[] getColorABGR(int[] vertices, int vertexIndex) {
        int color = vertices[STRIDE * vertexIndex + COLOR];
        int[] abgr = new int[4];
        abgr[0] = color >> 24 & 0xFF;
        abgr[1] = color >> 16 & 0xFF;
        abgr[2] = color >> 8 & 0xFF;
        abgr[3] = color & 0xFF;
        return abgr;
    }

    private static int[] multiplyColor(int[] abgr1, int[] abgr2) {
        return new int[] { abgr1[0] * abgr2[0] / 255, abgr1[1] * abgr2[1] / 255, abgr1[2] * abgr2[2] / 255,
                abgr1[3] * abgr2[3] / 255 };
    }

    public static void multiplyColor(int[] vertices, int vertexIndex, int rgbBlockColor) {
        int[] colorABGR = RenderUtil.getColorABGR(vertices, vertexIndex);
        int[] blockColorABGR = new int[4];
        blockColorABGR[0] = 0xFF | (rgbBlockColor >> 24 & 0xFF);
        blockColorABGR[3] = rgbBlockColor >> 16 & 0xFF;
        blockColorABGR[2] = rgbBlockColor >> 8 & 0xFF;
        blockColorABGR[1] = rgbBlockColor & 0xFF;
        int[] multipliedColor = RenderUtil.multiplyColor(colorABGR, blockColorABGR);
        RenderUtil.putColorABGR(vertices, vertexIndex, multipliedColor);
    }

    public static void putColorABGR(int[] vertices, int vertexIndex, int[] abgr) {
        int offset = vertexIndex * STRIDE + COLOR;
        vertices[offset] = (abgr[0] << 24) | (abgr[1] << 16) | (abgr[2] << 8) | abgr[3];
    }

    public static void putColorARGB(int[] vertices, int vertexIndex, int argb) {
        int[] blockColorABGR = new int[4];
        blockColorABGR[0] = 0xFF | (argb >> 24 & 0xFF);
        blockColorABGR[3] = argb >> 16 & 0xFF;
        blockColorABGR[2] = argb >> 8 & 0xFF;
        blockColorABGR[1] = argb & 0xFF;

        int offset = vertexIndex * STRIDE + COLOR;
        vertices[offset] = (blockColorABGR[0] << 24) | (blockColorABGR[1] << 16) | (blockColorABGR[2] << 8)
                | blockColorABGR[3];
    }
}
