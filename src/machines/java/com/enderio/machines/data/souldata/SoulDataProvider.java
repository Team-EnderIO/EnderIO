package com.enderio.machines.data.souldata;

import com.enderio.EnderIO;
import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import com.enderio.machines.common.souldata.ISoulData;
import com.enderio.machines.common.souldata.SpawnerSoul;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SoulDataProvider implements DataProvider {

    private final PackOutput.PathProvider souldataprovider;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public SoulDataProvider(PackOutput packOutput) {
        this.souldataprovider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "eio_soul");
    }

    public void buildSoulData(Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        addSpawnerData(EntityType.ALLAY, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.AXOLOTL, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.BAT, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.BEE, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.BLAZE, 6000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CAT, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CAVE_SPIDER, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CHICKEN, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.COD, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.COW, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.CREEPER, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.DOLPHIN, 1500, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.DONKEY, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.DROWNED, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ELDER_GUARDIAN, 8000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ENDERMAN, 6000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ENDERMITE, 800, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ENDER_DRAGON, 1000000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.EVOKER, 6000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.FOX, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.FROG, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GHAST, 6000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GIANT, 10000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GOAT, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.GUARDIAN, 5000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.HOGLIN, 6000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.HUSK, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.HORSE, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ILLUSIONER, 6000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.IRON_GOLEM, 7000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.LLAMA, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.MAGMA_CUBE, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.MULE, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.MOOSHROOM, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.OCELOT, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PANDA, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PARROT, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PHANTOM, 5000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIG, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIGLIN, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIGLIN_BRUTE, 4500, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PILLAGER, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.POLAR_BEAR, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PUFFERFISH, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.RABBIT, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.RAVAGER, 8000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SALMON, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SHEEP, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SHULKER, 6000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SKELETON, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SILVERFISH, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SKELETON_HORSE, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SLIME, 2000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SNOW_GOLEM, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SPIDER, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.SQUID, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.STRIDER, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TADPOLE, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TURTLE, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TRADER_LLAMA, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.TROPICAL_FISH, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.VEX, 1000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.VILLAGER, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.VINDICATOR, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WARDEN, 100000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WITCH, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WITHER, 100000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.WOLF, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOGLIN, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE_HORSE, 3000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIE_VILLAGER, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.ZOMBIFIED_PIGLIN, 4000, SpawnerMachineTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);

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

    @Override
    public String getName() {
        return "Souldata";
    }

    private void addSpawnerData(EntityType<?> entityType, int power, SpawnerMachineTask.SpawnType type, Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        SpawnerSoul.SoulData data = new SpawnerSoul.SoulData(key, power, type);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(SpawnerSoul.CODEC, data, "spawner/" + key.getNamespace() + "_" + key.getPath()));
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
