package com.enderio.enderio.datagen.common.data_maps;

import com.enderio.enderio.content.machines.powered_spawner.MobSpawnMode;
import com.enderio.enderio.foundation.datamap.EngineSoul;
import com.enderio.enderio.foundation.datamap.SolarSoul;
import com.enderio.enderio.foundation.datamap.SpawnerSoul;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIODataMaps;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SoulDataMapProvider extends DataMapProvider {
    public SoulDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        HolderGetter<Fluid> fluids = provider.lookupOrThrow(Registries.FLUID);

        builder(EIODataMaps.ENGINE_SOUL)
            .add(EntityTypeIds.BLAZE, new EngineSoul(FluidIngredient.of(fluids.getOrThrow(FluidTags.LAVA)), 800, 18), false)
            .add(EIOTags.EntityTypes.ZOMBIES, new EngineSoul(FluidIngredient.of(EIOFluids.NUTRIENT_DISTILLATION.source().get()), 1000, 18), false)
            .add(EIOTags.EntityTypes.ENDERMEN, new EngineSoul(FluidIngredient.of(EIOFluids.DEW_OF_THE_VOID.source().get()), 1200, 12), false)
            .add(EIOTags.EntityTypes.CREEPERS, new EngineSoul(FluidIngredient.of(EIOFluids.ROCKET_FUEL.source().get()), 800, 12), false)
            .build();

        builder(EIODataMaps.SOLAR_SOUL)
            .add(EntityTypeIds.PHANTOM, new SolarSoul(false, true, Optional.empty()), false)
            .build();

        builder(EIODataMaps.SPAWNER_SOUL)
            // Tags
            .add(EIOTags.EntityTypes.ZOMBIES, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeTags.SKELETONS, new SpawnerSoul(32_000, MobSpawnMode.NEW), false) // todo wrapper tag
            // Loose
            // TODO: Try and group common/shared configs by tag.
            .add(EntityTypeIds.ALLAY, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.ARMADILLO, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.AXOLOTL, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.BAT, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.BEE, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.BLAZE, new SpawnerSoul(40_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.BREEZE, new SpawnerSoul(40_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.CAMEL, new SpawnerSoul(15_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.CAT, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.CAVE_SPIDER, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.CHICKEN, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.COD, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.COPPER_GOLEM, new SpawnerSoul(50_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.COW, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.CREEPER, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.DOLPHIN, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.DONKEY, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.ELDER_GUARDIAN, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.ENDERMAN, new SpawnerSoul(60_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.ENDERMITE, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.ENDER_DRAGON, new SpawnerSoul(1_000_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.EVOKER, new SpawnerSoul(100_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.FOX, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.FROG, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.GHAST, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.GIANT, new SpawnerSoul(60_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.GLOW_SQUID, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.GOAT, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.GUARDIAN, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.HOGLIN, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.HORSE, new SpawnerSoul(15_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.ILLUSIONER, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.IRON_GOLEM, new SpawnerSoul(80_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.LLAMA, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.MAGMA_CUBE, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.MULE, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.MOOSHROOM, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.NAUTILUS, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.OCELOT, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.PANDA, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.PARROT, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.PHANTOM, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.PIG, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.PIGLIN, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.PIGLIN_BRUTE, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.PILLAGER, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.POLAR_BEAR, new SpawnerSoul(15_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.PUFFERFISH, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.RABBIT, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.RAVAGER, new SpawnerSoul(60_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.SALMON, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.SHEEP, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.SHULKER, new SpawnerSoul(200_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.SILVERFISH, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.SLIME, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.SNIFFER, new SpawnerSoul(60_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.SNOW_GOLEM, new SpawnerSoul(15_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.SPIDER, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.SQUID, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.STRIDER, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.TADPOLE, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.TURTLE, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.TRADER_LLAMA, new SpawnerSoul(12_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.TROPICAL_FISH, new SpawnerSoul(10_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.VEX, new SpawnerSoul(20_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.VILLAGER, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.VINDICATOR, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.WANDERING_TRADER, new SpawnerSoul(40_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.WARDEN, new SpawnerSoul(1_000_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.WITCH, new SpawnerSoul(32_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.WITHER, new SpawnerSoul(1_000_000, MobSpawnMode.NEW), false)
            .add(EntityTypeIds.WOLF, new SpawnerSoul(15_000, MobSpawnMode.NEW), false)
            .build();
    }

    @Override
    public String getName() {
        return "EnderIO Engine Soul Data Map";
    }
}
