package com.enderio.conduits.common.components;

import com.enderio.api.capability.IConduitUpgrade;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public class FluidSpeedUpgrade implements IConduitUpgrade {
    public static final Codec<FluidSpeedUpgrade> CODEC = RecordCodecBuilder.create(
        inst -> inst.group(ExtraCodecs.POSITIVE_INT.fieldOf("speed").forGetter(FluidSpeedUpgrade::getSpeed)).apply(inst, FluidSpeedUpgrade::new));

    public static final StreamCodec<ByteBuf, FluidSpeedUpgrade> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(FluidSpeedUpgrade::new, FluidSpeedUpgrade::getSpeed);

    private final int speed;

    public FluidSpeedUpgrade(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FluidSpeedUpgrade that = (FluidSpeedUpgrade) o;

        return speed == that.speed;
    }

    @Override
    public int hashCode() {
        return speed;
    }
}
