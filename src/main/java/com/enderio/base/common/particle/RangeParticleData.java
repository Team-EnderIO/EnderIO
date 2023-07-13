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

import java.util.Locale;

public record RangeParticleData(int range, String color) implements ParticleOptions {
    public static final Deserializer<RangeParticleData> DESERIALIZER = new Deserializer<>() {

        @Override
        public RangeParticleData fromCommand(ParticleType<RangeParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int range = reader.readInt();
            reader.expect(' ');
            String color = reader.readString();
            return new RangeParticleData(range, color);
        }

        @Override
        public RangeParticleData fromNetwork(ParticleType<RangeParticleData> particleType, FriendlyByteBuf buffer) {
            return new RangeParticleData(buffer.readInt(), buffer.readUtf());
        }
    };

    public static Codec<RangeParticleData> CODEC = RecordCodecBuilder.create(val -> val
        .group(Codec.INT.fieldOf("range").forGetter(data -> data.range), Codec.STRING.fieldOf("color").forGetter(data -> data.color))
        .apply(val, RangeParticleData::new));

    @Override
    public ParticleType<?> getType() {
        return EIOParticles.RANGE_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(range);
        buffer.writeUtf(color);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d %s ", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), range, color);
    }
}
