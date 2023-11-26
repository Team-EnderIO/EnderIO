package com.enderio.machines.common.souldata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EngineSoul {

    public record SoulData(ResourceLocation entitytype, String fluid, int powerpermb, int tickpermb) implements ISoulData {
        @Override
        public ResourceLocation getKey() {
            return entitytype();
        }
    }

    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance ->
       soulDataInstance.group(ResourceLocation.CODEC.fieldOf("entity").forGetter(SoulData::entitytype),
           Codec.STRING.fieldOf("fluid").forGetter(SoulData::fluid),
           Codec.INT.fieldOf("power/mb").forGetter(SoulData::powerpermb),
           Codec.INT.fieldOf("tick/mb").forGetter(SoulData::tickpermb))
           .apply(soulDataInstance, SoulData::new));

    public static final String NAME = "engine";
    public static final SoulDataReloadListener<SoulData> ENGINE = new SoulDataReloadListener<>(NAME, CODEC);

    @SubscribeEvent
    static void addResource(AddReloadListenerEvent event) {
        event.addListener(ENGINE);
    }
}
