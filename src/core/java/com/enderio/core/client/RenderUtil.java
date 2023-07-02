package com.enderio.core.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.IQuadTransformer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static net.minecraftforge.client.model.IQuadTransformer.COLOR;
import static net.minecraftforge.client.model.IQuadTransformer.STRIDE;

public class RenderUtil {
    /**
     * Render a face with its texture with local face coordinates.
     * Note: Up and Down UVs may not be accurate, please PR with a fix (and test all uses of this method) to modify it.
     * todo; is this confusing?
     */
    public static void renderFace(Direction face, Matrix4f pose, Matrix3f normal, VertexConsumer consumer, TextureAtlasSprite texture, float x, float y, float z, float w, float h, int color) {
        renderFace(face, pose, normal, consumer, texture, x, y, z, w, h, color, LightTexture.FULL_BRIGHT);
    }
    public static void renderFace(Direction face, Matrix4f pose, Matrix3f normal, VertexConsumer consumer, TextureAtlasSprite texture, float x, float y, float z, float w, float h, int color, int light) {
        switch (face) {
            case DOWN -> renderFace(pose, normal, consumer, texture, color, light, x, x + w, 1.0f - z, 1.0f - z, y, y, y + h, y + h, x, x + w, y, y + h);
            case UP -> renderFace(pose, normal, consumer, texture, color, light, x, x + w, z, z, y + h, y + h, y, y, x, x + w, y, y + h);
            case NORTH -> renderFace(pose, normal, consumer, texture, color, light, x, x + w, y + h, y, z, z, z, z, x, x + w, y, y + h);
            case SOUTH -> renderFace(pose, normal, consumer, texture, color, light, x, x + w, y, y + h, 1.0f - z, 1.0f - z, 1.0f - z, 1.0f - z, x + w, x, y + h, y);
            case EAST -> renderFace(pose, normal, consumer, texture, color, light, 1.0f - z, 1.0f - z, y + h, y, x, x + w, x + w, x, x, x + w, y, y + h);
            case WEST -> renderFace(pose, normal, consumer, texture, color, light, z, z, y, y + h, x, x + w, x + w, x, x + w, x, y + h, y);
        }
    }

    private static void renderFace(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, TextureAtlasSprite texture, int color, int light, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, float u0, float u1, float v0, float v1) {
        float minU = u0 * texture.contents().width();
        float maxU = u1 * texture.contents().width();
        float minV = v0 * texture.contents().height();
        float maxV = v1 * texture.contents().height();

        consumer.vertex(pose, x0, y0, z0).color(color).uv(texture.getU(minU), texture.getV(minV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
        consumer.vertex(pose, x1, y0, z1).color(color).uv(texture.getU(maxU), texture.getV(minV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
        consumer.vertex(pose, x1, y1, z2).color(color).uv(texture.getU(maxU), texture.getV(maxV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
        consumer.vertex(pose, x0, y1, z3).color(color).uv(texture.getU(minU), texture.getV(maxV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
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
        float x,y,z;
        int vertexData = vertices[vertexIndex * STRIDE + IQuadTransformer.NORMAL];
        x = (vertexData & 0x000000FF) / 127f;
        y = ((vertexData & 0x0000FF00) >> 8) / 127f;
        z = ((vertexData & 0x00FF0000) >> 16) / 127f;
        return new Vector3f(x,y,z);
    }

    /**
     * {@see net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer}
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
        return new int[] {
            abgr1[0]*abgr2[0]/255,
            abgr1[1]*abgr2[1]/255,
            abgr1[2]*abgr2[2]/255,
            abgr1[3]*abgr2[3]/255};
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
        vertices[offset] = (abgr[0] << 24) |
            (abgr[1] << 16) |
            (abgr[2] << 8) |
            abgr[3];
    }
    public static void putColorARGB(int[] vertices, int vertexIndex, int argb) {
        int[] blockColorABGR = new int[4];
        blockColorABGR[0] = 0xFF | (argb >> 24 & 0xFF);
        blockColorABGR[3] = argb >> 16 & 0xFF;
        blockColorABGR[2] = argb >> 8 & 0xFF;
        blockColorABGR[1] = argb & 0xFF;

        int offset = vertexIndex * STRIDE + COLOR;
        vertices[offset] = (blockColorABGR[0] << 24) |
            (blockColorABGR[1] << 16) |
            (blockColorABGR[2] << 8) |
            blockColorABGR[3];
    }
}
