package com.enderio.base.common.block.glass;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.common.registry.EnderBlockRegistry;
import com.enderio.core.common.registry.EnderDeferredBlock;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Container helper for the fused glass/quartz blocks as theres a lot, and this will tidy stuff up.
 */
public class GlassBlocks {
    public final EnderDeferredBlock<FusedQuartzBlock> CLEAR;
    public final Map<DyeColor, EnderDeferredBlock<FusedQuartzBlock>> COLORS;

    private final GlassIdentifier glassIdentifier;

    /**
     * Create the entire color family for this configuration of fused glass.
     */
    public GlassBlocks(EnderBlockRegistry registry, GlassIdentifier identifier) {
        glassIdentifier = identifier;
        String name = identifier.glassName();
        CLEAR = register(registry, name);
        Map<DyeColor, EnderDeferredBlock<FusedQuartzBlock>> tempMap = new HashMap<>();
        for (DyeColor color: DyeColor.values()) {
            tempMap.put(color,
                register(registry, name.concat("_").concat(color.getName()), color)
            );
        }
        COLORS = ImmutableMap.copyOf(tempMap);
    }

    public Stream<EnderDeferredBlock<FusedQuartzBlock>> getAllBlocks() {
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
    private EnderDeferredBlock<FusedQuartzBlock> register(EnderBlockRegistry registry, String name) {
        var block =  registry
            .register(name, () -> new FusedQuartzBlock(BlockBehaviour.Properties.of()
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never), glassIdentifier, null))
            .addBlockTags(glassIdentifier.explosion_resistance() ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS)
            .setTranslation("")
            .setBlockStateProvider(BlockStateProvider::simpleBlock)
            .createBlockItem()
            .setTab(EIOCreativeTabs.BLOCKS)
            .addBlockItemTags(glassIdentifier.explosion_resistance() ? EIOTags.Items.FUSED_QUARTZ : EIOTags.Items.CLEAR_GLASS)
            .addBlockItemTags(EIOTags.Items.GLASS_TAGS.get(glassIdentifier)); //TODO chainable tags

        if (glassIdentifier.lighting() == GlassLighting.EMITTING && glassIdentifier.explosion_resistance()) {
            block.addBlockItemTags(EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ); //TODO chainable tags
        }

        if (glassIdentifier.lighting() == GlassLighting.BLOCKING && glassIdentifier.explosion_resistance()) {
            block.addBlockItemTags(EIOTags.Items.DARK_FUSED_QUARTZ); //TODO chainable tags
        }

        return block.finishBlockItem();
    }

    /**
     * Register a colored glass.
     */
    private EnderDeferredBlock<FusedQuartzBlock> register(EnderBlockRegistry registry, String name, DyeColor color) {
        return registry
            .register(name, () -> new FusedQuartzBlock(BlockBehaviour.Properties.of()
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never)
                .mapColor(color), glassIdentifier, color))
            .setBlockStateProvider(BlockStateProvider::simpleBlock)
            .setTranslation("")
            //.color(() -> () -> (p_92567_, p_92568_, p_92569_, p_92570_) -> color.getMapColor().col) TODO
            .createBlockItem()
            .setTab(EIOCreativeTabs.BLOCKS)
            .addBlockItemTags(EIOTags.Items.GLASS_TAGS.get(glassIdentifier))
            //.color(() -> () -> (ItemColor) (p_92672_, p_92673_) -> color.getMapColor().col)
            .finishBlockItem();
    }

    public GlassIdentifier getGlassIdentifier() {
        return glassIdentifier;
    }
}
