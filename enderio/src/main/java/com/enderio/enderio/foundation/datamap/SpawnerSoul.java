package com.enderio.enderio.foundation.datamap;

import com.enderio.enderio.content.machines.powered_spawner.MobSpawnMode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SpawnerSoul(int energyCost, MobSpawnMode spawnMode) {
    public static final Codec<SpawnerSoul> CODEC = RecordCodecBuilder.create(soulDataInstance -> soulDataInstance
        .group(Codec.INT.fieldOf("power").forGetter(SpawnerSoul::energyCost),
            MobSpawnMode.CODEC.fieldOf("mode").forGetter(SpawnerSoul::spawnMode))
        .apply(soulDataInstance, SpawnerSoul::new));
}
