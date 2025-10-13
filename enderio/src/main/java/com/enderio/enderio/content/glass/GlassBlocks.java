package com.enderio.enderio.content.glass;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOCreativeTabs;
import com.enderio.regilite.holder.RegiliteBlock;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.ItemRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

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
    public GlassBlocks(DeferredRegister.Blocks blockRegistry, DeferredRegister.Items itemRegistry, GlassIdentifier identifier) {
        glassIdentifier = identifier;
        String name = identifier.glassName();
        CLEAR = register(blockRegistry, itemRegistry, name);
        Map<DyeColor, DeferredBlock<FusedQuartzBlock>> tempMap = new HashMap<>();
        for (DyeColor color: DyeColor.values()) {
            tempMap.put(color,
                register(blockRegistry, itemRegistry, name.concat("_").concat(color.getName()), color)
            );
        }
        COLORS = ImmutableMap.copyOf(tempMap);
    }

    public Stream<DeferredBlock<FusedQuartzBlock>> getAllBlocks() {
        return Stream.concat(Stream.of(CLEAR), COLORS.values().stream());
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
    private DeferredBlock<FusedQuartzBlock> register(DeferredRegister.Blocks blockRegistry, DeferredRegister.Items itemRegistry, String name) {
        var block = blockRegistry
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
                    .isViewBlocking(GlassBlocks::never));

        itemRegistry.registerSimpleBlockItem(block);
        return block;
    }

    /**
     * Register a colored glass.
     */
    private DeferredBlock<FusedQuartzBlock> register(DeferredRegister.Blocks blockRegistry, DeferredRegister.Items itemRegistry, String name, DyeColor color) {
        var block = blockRegistry
            .registerBlock(name,
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
                    .mapColor(color));

        itemRegistry.registerSimpleBlockItem(block);
        return block;
    }

    public GlassIdentifier getGlassIdentifier() {
        return glassIdentifier;
    }
}
