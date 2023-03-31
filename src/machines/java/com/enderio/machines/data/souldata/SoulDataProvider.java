package com.enderio.machines.data.souldata;

import com.enderio.EnderIO;
import com.enderio.machines.common.blockentity.task.SpawnTask;
import com.enderio.machines.common.souldata.ISoulData;
import com.enderio.machines.common.souldata.PoweredSpawnerSoul;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class SoulDataProvider implements DataProvider {

    private final DataGenerator.PathProvider generator;

    public SoulDataProvider(DataGenerator pGenerator) {
        this.generator = pGenerator.createPathProvider(DataGenerator.Target.DATA_PACK, "eio_soul");
    }
    @Override
    public void run(CachedOutput pOutput) throws IOException {
        Set<ResourceLocation> set = Sets.newHashSet();
        buildSoulData(finshedSoulData -> {
            if (!set.add(finshedSoulData.getId())) {
                throw new IllegalStateException("Duplicate recipe " + finshedSoulData.getId());
            } else {
                saveRecipe(pOutput, finshedSoulData.serializeData(), this.generator.json(finshedSoulData.getId()));
            }
        });
    }

    @Override
    public String getName() {
        return "souldata";
    }

    public void buildSoulData(Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        addSpawnerData(EntityType.ALLAY, 1000, SpawnTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
        addSpawnerData(EntityType.PIG, 1000, SpawnTask.SpawnType.ENTITYTYPE, finshedSoulDataConsumer);
    }

    private void addSpawnerData(EntityType<?> entityType, int power, SpawnTask.SpawnType type, Consumer<FinshedSoulData<?>> finshedSoulDataConsumer) {
        ResourceLocation key = Registry.ENTITY_TYPE.getKey(entityType);
        PoweredSpawnerSoul.SoulData data = new PoweredSpawnerSoul.SoulData(key, power, type);
        finshedSoulDataConsumer.accept(new FinshedSoulData<>(PoweredSpawnerSoul.CODEC, data, key.getPath()));
    }

    private static void saveRecipe(CachedOutput pOutput, JsonObject pSoulDataJson, Path pPath) {
        try {
            DataProvider.saveStable(pOutput, pSoulDataJson, pPath);
        } catch (IOException ioexception) {
            EnderIO.LOGGER.error("Couldn't save soul data {}", pPath, ioexception);
        }

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
