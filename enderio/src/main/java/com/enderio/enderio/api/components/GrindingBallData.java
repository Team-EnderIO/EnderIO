package com.enderio.enderio.api.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GrindingBallData(float outputMultiplier, float bonusMultiplier, float powerUse, int durability) {

    public static final Codec<GrindingBallData> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(Codec.FLOAT.fieldOf("OutputMultiplier").forGetter(GrindingBallData::outputMultiplier),
            Codec.FLOAT.fieldOf("BonusMultiplier").forGetter(GrindingBallData::bonusMultiplier),
            Codec.FLOAT.fieldOf("PowerUse").forGetter(GrindingBallData::powerUse), Codec.INT.fieldOf("Durability").forGetter(GrindingBallData::durability))
        .apply(instance, GrindingBallData::new));

    public static final StreamCodec<ByteBuf, GrindingBallData> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, GrindingBallData::outputMultiplier,
        ByteBufCodecs.FLOAT, GrindingBallData::bonusMultiplier, ByteBufCodecs.FLOAT, GrindingBallData::powerUse, ByteBufCodecs.INT,
        GrindingBallData::durability, GrindingBallData::new);

    public static final GrindingBallData IDENTITY = new GrindingBallData(1.0f, 1.0f, 1.0f, 0);

    public boolean isIdentity() {
        return this.equals(IDENTITY);
    }
}
