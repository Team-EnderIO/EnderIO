package com.enderio.enderio.foundation.souldata;

import com.enderio.enderio.content.machines.powered_spawner.MobSpawnMode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.AddReloadListenerEvent;

/**
 * Class that holds all information related to the mob soul in a spawner
 */
@Mod.EventBusSubscriber
public class SpawnerSoul {

    /**
     * Record that holds the data for the powered spawner spawn task
     * @param entityType entityType resourcelocation of the mob soul (and to spawn)
     * @param power powercost of the spawner
     * @param spawnType way to spawn the mob
     */
    public record SoulData(ResourceLocation entityType, int power, MobSpawnMode spawnType)
            implements com.enderio.enderio.foundation.souldata.SoulData {
        @Override
        public ResourceLocation getKey() {
            return entityType();
        }
    }

    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance -> soulDataInstance
            .group(ResourceLocation.CODEC.fieldOf("entity").forGetter(SoulData::entityType),
                    Codec.INT.fieldOf("power").forGetter(SoulData::power),
                    MobSpawnMode.CODEC.fieldOf("type").forGetter(SoulData::spawnType))
            .apply(soulDataInstance, SoulData::new));

    public static final StreamCodec<ByteBuf, SoulData> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC,
            SoulData::entityType, ByteBufCodecs.INT, SoulData::power, MobSpawnMode.STREAM_CODEC, SoulData::spawnType,
            SoulData::new);

    public static final String NAME = "spawner";

    // SoulData Manger for the spawner data
    public static final SoulDataReloadListener<SoulData> SPAWNER = new SoulDataReloadListener<>(NAME, CODEC);

    @SubscribeEvent
    static void addResource(AddReloadListenerEvent event) {
        event.addListener(SPAWNER);
    }
}
