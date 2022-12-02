package com.enderio.base.client.particle;

import com.enderio.base.common.particle.RangeParticleData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RangeParticle extends TextureSheetParticle {

    private final int range;

    public RangeParticle(ClientLevel level, Vec3 pos, int range) {
        super(level, pos.x, pos.y, pos.z);
        this.range = range;
        this.lifetime = 5;
        rCol = 1;
        gCol = 0;
        bCol = 0;
        //Note: Vanilla discards pieces from particles that are under the alpha of 0.1, due to floating point differences
        // of float and double if we set this to 0.1F, then it ends up getting discarded, so we just set this to 0.11F
        alpha = 0.11F;

        setBoundingBox(new AABB(pos.x - range, pos.y - range, pos.z - range, pos.x + range, pos.y + range, pos.z + range));
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, Camera renderInfo, float partialTicks) {
        Vec3 position = renderInfo.getPosition();
        var mappedX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - position.x());
        var mappedY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - position.y());
        var mappedZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - position.z());

        // Top face requires different z for some reason
        var vec = new Vector3f(-range, -range, range + 1);
        renderFace(consumer, remapPosition(calcPoints(Direction.UP, vec), mappedX, mappedY, mappedZ));
        vec = new Vector3f(-range, -range, -range);
        renderFace(consumer, remapPosition(calcPoints(Direction.SOUTH, vec), mappedX, mappedY, mappedZ));
        renderFace(consumer, remapPosition(calcPoints(Direction.EAST, vec), mappedX, mappedY, mappedZ));
        renderFace(consumer, remapPosition(calcPoints(Direction.UP, vec), mappedX, mappedY, mappedZ));
        renderFace(consumer, remapPosition(calcPoints(Direction.NORTH, vec), mappedX, mappedY, mappedZ));
        renderFace(consumer, remapPosition(calcPoints(Direction.WEST, vec), mappedX, mappedY, mappedZ));
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public Vector3f[] calcPoints(Direction face, Vector3f vec) {
        return calcPoints(face, vec.x(), vec.y(), vec.z(), 2 * range + 1, 2 * range + 1);
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
        consumer.vertex(pos.x(), pos.y(), pos.z()).uv(u, v).color(rCol, gCol, bCol, alpha).uv2(240, 240).endVertex();
    }

    public static class Provider implements ParticleProvider<RangeParticleData> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(RangeParticleData data, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed) {
            Vec3 pos = new Vec3(x, y, z);
            RangeParticle particle = new RangeParticle(level, pos, data.range());
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
