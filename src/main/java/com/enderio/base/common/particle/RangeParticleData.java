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

public record RangeParticleData(int range, float rCol, float gCol, float bCol) implements ParticleOptions {
    public static final Deserializer<RangeParticleData> DESERIALIZER = new Deserializer<>() {

        @NotNull
        @Override
        public RangeParticleData fromCommand(@NotNull ParticleType<RangeParticleData> type, @NotNull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int range = reader.readInt();
            reader.expect(' ');
            float rCol = reader.readFloat();
            reader.expect(' ');
            float gCol = reader.readFloat();
            reader.expect(' ');
            float bCol = reader.readFloat();
            return new RangeParticleData(range, rCol, gCol, bCol);
        }

        @Override
        @NotNull
        public RangeParticleData fromNetwork(@NotNull ParticleType<RangeParticleData> particleType, FriendlyByteBuf buffer) {
            return new RangeParticleData(buffer.readInt(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };

    public static Codec<RangeParticleData> CODEC = RecordCodecBuilder.create(val -> val
        .group(Codec.INT.fieldOf("range").forGetter(data -> data.range), Codec.FLOAT.fieldOf("rCol").forGetter(data -> data.rCol),
            Codec.FLOAT.fieldOf("gCol").forGetter(data -> data.gCol), Codec.FLOAT.fieldOf("bCol").forGetter(data -> data.bCol))
        .apply(val, RangeParticleData::new));

    @NotNull
    @Override
    public ParticleType<?> getType() {
        return EIOParticles.RANGE_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buffer) {
        buffer.writeInt(range);
        buffer.writeFloat(rCol);
        buffer.writeFloat(gCol);
        buffer.writeFloat(bCol);
    }

    @NotNull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%d %f %f %f ", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), range, rCol, gCol, bCol);
    }
}
