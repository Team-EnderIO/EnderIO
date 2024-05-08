package com.enderio.machines.common.souldata;

import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

/**
 * Class that holds all information related to the mob soul in a spawner
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class SpawnerSoul {

    /**
     * Record that holds the data for the powered spawner spawn task
     * @param entityType entityType resourcelocation of the mob soul (and to spawn)
     * @param power powercost of the spawner
     * @param spawnType way to spawn the mob
     */
    public record SoulData(ResourceLocation entityType, int power, SpawnerMachineTask.SpawnType spawnType) implements
        com.enderio.machines.common.souldata.SoulData {
        @Override
        public ResourceLocation getKey() {
            return entityType();
        }
    }

    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance ->
        soulDataInstance.group(ResourceLocation.CODEC.fieldOf("entity").forGetter(SpawnerSoul.SoulData::entityType),
            Codec.INT.fieldOf("power").forGetter(SpawnerSoul.SoulData::power),
            Codec.STRING.comapFlatMap(SpawnerMachineTask.SpawnType::byName, SpawnerMachineTask.SpawnType::getName).stable().fieldOf("type").forGetter(
                SpawnerSoul.SoulData::spawnType))
            .apply(soulDataInstance, SpawnerSoul.SoulData::new));

    public static StreamCodec<ByteBuf, SoulData> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        SpawnerSoul.SoulData::entityType,
        ByteBufCodecs.INT,
        SpawnerSoul.SoulData::power,
        SpawnerMachineTask.SpawnType.STREAM_CODEC,
        SpawnerSoul.SoulData::spawnType,
        SpawnerSoul.SoulData::new
    );

    public static final String NAME = "spawner";

    //SoulData Manger for the spawner data
    public static final SoulDataReloadListener<SoulData> SPAWNER = new SoulDataReloadListener<>(NAME, CODEC);

    @SubscribeEvent
    static void addResource(AddReloadListenerEvent event) {
        event.addListener(SPAWNER);
    }
}
