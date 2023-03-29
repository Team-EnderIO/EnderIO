package com.enderio.machines.data.souldata;

import com.enderio.EnderIO;
import com.enderio.machines.common.blockentity.task.SpawnTask;
import com.enderio.machines.common.souldata.PoweredSpawnerSoul;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class SoulDataProvider implements DataProvider {

    private final DataGenerator.PathProvider generator;

    public SoulDataProvider(DataGenerator pGenerator) {
        this.generator = pGenerator.createPathProvider(DataGenerator.Target.DATA_PACK, "eio_soul");
    }
    @Override
    public void run(CachedOutput pOutput) throws IOException {
        addSoulData(PoweredSpawnerSoul.CODEC, new PoweredSpawnerSoul.SoulData(new ResourceLocation("pig"), 1000, SpawnTask.SpawnType.ENTITYTYPE), "pig", pOutput);
    }

    @Override
    public String getName() {
        return "souldata";
    }

    public <T> void addSoulData(Codec<T> codec, T data, String name, CachedOutput pOutput) {
        JsonElement element = new JsonObject();
        codec.encode(data, JsonOps.INSTANCE, element);
        try {
            DataProvider.saveStable(pOutput, element, this.generator.json(new ResourceLocation(EnderIO.MODID, name)));
        } catch (IOException ioexception) {

        }
    }
}
