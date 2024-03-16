package com.enderio.machines.common.souldata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FarmSoul {

    public record SoulData(ResourceLocation entitytype, float bonemeal, float seeds, float power) implements ISoulData {
        @Override
        public ResourceLocation getKey() {
            return entitytype();
        }
    }

    public static final Codec<FarmSoul.SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance ->
        soulDataInstance.group(ResourceLocation.CODEC.fieldOf("entity").forGetter(FarmSoul.SoulData::entitytype),
            Codec.FLOAT.optionalFieldOf("bonemeal", 1f).forGetter(FarmSoul.SoulData::bonemeal),
            Codec.FLOAT.optionalFieldOf("seeds", 1f).forGetter(FarmSoul.SoulData::seeds),
            Codec.FLOAT.optionalFieldOf("power", 1f).forGetter(FarmSoul.SoulData::power))
            .apply(soulDataInstance, FarmSoul.SoulData::new));

    public static final String NAME = "farm";
    public static final SoulDataReloadListener<FarmSoul.SoulData> FARM = new SoulDataReloadListener<>(NAME, CODEC);

}
