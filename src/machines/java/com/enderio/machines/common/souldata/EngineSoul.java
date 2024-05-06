package com.enderio.machines.common.souldata;

import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class EngineSoul {

    public record SoulData(ResourceLocation entitytype, String fluid, int powerpermb, int tickpermb) implements ISoulData {
        @Override
        public ResourceLocation getKey() {
            return entitytype();
        }
    }

    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance ->
       soulDataInstance.group(
           ResourceLocation.CODEC.fieldOf("entity").forGetter(SoulData::entitytype),
           Codec.STRING.fieldOf("fluid").forGetter(SoulData::fluid),
           Codec.INT.fieldOf("power/mb").forGetter(SoulData::powerpermb),
           Codec.INT.fieldOf("tick/mb").forGetter(SoulData::tickpermb))
           .apply(soulDataInstance, SoulData::new));

    public static StreamCodec<ByteBuf, SoulData> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        SoulData::entitytype,
        ByteBufCodecs.STRING_UTF8,
        SoulData::fluid,
        ByteBufCodecs.INT,
        SoulData::powerpermb,
        ByteBufCodecs.INT,
        SoulData::tickpermb,
        SoulData::new
    );

    public static final String NAME = "engine";
    public static final SoulDataReloadListener<SoulData> ENGINE = new SoulDataReloadListener<>(NAME, CODEC);

    @SubscribeEvent
    public static void addResource(AddReloadListenerEvent event) {
        event.addListener(ENGINE);
    }
}
