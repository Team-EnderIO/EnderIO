package com.enderio.base.client.particle;

import com.enderio.base.common.particle.RangeParticleData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RangeParticle<T extends BlockEntity> extends TextureSheetParticle {

    private static final int INIT_TIME = 20;

    //    private final Vector4f color;
    private final int range;

    public RangeParticle(ClientLevel level, Vec3 pos, int range) {
        super(level, pos.x, pos.y, pos.z);
        //        this.color = new Vector4f(255, 0, 0, 150);
        this.range = range;
        //        this.lifetime = 20 * 60 * 10; // 10 minutes
        this.lifetime = 5;
        rCol = 1;
        gCol = 0;
        bCol = 0;
        //Note: Vanilla discards pieces from particles that are under the alpha of 0.1, due to floating point differences
        // of float and double if we set this to 0.1F, then it ends up getting discarded, so we just set this to 0.11F
        alpha = 0.11F;
    }

    // TODO reimplement this - refer Mek
    //    @Override
    //    public boolean isAlive() {
    //        return age < lifetime && owner.hasLevel() && !owner.isRemoved() && owner.isShowingRange() && level.getBlockEntity(owner.getBlockPos()) == owner;
    //    }

    @Override
    public void render(VertexConsumer consumer, Camera renderInfo, float partialTicks) {
        Vec3 position = renderInfo.getPosition();

        Vec3 a = position.add(range, range, range);
        Vec3 b = position.add(range, -range, range);
        Vec3 c = position.add(-range, -range, range);
        Vec3 d = position.add(-range, range, range);
        //        BlockPos position = owner.getBlockPos();
        addFace(Direction.UP, consumer, a, b, c, d, getU0(), getU1(), getV0(), getV1());
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    void addFace(Direction face, VertexConsumer consumer, Vec3 a, Vec3 b, Vec3 c, Vec3 d, float uMin, float uMax, float vMin, float vMax) {

        addVertex(consumer, new Vector3f(a), uMax, vMax);
        addVertex(consumer, new Vector3f(b), uMax, vMin);
        addVertex(consumer, new Vector3f(c), uMin, vMin);
        addVertex(consumer, new Vector3f(d), uMin, vMax);
        //backwards
        addVertex(consumer, new Vector3f(b), uMax, vMin);
        addVertex(consumer, new Vector3f(a), uMax, vMax);
        addVertex(consumer, new Vector3f(d), uMin, vMax);
        addVertex(consumer, new Vector3f(c), uMin, vMin);
        //        consumer.vertex(x1, y0, z1).color(color).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).endVertex();
        //        consumer.vertex(x1, y1, z2).color(color).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).endVertex();
        //        consumer.vertex(x0, y1, z3).color(color).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).endVertex();
    }

    void addVertex(VertexConsumer consumer, Vector3f pos, float u, float v) {
        consumer.vertex(pos.x(), pos.y(), pos.z()).uv(u, v).color(rCol, gCol, bCol, alpha).uv2(240, 240).endVertex();
    }

    public static class Provider implements ParticleProvider<RangeParticleData> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(RangeParticleData data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Vec3 pos = new Vec3(x, y, z);
            RangeParticle particle = new RangeParticle(level, pos, data.range());
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
