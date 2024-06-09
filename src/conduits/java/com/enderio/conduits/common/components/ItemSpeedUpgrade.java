package com.enderio.conduits.common.components;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public class ItemSpeedUpgrade implements ConduitUpgrade {
    public static final Codec<ItemSpeedUpgrade> CODEC = RecordCodecBuilder.create(
        inst -> inst.group(ExtraCodecs.POSITIVE_INT.fieldOf("speed").forGetter(ItemSpeedUpgrade::getSpeed)).apply(inst, ItemSpeedUpgrade::new));

    public static final StreamCodec<ByteBuf, ItemSpeedUpgrade> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(ItemSpeedUpgrade::new, ItemSpeedUpgrade::getSpeed);

    private final int speed;

    public ItemSpeedUpgrade(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemSpeedUpgrade that = (ItemSpeedUpgrade) o;

        return speed == that.speed;
    }

    @Override
    public boolean canApplyTo(ConduitType<?> type) {
        return type == EIOConduitTypes.Types.ITEM.get();
    }

    @Override
    public int hashCode() {
        return speed;
    }
}
