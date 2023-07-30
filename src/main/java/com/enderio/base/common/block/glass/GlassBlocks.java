package com.enderio.base.common.block.glass;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.tag.EIOTags;
import com.google.common.collect.ImmutableMap;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.tterrag.registrate.providers.ProviderType.LANG;
import static com.tterrag.registrate.util.nullness.NonNullBiConsumer.noop;

/**
 * Container helper for the fused glass/quartz blocks as theres a lot, and this will tidy stuff up.
 */
public class GlassBlocks {
    public final BlockEntry<FusedQuartzBlock> CLEAR;
    public final Map<DyeColor, BlockEntry<FusedQuartzBlock>> COLORS;

    private final GlassIdentifier glassIdentifier;

    /**
     * Create the entire color family for this configuration of fused glass.
     */
    public GlassBlocks(Registrate registrate, GlassIdentifier identifier) {
        glassIdentifier = identifier;
        String name = identifier.glassName();
        CLEAR = register(registrate, name);
        Map<DyeColor, BlockEntry<FusedQuartzBlock>> tempMap = new HashMap<>();
        for (DyeColor color: DyeColor.values()) {
            tempMap.put(color,
                register(registrate, name.concat("_").concat(color.getName()), color)
            );
        }
        COLORS = ImmutableMap.copyOf(tempMap);
    }

    public Stream<BlockEntry<FusedQuartzBlock>> getAllBlocks() {
        return Stream.concat(Stream.of(CLEAR), COLORS.values().stream());
    }

    private ResourceLocation getModelFile() {
        return glassIdentifier.explosion_resistance() ? EnderIO.loc("block/fused_quartz") : EnderIO.loc("block/clear_glass");
    }

    // Dirty dirty. TODO: Just access transforms for these in Blocks??
    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }

    private static boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
        return false;
    }

    /**
     * Register a non-colored glass
     */
    private BlockEntry<FusedQuartzBlock> register(Registrate registrate, String name) {
        var builder =  registrate
            .block(name, props -> new FusedQuartzBlock(props, glassIdentifier, null))
            .tag(glassIdentifier.explosion_resistance() ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS)
            .setData(LANG, noop())
            .blockstate((con, prov) -> prov.simpleBlock(con.get(), prov.models().getExistingFile(getModelFile())))
            .properties(props -> props
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never))
            .item()
            .tab(EIOCreativeTabs.BLOCKS)
            .tag(glassIdentifier.explosion_resistance() ? EIOTags.Items.FUSED_QUARTZ : EIOTags.Items.CLEAR_GLASS)
            .tag(EIOTags.Items.GLASS_TAGS.get(glassIdentifier));
        if (glassIdentifier.lighting() == GlassLighting.EMITTING && glassIdentifier.explosion_resistance())
            builder.tag(EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ);
        if (glassIdentifier.lighting() == GlassLighting.BLOCKING && glassIdentifier.explosion_resistance())
            builder.tag(EIOTags.Items.DARK_FUSED_QUARTZ);
        return builder.build().register();
    }

    /**
     * Register a colored glass.
     */
    private BlockEntry<FusedQuartzBlock> register(Registrate registrate, String name, DyeColor color) {
        return registrate
            .block(name, props -> new FusedQuartzBlock(props, glassIdentifier, color))
            .setData(LANG, noop())
            .blockstate((con, prov) -> prov.simpleBlock(con.get(), prov.models().getExistingFile(getModelFile())))
            .color(() -> () -> (p_92567_, p_92568_, p_92569_, p_92570_) -> color.getMapColor().col)
            .properties(props -> props
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never)
                .mapColor(color))
            .item()
            .tab(EIOCreativeTabs.BLOCKS)
            .tag(EIOTags.Items.GLASS_TAGS.get(glassIdentifier))
            .color(() -> () -> (ItemColor) (p_92672_, p_92673_) -> color.getMapColor().col)
            .build()
            .register();
    }

    public GlassIdentifier getGlassIdentifier() {
        return glassIdentifier;
    }
}
