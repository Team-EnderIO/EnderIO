package com.enderio.machines.common.souldata;

import com.enderio.machines.EnderIOMachines;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID)
public class FarmSoul {

    public record SoulData(ResourceLocation entitytype, float bonemeal, int seeds, float power)
            implements com.enderio.machines.common.souldata.SoulData {
        @Override
        public ResourceLocation getKey() {
            return entitytype();
        }
    }

    public static final Codec<FarmSoul.SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance -> soulDataInstance
            .group(ResourceLocation.CODEC.fieldOf("entity").forGetter(FarmSoul.SoulData::entitytype),
                    Codec.FLOAT.optionalFieldOf("bonemeal", 1f).forGetter(FarmSoul.SoulData::bonemeal),
                    Codec.INT.optionalFieldOf("seeds", 0).forGetter(FarmSoul.SoulData::seeds),
                    Codec.FLOAT.optionalFieldOf("power", 1f).forGetter(FarmSoul.SoulData::power))
            .apply(soulDataInstance, FarmSoul.SoulData::new));

    public static final StreamCodec<ByteBuf, FarmSoul.SoulData> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, FarmSoul.SoulData::entitytype, ByteBufCodecs.FLOAT,
            FarmSoul.SoulData::bonemeal, ByteBufCodecs.INT, FarmSoul.SoulData::seeds, ByteBufCodecs.FLOAT,
            FarmSoul.SoulData::power, FarmSoul.SoulData::new);

    public static final String NAME = "farm";
    public static final SoulDataReloadListener<FarmSoul.SoulData> FARM = new SoulDataReloadListener<>(NAME, CODEC);

    @SubscribeEvent
    public static void addResource(AddReloadListenerEvent event) {
        event.addListener(FARM);
    }
}
