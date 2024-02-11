package com.enderio.base.common.block.glass;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.regilite.holder.RegiliteBlock;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.ItemRegistry;
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Container helper for the fused glass/quartz blocks as theres a lot, and this will tidy stuff up.
 */
public class GlassBlocks {
    public final RegiliteBlock<FusedQuartzBlock> CLEAR;
    public final Map<DyeColor, RegiliteBlock<FusedQuartzBlock>> COLORS;

    private final GlassIdentifier glassIdentifier;

    /**
     * Create the entire color family for this configuration of fused glass.
     */
    public GlassBlocks(BlockRegistry blockRegistry, ItemRegistry itemRegistry, GlassIdentifier identifier) {
        glassIdentifier = identifier;
        String name = identifier.glassName();
        CLEAR = register(blockRegistry, itemRegistry, name);
        Map<DyeColor, RegiliteBlock<FusedQuartzBlock>> tempMap = new HashMap<>();
        for (DyeColor color: DyeColor.values()) {
            tempMap.put(color,
                register(blockRegistry, itemRegistry, name.concat("_").concat(color.getName()), color)
            );
        }
        COLORS = ImmutableMap.copyOf(tempMap);
    }

    public Stream<RegiliteBlock<FusedQuartzBlock>> getAllBlocks() {
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
    private RegiliteBlock<FusedQuartzBlock> register(BlockRegistry blockRegistry, ItemRegistry itemRegistry, String name) {
        var builder = blockRegistry
            .registerBlock(name,
                p -> new FusedQuartzBlock(p, glassIdentifier, null),
                BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(0.3F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .isValidSpawn(GlassBlocks::never)
                    .isRedstoneConductor(GlassBlocks::never)
                    .isSuffocating(GlassBlocks::never)
                    .isViewBlocking(GlassBlocks::never))
            .setBlockStateProvider((cons, b) -> cons.simpleBlock(b, cons.models().getExistingFile(getModelFile())))
            .addBlockTags(
                glassIdentifier.explosion_resistance() ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS
            )
            .createBlockItem(itemRegistry)
            .setTab(EIOCreativeTabs.BLOCKS)
            .addBlockItemTags(
                glassIdentifier.explosion_resistance() ? EIOTags.Items.FUSED_QUARTZ : EIOTags.Items.CLEAR_GLASS,
                EIOTags.Items.GLASS_TAGS.get(glassIdentifier)
            );

        if (glassIdentifier.lighting() == GlassLighting.EMITTING && glassIdentifier.explosion_resistance()) {
            builder.addBlockItemTags(EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ);
        }

        if (glassIdentifier.lighting() == GlassLighting.BLOCKING && glassIdentifier.explosion_resistance()) {
            builder.addBlockItemTags(EIOTags.Items.DARK_FUSED_QUARTZ);
        }

        // TODO - handle LANG noop in Regilite
        /*var builder = registrate
            .block(name, props -> new FusedQuartzBlock(props, glassIdentifier, null))
            .tag(glassIdentifier.explosion_resistance() ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS)
            .setData(LANG, noop());*/

        return builder.finishBlockItem();
    }

    /**
     * Register a colored glass.
     */
    private RegiliteBlock<FusedQuartzBlock> register(BlockRegistry blockRegistry, ItemRegistry itemRegistry, String name, DyeColor color) {
        var builder = blockRegistry
            .registerBlock(name,
                p -> new FusedQuartzBlock(p, glassIdentifier, null),
                BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(0.3F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .isValidSpawn(GlassBlocks::never)
                    .isRedstoneConductor(GlassBlocks::never)
                    .isSuffocating(GlassBlocks::never)
                    .isViewBlocking(GlassBlocks::never)
                    .mapColor(color))
            .setBlockStateProvider((cons, b) -> cons.simpleBlock(b, cons.models().getExistingFile(getModelFile())))
            .setColorSupplier((p_92567_, p_92568_, p_92569_, p_92570_) -> color.getMapColor().col)
            .addBlockTags(
                glassIdentifier.explosion_resistance() ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS
            )
            .createBlockItem(itemRegistry)
            .setTab(EIOCreativeTabs.BLOCKS)
            .addBlockItemTags(
                glassIdentifier.explosion_resistance() ? EIOTags.Items.FUSED_QUARTZ : EIOTags.Items.CLEAR_GLASS,
                EIOTags.Items.GLASS_TAGS.get(glassIdentifier)
            );

        if (glassIdentifier.lighting() == GlassLighting.EMITTING && glassIdentifier.explosion_resistance()) {
            builder.addBlockItemTags(EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ);
        }

        if (glassIdentifier.lighting() == GlassLighting.BLOCKING && glassIdentifier.explosion_resistance()) {
            builder.addBlockItemTags(EIOTags.Items.DARK_FUSED_QUARTZ);
        }

        // TODO - handle LANG noop in Regilite
        /*var builder = registrate
            .block(name, props -> new FusedQuartzBlock(props, glassIdentifier, null))
            .tag(glassIdentifier.explosion_resistance() ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS)
            .setData(LANG, noop());*/

        // TODO: Temporary workaround, setColorSupplier returns RegiliteItem, not RegiliteBlockItem.
        var block = builder.finishBlockItem();
        builder.setColorSupplier((p_92672_, p_92673_) -> color.getMapColor().col);

        return block;
    }

    public GlassIdentifier getGlassIdentifier() {
        return glassIdentifier;
    }
}
