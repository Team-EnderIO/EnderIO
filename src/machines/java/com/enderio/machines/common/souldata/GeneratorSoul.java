package com.enderio.machines.common.souldata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GeneratorSoul {

    //TODO add tag support for fluids
    public record SoulData(ResourceLocation entitytype, ResourceLocation fluid, int powerpermb, int tickpermb) implements ISoulData {
        @Override
        public ResourceLocation getKey() {
            return entitytype();
        }
    }

    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance ->
       soulDataInstance.group(ResourceLocation.CODEC.fieldOf("entity").forGetter(SoulData::entitytype),
           ResourceLocation.CODEC.fieldOf("fluid").forGetter(SoulData::fluid),
           Codec.INT.fieldOf("power/mb").forGetter(SoulData::powerpermb),
           Codec.INT.fieldOf("tick/mb").forGetter(SoulData::tickpermb))
           .apply(soulDataInstance, SoulData::new));

    public static final SoulDataReloadListner<SoulData> GENERATOR = new SoulDataReloadListner<>("eio_soul/generator", CODEC);

    @SubscribeEvent
    static void addResource(AddReloadListenerEvent event) {
        event.addListener(GENERATOR);
    }
}
