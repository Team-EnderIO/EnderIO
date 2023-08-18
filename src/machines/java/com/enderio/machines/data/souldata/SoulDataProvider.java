package com.enderio.machines.data.souldata;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.ISoulData;
import com.enderio.machines.common.souldata.SpawnerSoul;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SoulDataProvider implements DataProvider {

    private final PackOutput.PathProvider souldataprovider;
    public SoulDataProvider(PackOutput packOutput) {
        this.souldataprovider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "eio_soul");
    }

    public void buildSoulData(Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        addSpawnerData(EntityType.ALLAY, 15_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.AXOLOTL, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.BAT, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.BEE, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.BLAZE, 60_000 , SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CAT, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CAVE_SPIDER, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CHICKEN, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.COD, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.COW, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CREEPER, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.DOLPHIN, 15_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.DONKEY, 15_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.DROWNED, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ELDER_GUARDIAN, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ENDERMAN, 60_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ENDERMITE, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ENDER_DRAGON, 1_000_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.EVOKER, 200_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.FOX, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.FROG, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GHAST, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GIANT, 60_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GOAT, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GUARDIAN, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.HOGLIN, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.HUSK, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.HORSE, 15_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ILLUSIONER, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.IRON_GOLEM, 100_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.LLAMA, 15_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.MAGMA_CUBE, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.MULE, 15_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.MOOSHROOM, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.OCELOT, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PANDA, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PARROT, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PHANTOM, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIG, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIGLIN, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIGLIN_BRUTE, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PILLAGER, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.POLAR_BEAR, 18_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PUFFERFISH, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.RABBIT, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.RAVAGER, 60_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SALMON, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SHEEP, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SHULKER, 200_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SKELETON, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SILVERFISH, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SKELETON_HORSE, 18_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SLIME, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SNOW_GOLEM, 18_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SPIDER, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SQUID, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.STRIDER, 15_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TADPOLE, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TURTLE, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TRADER_LLAMA, 15_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TROPICAL_FISH, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.VEX, 12_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.VILLAGER, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.VINDICATOR, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WARDEN, 1_000_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WITCH, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WITHER, 1_000_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WOLF, 18_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOGLIN, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE_HORSE, 18_000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE_VILLAGER, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIFIED_PIGLIN, 51_200, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);

        addGeneratorData(EntityType.BLAZE, FluidTags.LAVA, 300, 15, finshedSoulDataConsumer);
        addGeneratorData(EntityType.ZOMBIE, EIOFluids.NUTRIENT_DISTILLATION.get(), 500, 15, finshedSoulDataConsumer);
        addGeneratorData(EntityType.ZOMBIE_VILLAGER, EIOFluids.NUTRIENT_DISTILLATION.get(), 500, 15, finshedSoulDataConsumer);
        addGeneratorData(EntityType.HUSK, EIOFluids.NUTRIENT_DISTILLATION.get(), 500, 15, finshedSoulDataConsumer);
        addGeneratorData(EntityType.ENDERMAN, EIOFluids.DEW_OF_THE_VOID.get(), 900, 10, finshedSoulDataConsumer);

    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildSoulData(finshedSoulData -> {
            if (!set.add(finshedSoulData.getId())) {
                throw new IllegalStateException("Duplicate recipe" + finshedSoulData.getId());
            } else {
                list.add(DataProvider.saveStable(cachedOutput, finshedSoulData.serializeData(), this.souldataprovider.json(finshedSoulData.getId())));
            }
        });
        return CompletableFuture.allOf(list.toArray((p_253414_) -> new CompletableFuture[p_253414_]));
    }

    @NotNull
    @Override
    public String getName() {
        return "Souldata";
    }

    private void addSpawnerData(EntityType<?> entityType, int power, SpawnerMachineTask.SpawnType type, Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        SpawnerSoul.SoulData data = new SpawnerSoul.SoulData(key, power, type);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(SpawnerSoul.CODEC, data, "spawner/" + key.getNamespace() + "_" + key.getPath()));
    }

    private void addGeneratorData(EntityType<?> entityType, Fluid fluid, int powerpermb, int tickpermb, Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        ResourceLocation entityRL = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        ResourceLocation fluidRL = ForgeRegistries.FLUIDS.getKey(fluid);
        EngineSoul.SoulData data = new EngineSoul.SoulData(entityRL, fluidRL.toString(), powerpermb, tickpermb);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(EngineSoul.CODEC, data, "generator/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    private void addGeneratorData(EntityType<?> entityType, TagKey<Fluid> fluid, int powerpermb, int tickpermb, Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        ResourceLocation entityRL = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        String fluidRL = "#" + fluid.location();
        EngineSoul.SoulData data = new EngineSoul.SoulData(entityRL, fluidRL, powerpermb, tickpermb);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(EngineSoul.CODEC, data, "generator/" + entityRL.getNamespace() + "_" + entityRL.getPath()));
    }

    static class FinshedSoulData<T extends ISoulData> {

        private final Codec<T> codec;
        private final T data;
        private final ResourceLocation id;

        FinshedSoulData(Codec<T> codec, T data, String id) {
            this.codec = codec;
            this.data = data;
            this.id = EnderIO.loc(id);
        }

        FinshedSoulData(Codec<T> codec, T data, ResourceLocation id) {
            this.codec = codec;
            this.data = data;
            this.id = id;
        }

        public JsonObject serializeData() {
            DataResult<JsonElement> element = codec.encodeStart(JsonOps.INSTANCE, data);
            return element.get().left().get().getAsJsonObject();
        }

        public ResourceLocation getId() {
            return this.id;
        }

    }
}
