package com.enderio.machines.common.soulpot;

import com.enderio.EnderIOBase;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.Optional;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID)
public record SoulEnvironmentData(EntityType<?> type, Weight weight, Origin<?> origin) implements WeightedEntry, SoulData {
    public static final Codec<SoulEnvironmentData> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(SoulEnvironmentData::type),
            Weight.CODEC.fieldOf("weight").forGetter(SoulEnvironmentData::weight),
            Origin.CODEC.fieldOf("potPos").forGetter(SoulEnvironmentData::origin))
        .apply(inst, SoulEnvironmentData::new));

    public static final String NAME = "environment";

    // SoulData Manger for the spawner data
    private static final SoulDataReloadListener<SoulEnvironmentData> ENVIRONMENT = new SoulDataReloadListener<>(NAME, CODEC);

    @SubscribeEvent
    private static void addResource(AddReloadListenerEvent event) {
        event.addListener(ENVIRONMENT);
    }


    public static Optional<EntityType<?>> findEntity(RandomSource source, OriginContext context) {
        WeightedRandomList<SoulEnvironmentData> soulEnvironmentDataWeightedRandomList = WeightedRandomList.create(
            ENVIRONMENT.map.values().stream().filter(data -> data.origin.matches(context)).toList());
        return soulEnvironmentDataWeightedRandomList.getRandom(source).map(SoulEnvironmentData::type);
    }

    @Override
    public Weight getWeight() {
        return weight;
    }

    @Override
    public ResourceLocation getKey() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }
}
