package com.enderio.enderio.client.foundation.particle;

import com.enderio.enderio.foundation.particle.RangeParticleData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class RangeParticle extends SingleQuadParticle {

    private final int range;
    private final float offset = 0.01f;
    private final int color;

    public RangeParticle(ClientLevel level, Vec3 pos, int range, String color, TextureAtlasSprite sprite) {
        super(level, pos.x, pos.y, pos.z, sprite);
        this.range = range;
        this.lifetime = 5;
        this.color = Integer.parseInt(color);
        this.rCol = (float)Integer.parseInt(color.substring(0,2), 16) / 255;
        this.gCol = (float)Integer.parseInt(color.substring(2,4), 16) / 255;
        this.bCol = (float)Integer.parseInt(color.substring(4,6), 16) / 255;
        //Note: Vanilla discards pieces from particles that are under the alpha of 0.1, due to floating point differences
        // of float and double if we set this to 0.1F, then it ends up getting discarded, so we just set this to 0.11F
        alpha = 0.11F;
        double bb_offset = 0.5;
        setBoundingBox(
            new AABB(pos.x - range - bb_offset, pos.y - range - bb_offset, pos.z - range - bb_offset, pos.x + range + bb_offset, pos.y + range + bb_offset,
                pos.z + range + bb_offset));
    }

    //TODO remap to the correct size
    @Override
    protected void extractRotatedQuad(QuadParticleRenderState reusedState, Quaternionf orientation, float x, float y, float z, float partialTick) {

        reusedState.add(this.getLayer(), x, y, z, orientation.x, orientation.y, orientation.z, orientation.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), color, this.getLightColor(partialTick));
    }

    @Override
    public int getLifetime() {
        return age + 10;
    }

    //    @Override
//    public void render(@NotNull VertexConsumer consumer, Camera renderInfo, float partialTicks) {
//        Vec3 position = renderInfo.getPosition();
//        float mappedX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - position.x());
//        float mappedY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - position.y());
//        float mappedZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - position.z());
//
//        // Top face requires different z for some reason
//        Vector3f vec = new Vector3f(-range - offset, -range - offset, range + 1 + offset);
//        renderFace(consumer, remapPosition(calcPoints(Direction.UP, vec), mappedX, mappedY, mappedZ));
//        vec = new Vector3f(-range - offset, -range - offset, -range - offset);
//        renderFace(consumer, remapPosition(calcPoints(Direction.SOUTH, vec), mappedX, mappedY, mappedZ));
//        renderFace(consumer, remapPosition(calcPoints(Direction.EAST, vec), mappedX, mappedY, mappedZ));
//        renderFace(consumer, remapPosition(calcPoints(Direction.UP, vec), mappedX, mappedY, mappedZ));
//        renderFace(consumer, remapPosition(calcPoints(Direction.NORTH, vec), mappedX, mappedY, mappedZ));
//        renderFace(consumer, remapPosition(calcPoints(Direction.WEST, vec), mappedX, mappedY, mappedZ));
//    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public Vector3f[] calcPoints(Direction face, Vector3f vec) {
        return calcPoints(face, vec.x(), vec.y(), vec.z(), 2 * range + 1 + (offset * 2), 2 * range + 1 + (offset * 2));
    }

    public Vector3f[] calcPoints(Direction face, float x, float y, float z, float w, float h) {
        return switch (face) {
            case DOWN -> calcCoordinates(x, x + w, 1.0f - z, 1.0f - z, y, y, y + h, y + h);
            case UP -> calcCoordinates(x, x + w, z, z, y + h, y + h, y, y);
            case NORTH -> calcCoordinates(x, x + w, y + h, y, z, z, z, z);
            case SOUTH -> calcCoordinates(x, x + w, y, y + h, 1.0f - z, 1.0f - z, 1.0f - z, 1.0f - z);
            case WEST -> calcCoordinates(1.0f - z, 1.0f - z, y + h, y, x, x + w, x + w, x);
            case EAST -> calcCoordinates(z, z, y, y + h, x, x + w, x + w, x);
        };
    }

    private Vector3f[] remapPosition(Vector3f[] coords, float mappedX, float mappedY, float mappedZ) {
        for (Vector3f vec : coords) {
            vec.add(mappedX, mappedY, mappedZ);
        }
        return coords;
    }

    private Vector3f[] calcCoordinates(float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3) {
        return new Vector3f[] { new Vector3f(x0, y0, z0), new Vector3f(x1, y0, z1), new Vector3f(x1, y1, z2), new Vector3f(x0, y1, z3) };
    }

    private void renderFace(VertexConsumer consumer, Vector3f[] coords) {
        float minU = getU0();
        float maxU = getU1();
        float minV = getV0();
        float maxV = getV1();
        addVertex(consumer, coords[0], minU, minV);
        addVertex(consumer, coords[1], maxU, minV);
        addVertex(consumer, coords[2], maxU, maxV);
        addVertex(consumer, coords[3], minU, maxV);
        //        backwards
        addVertex(consumer, coords[1], maxU, minV);
        addVertex(consumer, coords[0], minU, minV);
        addVertex(consumer, coords[3], minU, maxV);
        addVertex(consumer, coords[2], maxU, maxV);
    }

    private void addVertex(VertexConsumer consumer, Vector3f pos, float u, float v) {
        consumer.addVertex(pos.x(), pos.y(), pos.z()).setUv(u, v).setColor(rCol, gCol, bCol, alpha).setUv2(240, 240);
    }

    public static class Provider implements ParticleProvider.Sprite<RangeParticleData> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable SingleQuadParticle createParticle(RangeParticleData data, ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            Vec3 pos = new Vec3(x, y, z);
            RangeParticle particle = new RangeParticle(level, pos, data.range(), data.color(), this.spriteSet.get(random));
            return particle;
        }

    }
}
