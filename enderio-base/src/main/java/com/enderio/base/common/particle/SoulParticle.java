package com.enderio.base.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulParticle extends TextureSheetParticle {
    SoulParticleData data;
    SoulParticleData.BezierCurve posFunction;
    private final SpriteSet sprites;
    protected SoulParticle(ClientLevel level, double x, double y, double z, SoulParticleData data, SpriteSet set) {
        super(level, x, y, z);
        this.data = data;
        this.posFunction = this.data.createPosFunction(level);
        setLifetime(SoulParticleData.LIFETIME);
        Vec3 pos = posFunction.calcPos(0);
        this.setPos(pos.x, pos.y, pos.z);
        xo = pos.x;
        yo = pos.y;
        zo = pos.z;
        sprites = set;
        this.scale(1.5F);
        this.setSpriteFromAge(set);
    }




    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 pos = posFunction.calcPos((float)age/SoulParticleData.LIFETIME);
        setPos(pos.x, pos.y, pos.z);
        setParticleSpeed(0,0,0);
        this.setSpriteFromAge(this.sprites);
    }


    public static class Provider implements ParticleProvider<SoulParticleData> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SoulParticleData data, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed) {
            return new SoulParticle(level, x,y,z, data, spriteSet);
        }
    }
}
