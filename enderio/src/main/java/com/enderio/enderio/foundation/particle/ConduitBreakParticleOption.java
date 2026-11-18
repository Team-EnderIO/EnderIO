package com.enderio.enderio.foundation.particle;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.init.EIOParticles;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConduitBreakParticleOption(BlockPos pos, Holder<Conduit<?,?>> conduit) implements ParticleOptions {

    public static final MapCodec<ConduitBreakParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(ConduitBreakParticleOption::pos),
                Conduit.CODEC.fieldOf("conduit").forGetter(ConduitBreakParticleOption::conduit))
            .apply(instance, ConduitBreakParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConduitBreakParticleOption> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ConduitBreakParticleOption::pos,
        Conduit.STREAM_CODEC,
        ConduitBreakParticleOption::conduit,
        ConduitBreakParticleOption::new
    );

    @Override
    public ParticleType<ConduitBreakParticleOption> getType() {
        return EIOParticles.CONDUIT_BREAK.get();
    }
}
