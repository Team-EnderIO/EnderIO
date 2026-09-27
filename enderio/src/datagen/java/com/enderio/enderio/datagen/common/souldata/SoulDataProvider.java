package com.enderio.enderio.datagen.common.souldata;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.powered_spawner.MobSpawnMode;
import com.enderio.enderio.foundation.souldata.EngineSoul;
import com.enderio.enderio.foundation.souldata.FarmSoul;
import com.enderio.enderio.foundation.souldata.SolarSoul;
import com.enderio.enderio.foundation.souldata.SoulData;
import com.enderio.enderio.foundation.souldata.SpawnerSoul;
import com.enderio.enderio.init.EIOFluids;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SoulDataProvider implements DataProvider {

    private final PackOutput.PathProvider souldataprovider;

    public SoulDataProvider(PackOutput packOutput) {
        this.souldataprovider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "eio_soul");
    }

    public void buildSoulData(Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        addSpawnerData(EntityTypes.ALLAY, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ARMADILLO, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.AXOLOTL, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.BAT, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.BEE, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.BLAZE, 40_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.BOGGED, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.BREEZE, 40_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.CAMEL, 15_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.CAT, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.CAVE_SPIDER, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.CHICKEN, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.COD, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.COPPER_GOLEM, 50_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.COW, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.CREEPER, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.DOLPHIN, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.DONKEY, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.DROWNED, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ELDER_GUARDIAN, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ENDERMAN, 60_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ENDERMITE, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ENDER_DRAGON, 1_000_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.EVOKER, 100_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.FOX, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.FROG, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.GHAST, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.GIANT, 60_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.GLOW_SQUID, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.GOAT, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.GUARDIAN, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.HOGLIN, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.HUSK, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.HORSE, 15_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ILLUSIONER, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.IRON_GOLEM, 80_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.LLAMA, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.MAGMA_CUBE, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.MULE, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.MOOSHROOM, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.NAUTILUS, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.OCELOT, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.PANDA, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.PARROT, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.PHANTOM, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.PIG, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.PIGLIN, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.PIGLIN_BRUTE, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.PILLAGER, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.POLAR_BEAR, 15_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.PUFFERFISH, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.RABBIT, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.RAVAGER, 60_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SALMON, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SHEEP, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SHULKER, 200_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SKELETON, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SILVERFISH, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SKELETON_HORSE, 15_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SLIME, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SNIFFER, 60_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SNOW_GOLEM, 15_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SPIDER, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.SQUID, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.STRAY, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.STRIDER, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.TADPOLE, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.TURTLE, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.TRADER_LLAMA, 12_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.TROPICAL_FISH, 10_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.VEX, 20_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.VILLAGER, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.VINDICATOR, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.WANDERING_TRADER, 40_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.WARDEN, 1_000_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.WITCH, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.WITHER, 1_000_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.WOLF, 15_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ZOGLIN, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ZOMBIE, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ZOMBIE_HORSE, 15_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ZOMBIE_VILLAGER, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);
        addSpawnerData(EntityTypes.ZOMBIFIED_PIGLIN, 32_000, MobSpawnMode.NEW, finshedSoulDataConsumer);

        addEngineData(EntityTypes.BLAZE, FluidTags.LAVA, 800, 18, finshedSoulDataConsumer);
        addEngineData(EntityTypes.ZOMBIE, EIOFluids.NUTRIENT_DISTILLATION.source().get(), 1000, 18, finshedSoulDataConsumer);
        addEngineData(EntityTypes.ZOMBIE_VILLAGER, EIOFluids.NUTRIENT_DISTILLATION.source().get(), 1000, 18,
                finshedSoulDataConsumer);
        addEngineData(EntityTypes.HUSK, EIOFluids.NUTRIENT_DISTILLATION.source().get(), 1000, 18, finshedSoulDataConsumer);
        addEngineData(EntityTypes.ENDERMAN, EIOFluids.DEW_OF_THE_VOID.source().get(), 1200, 12, finshedSoulDataConsumer);
        addEngineData(EntityTypes.CREEPER, EIOFluids.ROCKET_FUEL.source().get(), 800, 12, finshedSoulDataConsumer);

        addFarmData(EntityTypes.BEE, 0.8f, 0, 1, finshedSoulDataConsumer);
        addFarmData(EntityTypes.VILLAGER, 1, 0, 1.2f, finshedSoulDataConsumer);
        addFarmData(EntityTypes.SNIFFER, 1, 1, 1, finshedSoulDataConsumer);

        addSolarData(EntityTypes.PHANTOM, false, true, null, finshedSoulDataConsumer);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Set<Identifier> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildSoulData(finshedSoulData -> {
            if (!set.add(finshedSoulData.getId())) {
                throw new IllegalStateException("Duplicate recipe" + finshedSoulData.getId());
            } else {
                list.add(DataProvider.saveStable(cachedOutput, finshedSoulData.serializeData(),
                        this.souldataprovider.json(finshedSoulData.getId())));
            }
        });
        return CompletableFuture.allOf(list.toArray((p_253414_) -> new CompletableFuture[p_253414_]));
    }

    @NonNull
    @Override
    public String getName() {
        return "Souldata";
    }

    private void addSpawnerData(EntityType<?> entityType, int power, MobSpawnMode type,
            Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        SpawnerSoul.SoulData data = new SpawnerSoul.SoulData(key, power, type);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(SpawnerSoul.CODEC, data,
                SpawnerSoul.NAME + "/" + key.getNamespace() + "_" + key.getPath()));
    }

    private void addEngineData(EntityType<?> entityType, Fluid fluid, int powerpermb, int tickpermb,
            Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        Identifier entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        Identifier fluidRL = BuiltInRegistries.FLUID.getKey(fluid);
        EngineSoul.SoulData data = new EngineSoul.SoulData(entityRL, fluidRL.toString(), powerpermb, tickpermb);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(EngineSoul.CODEC, data,
                EngineSoul.NAME + "/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    private void addEngineData(EntityType<?> entityType, TagKey<Fluid> fluid, int powerpermb, int tickpermb,
            Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        Identifier entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        String fluidRL = "#" + fluid.location();
        EngineSoul.SoulData data = new EngineSoul.SoulData(entityRL, fluidRL, powerpermb, tickpermb);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(EngineSoul.CODEC, data,
                EngineSoul.NAME + "/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    private void addFarmData(EntityType<?> entityType, float bonemeal, int seeds, float power,
            Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        Identifier entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        FarmSoul.SoulData data = new FarmSoul.SoulData(entityRL, bonemeal, seeds, power);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(FarmSoul.CODEC, data,
                FarmSoul.NAME + "/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    private void addSolarData(EntityType<?> entityType, boolean daytime, boolean nighttime,
            @Nullable ResourceKey<Level> level, Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        Identifier entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        SolarSoul.SoulData data = new SolarSoul.SoulData(entityRL, daytime, nighttime, Optional.ofNullable(level));
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(SolarSoul.CODEC, data,
                SolarSoul.NAME + "/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    public static class FinshedSoulData<T extends SoulData> {

        private final Codec<T> codec;
        private final T data;
        private final Identifier id;

        private FinshedSoulData(Codec<T> codec, T data, String id) {
            this.codec = codec;
            this.data = data;
            this.id = EnderIO.id(id);
        }

        private FinshedSoulData(Codec<T> codec, T data, Identifier id) {
            this.codec = codec;
            this.data = data;
            this.id = id;
        }

        public JsonObject serializeData() {
            DataResult<JsonElement> element = codec.encodeStart(JsonOps.INSTANCE, data);
            return element.getOrThrow().getAsJsonObject();
        }

        public Identifier getId() {
            return this.id;
        }

    }
}
