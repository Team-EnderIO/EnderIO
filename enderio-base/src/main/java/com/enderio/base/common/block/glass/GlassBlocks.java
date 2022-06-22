package com.enderio.base.common.block.glass;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.common.tag.EIOTags;
import com.google.common.collect.ImmutableMap;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

/**
 * Container helper for the fused glass/quartz blocks as theres a lot, and this will tidy stuff up.
 */
public class GlassBlocks {
    public final BlockEntry<FusedQuartzBlock> CLEAR;
    public Map<DyeColor, BlockEntry<FusedQuartzBlock>> COLORS;

    private final GlassIdentifier glassIdentifier;

    /**
     * Create the entire color family for this configuration of fused glass.
     */
    public GlassBlocks(Registrate registrate, GlassIdentifier identifier) {
        glassIdentifier = identifier;
        String name = identifier.glassName();
        String english = createEnglishGlassName(identifier);
        CLEAR = register(registrate, name, english);
        Map<DyeColor, BlockEntry<FusedQuartzBlock>> tempMap = new HashMap<>();
        for (DyeColor color: DyeColor.values()) {
            tempMap.put(color,
                register(registrate, name.concat("_").concat(color.getName()),
                    createEnglishPrefix(color).concat(english),
                    color)
            );
        }
        COLORS = ImmutableMap.copyOf(tempMap);
    }

    private ResourceLocation getModelFile() {
        return glassIdentifier.explosion_resistance() ? EnderIO.loc("block/fused_quartz") : EnderIO.loc("block/clear_glass");
    }

    private static String createEnglishPrefix(DyeColor color) {
        StringBuilder builder = new StringBuilder();
        boolean nextUpper = true;
        for (char c : color.getName().replace("_", " ").toCharArray()) {
            if (nextUpper) {
                builder.append(Character.toUpperCase(c));
                nextUpper = false;
                continue;
            }
            if (c == ' ') {
                nextUpper = true;
            }
            builder.append(c);
        }
        builder.append(" ");
        return builder.toString();
    }
    private static String createEnglishGlassName(GlassIdentifier identifier) {
        StringBuilder main = new StringBuilder();
        if (identifier.lighting() != GlassLighting.NONE) {
            main.append(identifier.lighting().englishName());
            main.append(" ");
        }
        if (identifier.explosion_resistance()) {
            main.append("Fused Quartz");
        } else {
            main.append("Clear Glass");
        }
        return main.toString();
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
    private BlockEntry<FusedQuartzBlock> register(Registrate registrate, String name, String english) {
        return registrate
            .block(name, props -> new FusedQuartzBlock(props, glassIdentifier))
            .tag(glassIdentifier.explosion_resistance() ? EIOTags.Blocks.FUSED_QUARTZ : EIOTags.Blocks.CLEAR_GLASS)
            .lang(english)
            .blockstate((con, prov) -> prov.simpleBlock(con.get(), prov.models().getExistingFile(getModelFile())))
            .addLayer(() -> RenderType::cutout)
            .properties(props -> props
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never))
            .item(FusedQuartzItem::new)
            .tab(() -> EIOCreativeTabs.BLOCKS)
            .tag(glassIdentifier.explosion_resistance() ? EIOTags.Items.FUSED_QUARTZ : EIOTags.Items.CLEAR_GLASS)
            .tag(EIOTags.Items.GLASS_TAGS.get(glassIdentifier))
            .build()
            .register();
    }

    /**
     * Register a colored glass.
     */
    private BlockEntry<FusedQuartzBlock> register(Registrate registrate, String name, String english, DyeColor color) {
        return registrate
            .block(name, props -> new FusedQuartzBlock(props, glassIdentifier))
            .lang(english)
            .blockstate((con, prov) -> prov.simpleBlock(con.get(), prov.models().getExistingFile(getModelFile())))
            .addLayer(() -> RenderType::cutout)
            .color(() -> () -> (p_92567_, p_92568_, p_92569_, p_92570_) -> color.getMaterialColor().col)
            .properties(props -> props
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never)
                .color(color.getMaterialColor()))
            .item(FusedQuartzItem::new)
            .tab(() -> EIOCreativeTabs.BLOCKS)
            .tag(EIOTags.Items.GLASS_TAGS.get(glassIdentifier))
            .color(() -> () -> (ItemColor) (p_92672_, p_92673_) -> color.getMaterialColor().col)
            .build()
            .register();
    }

    public GlassIdentifier getGlassIdentifier() {
        return glassIdentifier;
    }
}
