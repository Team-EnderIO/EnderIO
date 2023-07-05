package com.enderio.machines.common.souldata;

import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Class that holds all information related to the mob soul in a spawner
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpawnerSoul {

    /**
     * Record that holds the data for the powered spawner spawn task
     * @param entitytype entitytype resourcelocation of the mob soul (and to spawn)
     * @param power powercost of the spawner
     * @param spawnType way to spawn the mob
     */
    public record SoulData(ResourceLocation entitytype, int power, SpawnerMachineTask.SpawnType spawnType) implements ISoulData {
        @Override
        public ResourceLocation getKey() {
            return entitytype();
        }
    }

    /**
     * Codec for the spawner data
     */
    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance ->
        soulDataInstance.group(ResourceLocation.CODEC.fieldOf("entity").forGetter(SoulData::entitytype),
            Codec.INT.fieldOf("power").forGetter(SoulData::power),
            Codec.STRING.comapFlatMap(SpawnerMachineTask.SpawnType::byName, SpawnerMachineTask.SpawnType::getName).stable().fieldOf("type").forGetter(SoulData::spawnType))
            .apply(soulDataInstance, SoulData::new));

    //SoulData Manger for the spawner data
    public static final SoulDataReloadListner<SoulData> SPAWNER = new SoulDataReloadListner<>("eio_soul/spawner", CODEC);

    @SubscribeEvent
    static void addResource(AddReloadListenerEvent event) {
        event.addListener(SPAWNER);
    }
}
