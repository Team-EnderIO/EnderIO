package com.enderio.enderio.foundation.particle;

import com.enderio.enderio.init.EIOParticles;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ConduitBreakParticleOption(BlockPos pos, ResourceLocation texture) implements ParticleOptions {

    public static final MapCodec<ConduitBreakParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(ConduitBreakParticleOption::pos),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ConduitBreakParticleOption::texture))
            .apply(instance, ConduitBreakParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConduitBreakParticleOption> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ConduitBreakParticleOption::pos,
        ResourceLocation.STREAM_CODEC,
        ConduitBreakParticleOption::texture,
        ConduitBreakParticleOption::new
    );

    @Override
    public ParticleType<ConduitBreakParticleOption> getType() {
        return EIOParticles.CONDUIT_BREAK.get();
    }
}
