package com.enderio.enderio.datagen.client.models.block;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.init.EIOBlocks;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AthenaProvider implements DataProvider {
    private final PackOutput.PathProvider athenaPathProvider;
    public final String modId;

    private final Map<Block, Athena> definitions = new HashMap<>();

    public AthenaProvider(PackOutput output) {
        this(output, EnderIO.MOD_ID);
    }

    public AthenaProvider(PackOutput output, String modId) {
        this.athenaPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "athena");
        this.modId = modId;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        registerDefinitions();
        Function<Block, Path> pathGetter = block -> athenaPathProvider.json(block.builtInRegistryHolder().key().identifier());
        return DataProvider.saveAll(cache, Athena.CODEC, pathGetter, definitions);
    }

    @Override
    public String getName() {
        return "Athena Definitions - " + modId;
    }

    public record Athena(Identifier loader, Map<String, Identifier> textures, int tint, String renderType) {
        public static Codec<Athena> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("athena:loader").forGetter(Athena::loader),
            Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("ctm_textures").forGetter(Athena::textures),
            Codec.INT.fieldOf("tint").forGetter(Athena::tint),
            Codec.STRING.fieldOf("render_type").forGetter(Athena::renderType)
        ).apply(instance, Athena::new));
    }


    public void registerDefinitions() {
        for (var glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            for (var block : glassBlocks.getAllBlocks().toList()) {
                if (block.get().glassIdentifier().explosionResistance()) {
                    registerSimple(block.get(), EnderIO.id("block/ctm/fused_quartz/center"), EnderIO.id("block/ctm/fused_quartz/empty"),
                        EnderIO.id("block/ctm/fused_quartz/horizontal"), EnderIO.id("block/ctm/fused_quartz/vertical"),
                        EnderIO.id("block/ctm/fused_quartz/particle"), 0, "cutout");

                } else {
                    registerSimple(block.get(), EnderIO.id("block/ctm/clear_glass/center"), EnderIO.id("block/ctm/clear_glass/empty"),
                        EnderIO.id("block/ctm/clear_glass/horizontal"), EnderIO.id("block/ctm/clear_glass/vertical"),
                        EnderIO.id("block/ctm/clear_glass/particle"), 0, "cutout");

                }
            }
        }
    }

    private void register(Block block, Athena definition) {
        definitions.put(block, definition);
    }

    public void registerSimple(Block block, Identifier center, Identifier empty, Identifier horizontal, Identifier vertical,
        Identifier particle, int tint, String renderType) {
        Map<String, Identifier> map = new HashMap<>();
        map.put("center", center);
        map.put("empty", empty);
        map.put("horizontal", horizontal);
        map.put("vertical", vertical);
        map.put("particle", particle);
        Athena definition = new Athena(Identifier.parse("athena:ctm"), map, tint, renderType);
        register(block, definition);
    }
}
