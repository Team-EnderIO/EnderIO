package com.enderio.conduits.common.components;

import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record ExtractionSpeedUpgrade(int tier) implements ConduitUpgrade {
    public static final Codec<ExtractionSpeedUpgrade> CODEC = RecordCodecBuilder.create(
        inst -> inst.group(ExtraCodecs.POSITIVE_INT.fieldOf("tier").forGetter(ExtractionSpeedUpgrade::tier)).apply(inst, ExtractionSpeedUpgrade::new));

    public static final StreamCodec<ByteBuf, ExtractionSpeedUpgrade> STREAM_CODEC =
        ByteBufCodecs.VAR_INT.map(ExtractionSpeedUpgrade::new, ExtractionSpeedUpgrade::tier);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExtractionSpeedUpgrade that = (ExtractionSpeedUpgrade) o;

        return tier == that.tier;
    }
}
