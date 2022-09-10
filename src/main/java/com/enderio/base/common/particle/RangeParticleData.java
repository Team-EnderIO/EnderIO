package com.enderio.base.common.particle;

import com.enderio.base.common.init.EIOParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public record RangeParticleData(int range) implements ParticleOptions {
    public static final Deserializer<RangeParticleData> DESERIALIZER = new Deserializer<>() {

        @NotNull
        @Override
        public RangeParticleData fromCommand(@NotNull ParticleType<RangeParticleData> type, @NotNull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int range = reader.readInt();
            return new RangeParticleData(range);
        }

        @Override
        public RangeParticleData fromNetwork(ParticleType<RangeParticleData> particleType, FriendlyByteBuf buffer) {
            return new RangeParticleData(buffer.readInt());
        }
    };

    public static Codec<RangeParticleData> CODEC = RecordCodecBuilder.create(
        val -> val.group(Codec.INT.fieldOf("range").forGetter(data -> data.range)).apply(val, RangeParticleData::new));

    @NotNull
    @Override
    public ParticleType<?> getType() {
        return EIOParticles.RANGE_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buffer) {
        buffer.writeInt(range);
    }

    @NotNull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%d", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), range);
    }
}
