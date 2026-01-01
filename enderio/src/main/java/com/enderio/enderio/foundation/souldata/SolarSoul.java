package com.enderio.enderio.foundation.souldata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SolarSoul {

    public record SoulData(Identifier entitytype, boolean daytime, boolean nighttime, Optional<ResourceKey<Level>> level) implements com.enderio.enderio.foundation.souldata.SoulData {

        @Override
        public Identifier getKey() {
            return entitytype;
        }
    }

    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Identifier.CODEC.fieldOf("entity").forGetter(SoulData::entitytype),
            Codec.BOOL.fieldOf("daytime").forGetter(SoulData::daytime),
            Codec.BOOL.fieldOf("nighttime").forGetter(SoulData::nighttime),
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("level").forGetter(SoulData::level)
            ).apply(instance, SoulData::new));

    public static final StreamCodec<ByteBuf, SoulData> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC,
        SoulData::entitytype,
        ByteBufCodecs.BOOL,
        SoulData::daytime,
        ByteBufCodecs.BOOL,
        SoulData::nighttime,
        ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.DIMENSION)),
        SoulData::level,
        SoulData::new
    );

    public static final String NAME = "solar";
    public static final SoulDataReloadListener<SoulData> RELOAD_LISTENER = new SoulDataReloadListener<>(NAME, CODEC);
}
