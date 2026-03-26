package com.enderio.enderio.foundation.souldata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class FarmSoul {

    public record SoulData(Identifier entitytype, float bonemeal, int seeds, float power)
            implements com.enderio.enderio.foundation.souldata.SoulData {
        @Override
        public Identifier getKey() {
            return entitytype();
        }
    }

    public static final Codec<FarmSoul.SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance -> soulDataInstance
            .group(Identifier.CODEC.fieldOf("entity").forGetter(FarmSoul.SoulData::entitytype),
                    Codec.FLOAT.optionalFieldOf("bonemeal", 1f).forGetter(FarmSoul.SoulData::bonemeal),
                    Codec.INT.optionalFieldOf("seeds", 0).forGetter(FarmSoul.SoulData::seeds),
                    Codec.FLOAT.optionalFieldOf("power", 1f).forGetter(FarmSoul.SoulData::power))
            .apply(soulDataInstance, FarmSoul.SoulData::new));

    public static final StreamCodec<ByteBuf, FarmSoul.SoulData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, FarmSoul.SoulData::entitytype, ByteBufCodecs.FLOAT,
            FarmSoul.SoulData::bonemeal, ByteBufCodecs.INT, FarmSoul.SoulData::seeds, ByteBufCodecs.FLOAT,
            FarmSoul.SoulData::power, FarmSoul.SoulData::new);

    public static final String NAME = "farm";
    public static final SoulDataReloadListener<FarmSoul.SoulData> RELOAD_LISTENER = new SoulDataReloadListener<>(CODEC, NAME);
}
