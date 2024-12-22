package com.enderio.machines.data.souldata;

import com.enderio.EnderIOBase;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.machines.common.blocks.powered_spawner.SpawnerMachineTask;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.SolarSoul;
import com.enderio.machines.common.souldata.SoulData;
import com.enderio.machines.common.souldata.SpawnerSoul;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulDataProvider implements DataProvider {

    private final PackOutput.PathProvider souldataprovider;

    public SoulDataProvider(PackOutput packOutput) {
        this.souldataprovider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "eio_soul");
    }

    public void buildSoulData(Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        addSpawnerData(EntityType.ALLAY, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.AXOLOTL, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.BAT, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.BEE, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.BLAZE, 40_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CAT, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CAVE_SPIDER, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.CHICKEN, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.COD, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.COW, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CREEPER, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.DOLPHIN, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.DONKEY, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.DROWNED, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ELDER_GUARDIAN, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.ENDERMAN, 60_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ENDERMITE, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ENDER_DRAGON, 1_000_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.EVOKER, 100_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.FOX, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.FROG, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GHAST, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GIANT, 60_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GOAT, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GUARDIAN, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.HOGLIN, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.HUSK, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.HORSE, 15_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ILLUSIONER, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.IRON_GOLEM, 80_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.LLAMA, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.MAGMA_CUBE, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.MULE, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.MOOSHROOM, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.OCELOT, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PANDA, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PARROT, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PHANTOM, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIG, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIGLIN, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIGLIN_BRUTE, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.PILLAGER, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.POLAR_BEAR, 15_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.PUFFERFISH, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.RABBIT, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.RAVAGER, 60_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SALMON, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SHEEP, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SHULKER, 200_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SKELETON, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SILVERFISH, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.SKELETON_HORSE, 15_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.SLIME, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SNOW_GOLEM, 15_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.SPIDER, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SQUID, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.STRIDER, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TADPOLE, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TURTLE, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TRADER_LLAMA, 12_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.TROPICAL_FISH, 10_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.VEX, 20_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.VILLAGER, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.VINDICATOR, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.WARDEN, 1_000_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WITCH, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WITHER, 1_000_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WOLF, 15_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOGLIN, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE_HORSE, 15_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE_VILLAGER, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIFIED_PIGLIN, 32_000, SpawnerMachineTask.SpawnType.ENTITY_TYPE,
                finshedSoulDataConsumer);

        addEngineData(EntityType.BLAZE, FluidTags.LAVA, 300, 15, finshedSoulDataConsumer);
        addEngineData(EntityType.ZOMBIE, EIOFluids.NUTRIENT_DISTILLATION.getSource(), 500, 15, finshedSoulDataConsumer);
        addEngineData(EntityType.ZOMBIE_VILLAGER, EIOFluids.NUTRIENT_DISTILLATION.getSource(), 500, 15,
                finshedSoulDataConsumer);
        addEngineData(EntityType.HUSK, EIOFluids.NUTRIENT_DISTILLATION.getSource(), 500, 15, finshedSoulDataConsumer);
        addEngineData(EntityType.ENDERMAN, EIOFluids.DEW_OF_THE_VOID.getSource(), 900, 10, finshedSoulDataConsumer);

        addSolarData(EntityType.PHANTOM, false, true, null, finshedSoulDataConsumer);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
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

    @NotNull
    @Override
    public String getName() {
        return "Souldata";
    }

    private void addSpawnerData(EntityType<?> entityType, int power, SpawnerMachineTask.SpawnType type,
            Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        SpawnerSoul.SoulData data = new SpawnerSoul.SoulData(key, power, type);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(SpawnerSoul.CODEC, data,
                SpawnerSoul.NAME + "/" + key.getNamespace() + "_" + key.getPath()));
    }

    private void addEngineData(EntityType<?> entityType, Fluid fluid, int powerpermb, int tickpermb,
            Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        ResourceLocation fluidRL = BuiltInRegistries.FLUID.getKey(fluid);
        EngineSoul.SoulData data = new EngineSoul.SoulData(entityRL, fluidRL.toString(), powerpermb, tickpermb);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(EngineSoul.CODEC, data,
                EngineSoul.NAME + "/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    private void addEngineData(EntityType<?> entityType, TagKey<Fluid> fluid, int powerpermb, int tickpermb,
            Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        String fluidRL = "#" + fluid.location();
        EngineSoul.SoulData data = new EngineSoul.SoulData(entityRL, fluidRL, powerpermb, tickpermb);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(EngineSoul.CODEC, data,
                EngineSoul.NAME + "/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    private void addSolarData(EntityType<?> entityType, boolean daytime, boolean nighttime,
            @Nullable ResourceKey<Level> level, Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        SolarSoul.SoulData data = new SolarSoul.SoulData(entityRL, daytime, nighttime, Optional.ofNullable(level));
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(SolarSoul.CODEC, data,
                SolarSoul.NAME + "/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    public static class FinshedSoulData<T extends SoulData> {

        private final Codec<T> codec;
        private final T data;
        private final ResourceLocation id;

        private FinshedSoulData(Codec<T> codec, T data, String id) {
            this.codec = codec;
            this.data = data;
            this.id = EnderIOBase.loc(id);
        }

        private FinshedSoulData(Codec<T> codec, T data, ResourceLocation id) {
            this.codec = codec;
            this.data = data;
            this.id = id;
        }

        public JsonObject serializeData() {
            DataResult<JsonElement> element = codec.encodeStart(JsonOps.INSTANCE, data);
            return element.getOrThrow().getAsJsonObject();
        }

        public ResourceLocation getId() {
            return this.id;
        }

    }
}
