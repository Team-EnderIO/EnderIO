package com.enderio.machines.data;

import com.enderio.core.data.CodecReloadListner;
import com.enderio.machines.common.blockentity.task.SpawnTask;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PoweredSpawnerSoul {

    public record SoulData(ResourceLocation entitytype, int power, SpawnTask.SpawnType spawnType) {}

    private static final Codec<SoulData> CODEC = RecordCodecBuilder.create(soulDataInstance ->
        soulDataInstance.group(ResourceLocation.CODEC.fieldOf("entity").forGetter(SoulData::entitytype),
            Codec.INT.optionalFieldOf("power", 40000).forGetter(SoulData::power),
            Codec.STRING.comapFlatMap(SpawnTask.SpawnType::byName, SpawnTask.SpawnType::name).stable().fieldOf("type").forGetter(SoulData::spawnType))
            .apply(soulDataInstance, SoulData::new));

    public static final CodecReloadListner<SoulData> SPAWNER = new CodecReloadListner<SoulData>("eio_soul/spawner", CODEC) {
        public Optional<SoulData> matches(ResourceLocation entitytype) {
            return this.map.values().stream().filter(data -> entitytype.equals(data.entitytype)).findFirst();
        }
    };

    @SubscribeEvent
    static void addResource(AddReloadListenerEvent event) {
        event.addListener(SPAWNER);
    }
}
