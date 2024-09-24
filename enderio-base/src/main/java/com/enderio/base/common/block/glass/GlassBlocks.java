package com.enderio.base.common.block.glass;

import com.enderio.EnderIOBase;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.regilite.Regilite;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Container helper for the fused glass/quartz blocks as theres a lot, and this will tidy stuff up.
 */
public class GlassBlocks {
    public final DeferredBlock<FusedQuartzBlock> CLEAR;
    public final Map<DyeColor, DeferredBlock<FusedQuartzBlock>> COLORS;

    private final GlassIdentifier glassIdentifier;

    /**
     * Create the entire color family for this configuration of fused glass.
     */
    public GlassBlocks(Regilite regilite, GlassIdentifier identifier) {
        glassIdentifier = identifier;
        String name = identifier.glassName();
        CLEAR = register(regilite, name);
        Map<DyeColor, DeferredBlock<FusedQuartzBlock>> tempMap = new HashMap<>();
        for (DyeColor color: DyeColor.values()) {
            tempMap.put(color,
                register(regilite, name.concat("_").concat(color.getName()), color)
            );
        }
        COLORS = ImmutableMap.copyOf(tempMap);
    }

    public Stream<DeferredBlock<FusedQuartzBlock>> getAllBlocks() {
        return Stream.concat(Stream.of(CLEAR), COLORS.values().stream());
    }

    private ResourceLocation getModelFile() {
        return glassIdentifier.explosion_resistance() ? EnderIOBase.loc("block/fused_quartz") : EnderIOBase.loc("block/clear_glass");
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
    private DeferredBlock<FusedQuartzBlock> register(Regilite regilite, String name) {
        var block = regilite.blocks()
            .create(name,
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
            .translation("")
            .blockStateProvider((cons, ctx) -> cons.simpleBlock(ctx.get(), cons.models().getExistingFile(getModelFile())))
            .tag(glassIdentifier.explosion_resistance() ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS);

        block.createSimpleBlockItem(item -> {
            item.tab(EIOCreativeTabs.BLOCKS)
                .tags(
                    glassIdentifier.explosion_resistance() ? EIOTags.Items.FUSED_QUARTZ : EIOTags.Items.CLEAR_GLASS,
                    EIOTags.Items.GLASS_TAGS.get(glassIdentifier)
                )
                .modelProvider((prov, ctx) -> prov.withExistingParent(ctx.getName(), getModelFile()));

            if (glassIdentifier.lighting() == GlassLighting.EMITTING && glassIdentifier.explosion_resistance()) {
                item.tag(EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ);
            }

            if (glassIdentifier.lighting() == GlassLighting.BLOCKING && glassIdentifier.explosion_resistance()) {
                item.tag(EIOTags.Items.DARK_FUSED_QUARTZ);
            }
        });

        return block.finish();
    }

    /**
     * Register a colored glass.
     */
    private DeferredBlock<FusedQuartzBlock> register(Regilite regilite, String name, DyeColor color) {
        var block = regilite.blocks()
            .create(name,
                p -> new FusedQuartzBlock(p, glassIdentifier, color),
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
            .translation("")
            .blockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(getModelFile())))
            .blockColor(() -> () -> (state, level, pos, tintIndex) -> color.getMapColor().col)
            .tag(glassIdentifier.explosion_resistance() ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS);

        block.createSimpleBlockItem(item -> {
            item.tab(EIOCreativeTabs.BLOCKS)
                .itemColor(() -> () -> (stack, tintIndex) -> color.getMapColor().col)
                .tags(
                    glassIdentifier.explosion_resistance() ? EIOTags.Items.FUSED_QUARTZ : EIOTags.Items.CLEAR_GLASS,
                    EIOTags.Items.GLASS_TAGS.get(glassIdentifier)
                )
                .modelProvider((prov, ctx) -> prov.withExistingParent(ctx.getName(), getModelFile()));

            if (glassIdentifier.lighting() == GlassLighting.EMITTING && glassIdentifier.explosion_resistance()) {
                item.tag(EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ);
            }

            if (glassIdentifier.lighting() == GlassLighting.BLOCKING && glassIdentifier.explosion_resistance()) {
                item.tag(EIOTags.Items.DARK_FUSED_QUARTZ);
            }
        });

        return block.finish();
    }

    public GlassIdentifier getGlassIdentifier() {
        return glassIdentifier;
    }
}
