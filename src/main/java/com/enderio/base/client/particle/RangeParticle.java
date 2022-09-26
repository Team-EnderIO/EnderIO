package com.enderio.base.client.particle;

import com.enderio.EnderIO;
import com.enderio.base.common.particle.RangeParticleData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RangeParticle extends TextureSheetParticle {

    private static final int INIT_TIME = 20;
    private static final ResourceLocation WHITE = EnderIO.loc("textures/block/white.png");

    private final int range;

    public RangeParticle(ClientLevel level, Vec3 pos, int range) {
        super(level, pos.x, pos.y, pos.z);
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
    public void render(@NotNull VertexConsumer consumer, Camera renderInfo, float partialTicks) {
        Vec3 position = Vec3.atLowerCornerOf(renderInfo.getBlockPosition());
        renderFace(Direction.UP, consumer, (float) (position.x - range), (float) (position.y - range), (float) (position.z - range), 2 * range, 2 * range);
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void renderFace(Direction face, VertexConsumer consumer, float x, float y, float z, float w, float h) {
        switch (face) {
        case DOWN -> renderFace(consumer, x, x + w, 1.0f - z, 1.0f - z, y, y, y + h, y + h);
        case UP -> renderFace(consumer, x, x + w, z, z, y + h, y + h, y, y);
        case NORTH -> renderFace(consumer, x, x + w, y + h, y, z, z, z, z);
        case SOUTH -> renderFace(consumer, x, x + w, y, y + h, 1.0f - z, 1.0f - z, 1.0f - z, 1.0f - z);
        case WEST -> renderFace(consumer, 1.0f - z, 1.0f - z, y + h, y, x, x + w, x + w, x);
        case EAST -> renderFace(consumer, z, z, y, y + h, x, x + w, x + w, x);
        }
    }

    private void renderFace(VertexConsumer consumer, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3) {
        float minU = getU0();
        float maxU = getU1();
        float minV = getV0();
        float maxV = getV1();
        consumer.vertex(x0, y0, z0).uv(minU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(15728880).endVertex();
        consumer.vertex(x1, y0, z1).uv(maxU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(15728880).endVertex();
        consumer.vertex(x1, y1, z2).uv(maxU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(15728880).endVertex();
        consumer.vertex(x0, y1, z3).uv(minU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(15728880).endVertex();
        //        backwards
        consumer.vertex(x1, y0, z1).uv(maxU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(15728880).endVertex();
        consumer.vertex(x0, y0, z0).uv(minU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(15728880).endVertex();
        consumer.vertex(x0, y1, z3).uv(minU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(15728880).endVertex();
        consumer.vertex(x1, y1, z2).uv(maxU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(15728880).endVertex();
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
