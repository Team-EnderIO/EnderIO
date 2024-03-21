package com.enderio.machines.common.souldata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FarmSoul {

    public record SoulData(ResourceLocation entitytype, float bonemeal, int seeds, float power) implements ISoulData {
        @Override
        public ResourceLocation getKey() {
            return entitytype();
        }
    }

    public static final Codec<FarmSoul.SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance ->
        soulDataInstance.group(ResourceLocation.CODEC.fieldOf("entity").forGetter(FarmSoul.SoulData::entitytype),
            Codec.FLOAT.optionalFieldOf("bonemeal", 1f).forGetter(FarmSoul.SoulData::bonemeal),
            Codec.INT.optionalFieldOf("seeds", 0).forGetter(FarmSoul.SoulData::seeds),
            Codec.FLOAT.optionalFieldOf("power", 1f).forGetter(FarmSoul.SoulData::power))
            .apply(soulDataInstance, FarmSoul.SoulData::new));

    public static final String NAME = "farm";
    public static final SoulDataReloadListener<FarmSoul.SoulData> FARM = new SoulDataReloadListener<>(NAME, CODEC);

    @SubscribeEvent
    public static void addResource(AddReloadListenerEvent event) {
        event.addListener(FARM);
    }
}
