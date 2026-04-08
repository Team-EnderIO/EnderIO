package com.enderio.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;

public class RenderUtil {
    /**
     * Render a face with its texture with local face coordinates.
     * Note: Up and Down UVs may not be accurate, please PR with a fix (and test all uses of this method) to modify it.
     * todo; is this confusing?
     */
    public static void submitFace(Direction face, PoseStack poseStack, RenderType renderType, SubmitNodeCollector nodeCollector,
            TextureAtlasSprite texture, float x, float y, float z, float w, float h, int color) {
        submitFace(face, poseStack, renderType, nodeCollector, texture, x, y, z, w, h, color, LightCoordsUtil.FULL_BRIGHT);
    }

    public static void submitFace(Direction face, PoseStack poseStack, RenderType renderType, SubmitNodeCollector nodeCollector,
            TextureAtlasSprite texture, float x, float y, float z, float w, float h, int color, int light) {
        // Normals are taken from Direction enum. They are necessary for proper lighting
        // and block breaking textures
        switch (face) {
        case DOWN -> submitFace(poseStack, renderType, nodeCollector, texture, color, light, x, x + w, 1.0f - z, 1.0f - z, y, y, y + h, y + h,
                x, x + w, y, y + h, 0, -1, 0);
        case UP -> submitFace(poseStack, renderType, nodeCollector, texture, color, light, x, x + w, z, z, y + h, y + h, y, y, x, x + w, y,
                y + h, 0, 1, 0);
        case NORTH -> submitFace(poseStack, renderType, nodeCollector, texture, color, light, x, x + w, y + h, y, z, z, z, z, x, x + w, y,
                y + h, 0, 0, -1);
        case SOUTH -> submitFace(poseStack, renderType, nodeCollector, texture, color, light, x, x + w, y, y + h, 1.0f - z, 1.0f - z,
                1.0f - z, 1.0f - z, x + w, x, y + h, y, 0, 0, 1);
        case EAST -> submitFace(poseStack, renderType, nodeCollector, texture, color, light, 1.0f - z, 1.0f - z, y + h, y, x, x + w, x + w, x,
                x, x + w, y, y + h, 1, 0, 0);
        case WEST -> submitFace(poseStack, renderType, nodeCollector, texture, color, light, z, z, y, y + h, x, x + w, x + w, x, x + w, x,
                y + h, y, -1, 0, 0);
        default -> throw new IllegalStateException("Unexpected value: " + face);
        }
    }

    private static void submitFace(PoseStack pose, RenderType renderType, SubmitNodeCollector nodeCollector, TextureAtlasSprite texture, int color,
            int light, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, float minU,
            float maxU, float minV, float maxV, float normalX, float normalY, float normalZ) {

        nodeCollector.submitCustomGeometry(pose, renderType, (pose1, consumer) -> {
            consumer.addVertex(pose1, x0, y0, z0)
                .setColor(color)
                .setUv(texture.getU(minU), texture.getV(minV))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose1, normalX, normalY, normalZ);
            consumer.addVertex(pose1, x1, y0, z1)
                .setColor(color)
                .setUv(texture.getU(maxU), texture.getV(minV))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose1, normalX, normalY, normalZ);
            consumer.addVertex(pose1, x1, y1, z2)
                .setColor(color)
                .setUv(texture.getU(maxU), texture.getV(maxV))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose1, normalX, normalY, normalZ);
            consumer.addVertex(pose1, x0, y1, z3)
                .setColor(color)
                .setUv(texture.getU(minU), texture.getV(maxV))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose1, normalX, normalY, normalZ);
        });
    }
}
