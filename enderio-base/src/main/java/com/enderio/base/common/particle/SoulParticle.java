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
import org.jetbrains.annotations.Range;
import org.joml.SimplexNoise;

import java.util.Random;

public class SoulParticle extends TextureSheetParticle {
    SoulParticleData.BezierCurve posFunction;
    private final SpriteSet sprites;
    private static final Random r = new Random();
    int randomOffset = r.nextInt(32*1024);

    private static final float EASING_TIME = 10;

    protected SoulParticle(ClientLevel level, double x, double y, double z, SoulParticleData data, SpriteSet set) {
        super(level, x, y, z);
        this.posFunction = data.createPosFunction(level);
        setLifetime(SoulParticleData.LIFETIME);
        Vec3 pos = applyNoise(posFunction.calcPos(0));
        
        this.setPos(pos.x, pos.y, pos.z);
        xo = pos.x;
        yo = pos.y;
        zo = pos.z;
        sprites = set;
        this.scale(1.5F);
        this.setSpriteFromAge(set);
    }

    Vec3 applyNoise(Vec3 pos) {
        final float scaleNoiseInput = 0.1f;
        final float scaleNoiseOutput = 0.4f;
        float noiseX = SimplexNoise.noise(((float)pos.x*scaleNoiseInput) + randomOffset, ((float)pos.y*scaleNoiseInput), ((float)pos.z*scaleNoiseInput));
        float noiseY = SimplexNoise.noise(((float)pos.x*scaleNoiseInput), ((float)pos.y*scaleNoiseInput) + randomOffset, ((float)pos.z*scaleNoiseInput));
        float noiseZ = SimplexNoise.noise(((float)pos.x*scaleNoiseInput), ((float)pos.y*scaleNoiseInput), ((float)pos.z*scaleNoiseInput) + randomOffset);
        Vec3 noiseOffset = new Vec3(noiseX, noiseY, noiseZ).scale(scaleNoiseOutput);
        //lower noise effect at the beginning/end of particle lifetime
        float pullEndScale;
        if (age <= EASING_TIME) {
            pullEndScale = easeInOutQuad(age/EASING_TIME);
        } else if (age >= getLifetime() -EASING_TIME) {
            pullEndScale = easeInOutQuad((getLifetime()-age) / EASING_TIME);
        } else {
            pullEndScale = 1;
        }
        return pos.add(noiseOffset.scale(pullEndScale));
    }

    private static float easeInOutQuad(@Range(from = 0, to = 1) float progress) {
        return (float)(progress < 0.5 ? 2 * progress * progress : 1 - Math.pow(-2 * progress + 2, 2) / 2);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 pos = applyNoise(posFunction.calcPos((float)age/SoulParticleData.LIFETIME));
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
