package com.enderio.enderio.client.foundation.particle;

import com.enderio.enderio.foundation.particle.RangeParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

//TODO get a neo patch to always render the particle, or go custom particle
public class RangeParticle extends SingleQuadParticle {

    private final int range;

    public RangeParticle(ClientLevel level, Vec3 pos, int range, String color, TextureAtlasSprite sprite) {
        super(level, pos.x, pos.y, pos.z, sprite);
        this.range = range;
        this.lifetime = 5;
        this.rCol = (float)Integer.parseInt(color.substring(0,2), 16) / 255;
        this.gCol = (float)Integer.parseInt(color.substring(2,4), 16) / 255;
        this.bCol = (float)Integer.parseInt(color.substring(4,6), 16) / 255;
        this.hasPhysics = false;
        //Note: Vanilla discards pieces from particles that are under the alpha of 0.1, due to floating point differences
        // of float and double if we set this to 0.1F, then it ends up getting discarded, so we just set this to 0.11F
        alpha = 0.11F;
        double bb_offset = 0.5;
        setBoundingBox(
            new AABB(pos.x - range - bb_offset, pos.y - range - bb_offset, pos.z - range - bb_offset, pos.x + range + bb_offset, pos.y + range + bb_offset,
                pos.z + range + bb_offset));
    }

    @Override
    protected void extractRotatedQuad(QuadParticleRenderState reusedState, Quaternionf orientation, float x, float y, float z, float partialTick) {

        var quad = new Quaternionf(0,0,0,1);
        var offsetPos = getOffset(Direction.SOUTH).add(x, y, z);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));

        quad = new Quaternionf(0,1,0,0);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));


        quad = new Quaternionf(0,0,0,1);
        offsetPos = getOffset(Direction.NORTH).add(x, y, z);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));

        quad = new Quaternionf(0,1,0,0);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));


        quad = new Quaternionf(0,-0.7071,0.7071,0);
        offsetPos = getOffset(Direction.DOWN).add(x, y, z);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));

        quad = new Quaternionf(0,-0.7071,-0.7071,0);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));


        quad = new Quaternionf(0,-0.7071,0.7071,0);
        offsetPos = getOffset(Direction.UP).add(x, y, z);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));

        quad = new Quaternionf(0,-0.7071,-0.7071,0);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));


        quad = new Quaternionf( 0.7071,0,0.7071,0);
        offsetPos = getOffset(Direction.EAST).add(x, y, z);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));

        quad = new Quaternionf(0.7071,0,-0.7071, 0);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));


        quad = new Quaternionf( 0.7071,0,0.7071,0);
        offsetPos = getOffset(Direction.WEST).add(x, y, z);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));

        quad = new Quaternionf(0.7071,0,-0.7071, 0);
        reusedState.add(this.getLayer(), offsetPos.x, offsetPos.y, offsetPos.z, quad.x, quad.y, quad.z, quad.w, this.getQuadSize(partialTick),
            this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(alpha, rCol, gCol, bCol), this.getLightColor(partialTick));



    }

    public Vector3f getOffset(Direction direction) {
        return switch (direction) {
            case DOWN -> new Vector3f(0.5f, -range, 0.5f);
            case UP -> new Vector3f(0.5f, range + 1, 0.5f);
            case EAST -> new Vector3f(-range, 0.5f, 0.5f);
            case WEST -> new Vector3f(range +1, 0.5f, 0.5f);
            case NORTH -> new Vector3f(0.5f, 0.5f, -range);
            case SOUTH -> new Vector3f(0.5f, 0.5f, 1 + range);
        };
    }

//    @Override
//    public int getLifetime() {
//        return age + 10;
//    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return range + 0.5f;
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

    public static class Provider implements ParticleProvider<RangeParticleData> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable SingleQuadParticle createParticle(RangeParticleData data, ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            Vec3 pos = new Vec3(x, y, z);
            return new RangeParticle(level, pos, data.range(), data.color(), this.spriteSet.get(random));
        }

    }
}
