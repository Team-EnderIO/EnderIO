package com.enderio.enderio.datagen.common.souldata;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.souldata.FarmSoul;
import com.enderio.enderio.foundation.souldata.SolarSoul;
import com.enderio.enderio.foundation.souldata.SoulData;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Level;
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
