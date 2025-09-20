package com.enderio.machines.common.blockentity.sync;

import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.io.energy.ILargeMachineEnergyStorage;
import com.enderio.machines.common.io.energy.LargeImmutableMachineEnergyStorage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Deprecated(forRemoval = true, since = "7.1")
public record LargeEnergyData(long energyStored, long maxEnergyStored) {
    public static final Codec<LargeEnergyData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.LONG.fieldOf("EnergyStored").forGetter(LargeEnergyData::energyStored),
            Codec.LONG.fieldOf("MaxEnergyStored").forGetter(LargeEnergyData::maxEnergyStored)
        ).apply(instance, LargeEnergyData::new)
    );

    public static final StreamCodec<ByteBuf, LargeEnergyData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG,
        LargeEnergyData::energyStored,
        ByteBufCodecs.VAR_LONG,
        LargeEnergyData::maxEnergyStored,
        LargeEnergyData::new
    );

    public static LargeEnergyData from(ILargeMachineEnergyStorage storage) {
        return new LargeEnergyData(
            storage.getLargeEnergyStored(),
            storage.getLargeMaxEnergyStored()
        );
    }

    public static final NetworkDataSlot.CodecType<LargeEnergyData> DATA_SLOT_TYPE
        = new NetworkDataSlot.CodecType<>(CODEC, STREAM_CODEC.cast());

    public LargeImmutableMachineEnergyStorage toImmutableStorage() {
        return new LargeImmutableMachineEnergyStorage(energyStored, maxEnergyStored);
    }
}
