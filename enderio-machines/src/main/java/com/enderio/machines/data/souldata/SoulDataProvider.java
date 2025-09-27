package com.enderio.machines.data.souldata;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.machines.common.blocks.powered_spawner.MobSpawnMode;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.FarmSoul;
import com.enderio.machines.common.souldata.SolarSoul;
import com.enderio.machines.common.soulpot.BiomeOrigin;
import com.enderio.machines.common.soulpot.BlockOrigin;
import com.enderio.machines.common.soulpot.HeightOrigin;
import com.enderio.machines.common.soulpot.LightOrigin;
import com.enderio.machines.common.soulpot.LogicOrigin;
import com.enderio.machines.common.soulpot.NotOrigin;
import com.enderio.machines.common.soulpot.Origin;
import com.enderio.machines.common.soulpot.SoulData;
import com.enderio.machines.common.souldata.SpawnerSoul;
import com.enderio.machines.common.soulpot.SoulEnvironmentData;
import com.enderio.machines.common.soulpot.StructureOrigin;
import com.enderio.machines.common.soulpot.SurfaceOrigin;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.Weight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SoulDataProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final HolderLookup.Provider provider;

    public SoulDataProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "eio_soul");
        try {
            this.provider = lookupProvider.get();
        } catch (Exception e) {
            throw new IllegalStateException("couldn't wait for lookup", e);
        }
    }

    public void buildSoulData(Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        addSpawnerData(EntityType.ALLAY, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.AXOLOTL, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.BAT, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.BEE, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.BLAZE, 40_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.CAT, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.CAVE_SPIDER, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.CHICKEN, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.COD, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.COW, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.CREEPER, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.DOLPHIN, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.DONKEY, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.DROWNED, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ELDER_GUARDIAN, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ENDERMAN, 60_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ENDERMITE, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ENDER_DRAGON, 1_000_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.EVOKER, 100_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.FOX, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.FROG, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.GHAST, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.GIANT, 60_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.GOAT, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.GUARDIAN, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.HOGLIN, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.HUSK, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.HORSE, 15_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ILLUSIONER, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.IRON_GOLEM, 80_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.LLAMA, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.MAGMA_CUBE, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.MULE, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.MOOSHROOM, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.OCELOT, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.PANDA, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.PARROT, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.PHANTOM, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.PIG, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.PIGLIN, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.PIGLIN_BRUTE, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.PILLAGER, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.POLAR_BEAR, 15_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.PUFFERFISH, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.RABBIT, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.RAVAGER, 60_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SALMON, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SHEEP, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SHULKER, 200_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SKELETON, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SILVERFISH, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SKELETON_HORSE, 15_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SLIME, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SNOW_GOLEM, 15_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SPIDER, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.SQUID, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.STRIDER, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.TADPOLE, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.TURTLE, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.TRADER_LLAMA, 12_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.TROPICAL_FISH, 10_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.VEX, 20_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.VILLAGER, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.VINDICATOR, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.WARDEN, 1_000_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.WITCH, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.WITHER, 1_000_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.WOLF, 15_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ZOGLIN, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE_HORSE, 15_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE_VILLAGER, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIFIED_PIGLIN, 32_000, MobSpawnMode.NEW, finishedSoulDataConsumer);

        addEngineData(EntityType.BLAZE, FluidTags.LAVA, 800, 18, finishedSoulDataConsumer);
        addEngineData(EntityType.ZOMBIE, EIOFluids.NUTRIENT_DISTILLATION.getSource(), 1000, 18, finishedSoulDataConsumer);
        addEngineData(EntityType.ZOMBIE_VILLAGER, EIOFluids.NUTRIENT_DISTILLATION.getSource(), 1000, 18,
                finishedSoulDataConsumer);
        addEngineData(EntityType.HUSK, EIOFluids.NUTRIENT_DISTILLATION.getSource(), 1000, 18, finishedSoulDataConsumer);
        addEngineData(EntityType.ENDERMAN, EIOFluids.DEW_OF_THE_VOID.getSource(), 1200, 12, finishedSoulDataConsumer);
        addEngineData(EntityType.CREEPER, EIOFluids.ROCKET_FUEL.getSource(), 800, 12, finishedSoulDataConsumer);

        addFarmData(EntityType.BEE, 0.8f, 0, 1, finishedSoulDataConsumer);
        addFarmData(EntityType.VILLAGER, 1, 0, 1.2f, finishedSoulDataConsumer);
        addFarmData(EntityType.SNIFFER, 1, 1, 1, finishedSoulDataConsumer);

        addSolarData(EntityType.PHANTOM, false, true, null, finishedSoulDataConsumer);

        addEnvironmentData(finishedSoulDataConsumer);

    }

    private void addEnvironmentData(Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        Origin<?> darkness = new LightOrigin(false, 0);
        Origin<?> hasBats = new LightOrigin(false, 3);
        HolderGetter.Provider holderGetter = provider.asGetterLookup();
        HolderLookup.RegistryLookup<Biome> biomeRegistryLookup = provider.lookupOrThrow(Registries.BIOME);
        HolderGetter<Structure> structureHolderGetter = holderGetter.lookupOrThrow(Registries.STRUCTURE);
        Origin<?> isOverworld = new BiomeOrigin(biomeRegistryLookup.getOrThrow(BiomeTags.IS_OVERWORLD));
        Origin<?> isNether = new BiomeOrigin(biomeRegistryLookup.getOrThrow(BiomeTags.IS_NETHER));
        Origin<?> isOceanMonument = new StructureOrigin(structureHolderGetter.getOrThrow(BuiltinStructures.OCEAN_MONUMENT));
        Origin<?> isVillage = new StructureOrigin(structureHolderGetter.getOrThrow(StructureTags.VILLAGE));
        Origin<?> isDesert = new BiomeOrigin(biomeRegistryLookup.getOrThrow(Tags.Biomes.IS_DESERT));
        Origin<?> isSwamp = new BiomeOrigin(biomeRegistryLookup.getOrThrow(Tags.Biomes.IS_SWAMP));
        Origin<?> isOutpost = new StructureOrigin(structureHolderGetter.getOrThrow(BuiltinStructures.PILLAGER_OUTPOST));
        Origin<?> isMansion = new StructureOrigin(structureHolderGetter.getOrThrow(BuiltinStructures.WOODLAND_MANSION));
        Origin<?> isMountain = new BiomeOrigin(biomeRegistryLookup.getOrThrow(BiomeTags.IS_MOUNTAIN));
        Origin<?> isHills = new BiomeOrigin(biomeRegistryLookup.getOrThrow(BiomeTags.IS_HILL));
        Origin<?> isJungle = new BiomeOrigin(biomeRegistryLookup.getOrThrow(BiomeTags.IS_JUNGLE));
        Origin<?> isSavanna = new BiomeOrigin(biomeRegistryLookup.getOrThrow(BiomeTags.IS_SAVANNA));
        Origin<?> isBirchForest = new BiomeOrigin(biomeRegistryLookup.getOrThrow(Biomes.BIRCH_FOREST));
        Origin<?> isLushCave = new BiomeOrigin(biomeRegistryLookup.getOrThrow(Biomes.LUSH_CAVES));
        Origin<?> isSurface = new SurfaceOrigin(Heightmap.Types.MOTION_BLOCKING);
        Origin<?> hasWater = new BlockOrigin(Blocks.WATER, 3, 3);
        Origin<?> hasLava = new BlockOrigin(Blocks.LAVA, 5, 5);
        Origin<?> hasBeeHive = new BlockOrigin(BlockTags.BEEHIVES, 3, 5);
        BiomeOrigin isCrimson = new BiomeOrigin(biomeRegistryLookup.getOrThrow(Biomes.CRIMSON_FOREST));
        Origin<?> hasSpawns = new BiomeOrigin(new NotHolderSet<>(biomeRegistryLookup, biomeRegistryLookup.getOrThrow(Tags.Biomes.NO_DEFAULT_MONSTERS)));
        Origin<?> hasAnimals = LogicOrigin.and(isSurface, isOverworld);
        Origin<?> isNotOcean = new BiomeOrigin(new NotHolderSet<>(biomeRegistryLookup, biomeRegistryLookup.getOrThrow(Tags.Biomes.IS_AQUATIC)));
        Origin<?> isOcean = new BiomeOrigin(biomeRegistryLookup.getOrThrow(Tags.Biomes.IS_AQUATIC));
        Origin<?> isTaiga = new BiomeOrigin(biomeRegistryLookup.getOrThrow(Tags.Biomes.IS_TAIGA));
        Origin<?> hasMonsters = LogicOrigin.and(LogicOrigin.and(LogicOrigin.and(darkness, isOverworld), hasSpawns), isNotOcean);
        Origin<?> isNetherFortress = new StructureOrigin(structureHolderGetter.getOrThrow(BuiltinStructures.FORTRESS));
        Origin<?> isBastion = new StructureOrigin(structureHolderGetter.getOrThrow(BuiltinStructures.BASTION_REMNANT));
        addSoulEnvironment(EntityType.CREEPER, 10, hasMonsters, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.ENDERMAN, 3, hasMonsters, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.SKELETON, 20, hasMonsters, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.ZOMBIE, 20, hasMonsters, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.SPIDER, 20, hasMonsters, finishedSoulDataConsumer);

        addSoulEnvironment(EntityType.BOGGED, 50, LogicOrigin.and(darkness, isSwamp), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.HUSK, 50, LogicOrigin.and(darkness, isDesert), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.CAVE_SPIDER, 30, LogicOrigin.and(darkness, new StructureOrigin(structureHolderGetter.getOrThrow(StructureTags.MINESHAFT) )), finishedSoulDataConsumer);
        addSoulEnvironment("outpost", EntityType.ALLAY, 20, isOutpost, finishedSoulDataConsumer);
        addSoulEnvironment("outpost", EntityType.PILLAGER, 20, isOutpost, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.CAMEL, 10, isDesert, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.CAT, 5, isVillage, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.GUARDIAN, 10, isOceanMonument, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.ELDER_GUARDIAN, 1, isOceanMonument, finishedSoulDataConsumer);

        addSoulEnvironment("mansion", EntityType.VINDICATOR, 6, isMansion, finishedSoulDataConsumer);
        addSoulEnvironment("mansion", EntityType.EVOKER, 3, isMansion, finishedSoulDataConsumer);
        addSoulEnvironment("mansion", EntityType.VEX, 6, isMansion, finishedSoulDataConsumer);
        addSoulEnvironment("mansion", EntityType.ALLAY, 20, isMansion, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.FOX, 15, new BiomeOrigin(biomeRegistryLookup.getOrThrow(BiomeTags.IS_TAIGA)), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.GLOW_SQUID, 15, LogicOrigin.and(LogicOrigin.and(darkness, new HeightOrigin(false, 30)), hasWater), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.GOAT, 10, LogicOrigin.and(isMountain, isSurface), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.LLAMA, 10, LogicOrigin.and(isHills, isSurface), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.MOOSHROOM, 100, LogicOrigin.and(new BiomeOrigin(biomeRegistryLookup.getOrThrow(Biomes.MUSHROOM_FIELDS)), isSurface), finishedSoulDataConsumer); //very high weight to have less monsters in mushroom island
        addSoulEnvironment(EntityType.OCELOT, 10, LogicOrigin.and(isSurface, isJungle), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.PANDA, 15, LogicOrigin.and(isSurface, isJungle), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.PARROT, 5, LogicOrigin.and(isSurface, isJungle), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.POLAR_BEAR, 25, LogicOrigin.and(isSurface, new BiomeOrigin(biomeRegistryLookup.getOrThrow(BiomeTags.POLAR_BEARS_SPAWN_ON_ALTERNATE_BLOCKS))), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.SLIME, 25, LogicOrigin.and(isSurface, isSwamp), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.WITCH, 25, LogicOrigin.and(isSurface, isSwamp), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.SQUID, 25, LogicOrigin.and(isOcean, LogicOrigin.and(new HeightOrigin(true, 50), new HeightOrigin(false, 63))), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.WOLF, 5, LogicOrigin.and(isSurface, isTaiga), finishedSoulDataConsumer);

        addSoulEnvironment(EntityType.PIG, 7, hasAnimals, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.CHICKEN, 7, hasAnimals, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.COW, 7, hasAnimals, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.HORSE, 3, hasAnimals, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.SHEEP, 7, hasAnimals, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.RABBIT, 3, LogicOrigin.and(isSurface, isDesert), finishedSoulDataConsumer);

        addSoulEnvironment(EntityType.COD, 10, LogicOrigin.and(hasWater, isOcean), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.SALMON, 10, LogicOrigin.and(hasWater, isOcean), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.DOLPHIN, 10, LogicOrigin.and(hasWater, isOcean), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.PUFFERFISH, 10, LogicOrigin.and(hasWater, isOcean), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.TROPICAL_FISH, 10, LogicOrigin.and(hasWater, isOcean), finishedSoulDataConsumer);
        //special: axolotl, armadillo, bat, bee, breeze, drowned, frog, phantom, Silverfish
        addSoulEnvironment(EntityType.AXOLOTL, 40, LogicOrigin.and(isLushCave, hasWater), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.ARMADILLO, 7, LogicOrigin.and(isSavanna, isSurface), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.BAT, 7, LogicOrigin.and(hasBats, new NotOrigin(isSurface)), finishedSoulDataConsumer);
        // rare chance for bees in birch forests
        addSoulEnvironment("general", EntityType.BEE, 3, LogicOrigin.and(isBirchForest, isSurface), finishedSoulDataConsumer);
        // higher chance when near beehive/nest
        addSoulEnvironment(EntityType.BEE, 35, hasBeeHive, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.BREEZE, 60, new StructureOrigin(structureHolderGetter.getOrThrow(BuiltinStructures.TRIAL_CHAMBERS)), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.DROWNED, 20, LogicOrigin.and(darkness, isOcean), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.FROG, 20, LogicOrigin.and(isSwamp, isSurface), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.SILVERFISH, 20, new BlockOrigin(Blocks.INFESTED_STONE, 6,5), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.PHANTOM, 40, LogicOrigin.and(LogicOrigin.and(isSurface, new HeightOrigin(true, 90)), isOverworld), finishedSoulDataConsumer);

        //nether
        addSoulEnvironment(EntityType.BLAZE, 15, isNetherFortress, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.WITHER_SKELETON, 5, isNetherFortress, finishedSoulDataConsumer);
        addSoulEnvironment("general" + rl2String(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SKELETON)), EntityType.SKELETON, 5, isNetherFortress, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.MAGMA_CUBE, 5, isNetherFortress, finishedSoulDataConsumer);
        addSoulEnvironment("general" + rl2String(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.MAGMA_CUBE)), EntityType.MAGMA_CUBE, 15, new BiomeOrigin(biomeRegistryLookup.getOrThrow(Biomes.BASALT_DELTAS)), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.GHAST, 5, isNether, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.HOGLIN, 10, isCrimson, finishedSoulDataConsumer);
        addSoulEnvironment("general", EntityType.PIGLIN, 10, isCrimson, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.PIGLIN, 10, isBastion, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.PIGLIN_BRUTE, 7, isBastion, finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.STRIDER, 8, LogicOrigin.and(isNether, hasLava), finishedSoulDataConsumer);

        //end
        addSoulEnvironment("general", EntityType.ENDERMAN, 20, new BiomeOrigin(biomeRegistryLookup.getOrThrow(BiomeTags.IS_END)), finishedSoulDataConsumer);
        addSoulEnvironment(EntityType.SHULKER, 15, new StructureOrigin(structureHolderGetter.getOrThrow(BuiltinStructures.END_CITY)), finishedSoulDataConsumer);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        RegistryOps<JsonElement> jsonOps = provider.createSerializationContext(JsonOps.INSTANCE);
        this.buildSoulData(finishedSoulData -> {
            if (!set.add(finishedSoulData.getId())) {
                throw new IllegalStateException("Duplicate recipe:" + finishedSoulData.getId());
            } else {
                list.add(DataProvider.saveStable(cachedOutput, finishedSoulData.serializeData(jsonOps),
                        this.pathProvider.json(finishedSoulData.getId())));
            }
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @NotNull
    @Override
    public String getName() {
        return "Souldata";
    }

    private void addSpawnerData(EntityType<?> entityType, int power, MobSpawnMode type,
            Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        SpawnerSoul.SoulData data = new SpawnerSoul.SoulData(key, power, type);
        finishedSoulDataConsumer.accept(new FinishedSoulData<>(SpawnerSoul.CODEC, data,
                SpawnerSoul.NAME + "/" + rl2String(key)));
    }

    private void addEngineData(EntityType<?> entityType, Fluid fluid, int powerpermb, int tickpermb,
            Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        ResourceLocation fluidRL = BuiltInRegistries.FLUID.getKey(fluid);
        EngineSoul.SoulData data = new EngineSoul.SoulData(entityRL, fluidRL.toString(), powerpermb, tickpermb);
        finishedSoulDataConsumer.accept(new FinishedSoulData<>(EngineSoul.CODEC, data,
                EngineSoul.NAME + "/" + rl2String(entityRL)));
    }

    private void addEngineData(EntityType<?> entityType, TagKey<Fluid> fluid, int powerpermb, int tickpermb,
            Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        String fluidRL = "#" + fluid.location();
        EngineSoul.SoulData data = new EngineSoul.SoulData(entityRL, fluidRL, powerpermb, tickpermb);
        finishedSoulDataConsumer.accept(new FinishedSoulData<>(EngineSoul.CODEC, data,
                EngineSoul.NAME + "/" + rl2String(entityRL)));
    }

    private void addFarmData(EntityType<?> entityType, float bonemeal, int seeds, float power,
            Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        FarmSoul.SoulData data = new FarmSoul.SoulData(entityRL, bonemeal, seeds, power);
        finishedSoulDataConsumer.accept(new FinishedSoulData<>(FarmSoul.CODEC, data,
                FarmSoul.NAME + "/" + rl2String(entityRL)));
    }

    private void addSolarData(EntityType<?> entityType, boolean daytime, boolean nighttime,
            @Nullable ResourceKey<Level> level, Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        SolarSoul.SoulData data = new SolarSoul.SoulData(entityRL, daytime, nighttime, Optional.ofNullable(level));
        finishedSoulDataConsumer.accept(new FinishedSoulData<>(SolarSoul.CODEC, data,
                SolarSoul.NAME + "/" + rl2String(entityRL)));
    }
    
    private void addSoulEnvironment(EntityType<?> type, int weight, Origin<?> data, Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        addSoulEnvironment0(rl2String(BuiltInRegistries.ENTITY_TYPE.getKey(type)), type, weight, data, finishedSoulDataConsumer);
    }
    private void addSoulEnvironment(String prefix, EntityType<?> type, int weight, Origin<?> data, Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        addSoulEnvironment0(prefix + "/" + rl2String(BuiltInRegistries.ENTITY_TYPE.getKey(type)), type, weight, data, finishedSoulDataConsumer);
    }

    private void addSoulEnvironment0(String location, EntityType<?> type, int weight, Origin<?> data, Consumer<FinishedSoulData<?>> finishedSoulDataConsumer) {
        finishedSoulDataConsumer.accept(new FinishedSoulData<>(SoulEnvironmentData.CODEC, new SoulEnvironmentData(type, Weight.of(weight), data), SoulEnvironmentData.NAME + "/" + location));
    }
    
    private static String rl2String(ResourceLocation rl) {
        return rl.getNamespace() + "_" + rl.getPath();
    }

    public static class FinishedSoulData<T extends SoulData> {

        private final Codec<T> codec;
        private final T data;
        private final ResourceLocation id;

        private FinishedSoulData(Codec<T> codec, T data, String id) {
            this.codec = codec;
            this.data = data;
            this.id = EnderIO.loc(id);
        }

        private FinishedSoulData(Codec<T> codec, T data, ResourceLocation id) {
            this.codec = codec;
            this.data = data;
            this.id = id;
        }

        public JsonObject serializeData(DynamicOps<JsonElement> ops) {
            DataResult<JsonElement> element = codec.encodeStart(ops, data);
            return element.getOrThrow().getAsJsonObject();
        }

        public ResourceLocation getId() {
            return this.id;
        }

    }
}
