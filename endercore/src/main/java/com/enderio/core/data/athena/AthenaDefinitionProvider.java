package com.enderio.core.data.athena;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * DataProvider for Athena definition files.
 */
public abstract class AthenaDefinitionProvider implements DataProvider {
    private final PackOutput.PathProvider athenaPathProvider;
    public final String modId;

    private final Map<Block, Athena> definitions = new HashMap<>();

    public AthenaDefinitionProvider(PackOutput output, String modId) {
        this.athenaPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "athena");
        this.modId = modId;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        definitions.clear();
        registerDefinitions();
        List<CompletableFuture<?>> list = new ArrayList<>();
        for (var entry : definitions.entrySet()) {
            Path path = athenaPathProvider.json(entry.getKey().builtInRegistryHolder().key().location());
            JsonElement element = Athena.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
            list.add(DataProvider.saveStable(cache, element, path));
        }

        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Athena Definitions - " + modId;
    }

    public record Athena(ResourceLocation loader, Map<String, ResourceLocation> textures, int tint, String renderType) {
        public static Codec<Athena> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ResourceLocation.CODEC.fieldOf("athena:loader").forGetter(Athena::loader),
                Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).fieldOf("ctm_textures").forGetter(Athena::textures),
                Codec.INT.fieldOf("tint").forGetter(Athena::tint), Codec.STRING.fieldOf("render_type").forGetter(Athena::renderType))
            .apply(instance, Athena::new));
    }

    public abstract void registerDefinitions();

    protected void register(Block block, Athena definition) {
        definitions.put(block, definition);
    }

    public void registerSimple(Block block, ResourceLocation center, ResourceLocation empty, ResourceLocation horizontal, ResourceLocation vertical,
        ResourceLocation particle, int tint, String renderType) {
        Map<String, ResourceLocation> map = new HashMap<>();
        map.put("center", center);
        map.put("empty", empty);
        map.put("horizontal", horizontal);
        map.put("vertical", vertical);
        map.put("particle", particle);
        Athena definition = new Athena(ResourceLocation.parse("athena:ctm"), map, tint, renderType);
        register(block, definition);
    }
}
