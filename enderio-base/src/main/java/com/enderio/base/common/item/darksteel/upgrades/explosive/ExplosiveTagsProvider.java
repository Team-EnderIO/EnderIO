package com.enderio.base.common.item.darksteel.upgrades.explosive;

import com.enderio.base.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ExplosiveTagsProvider extends BlockTagsProvider {

    public ExplosiveTagsProvider(DataGenerator dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, EnderIO.MODID, existingFileHelper);
    }

    private static final List<Block> TERRECOTTA = List.of(
        Blocks.TERRACOTTA,
        Blocks.WHITE_TERRACOTTA,
        Blocks.ORANGE_TERRACOTTA,
        Blocks.MAGENTA_TERRACOTTA,
        Blocks.LIGHT_BLUE_TERRACOTTA,
        Blocks.YELLOW_TERRACOTTA,
        Blocks.LIME_TERRACOTTA,
        Blocks.PINK_TERRACOTTA,
        Blocks.GRAY_TERRACOTTA,
        Blocks.LIGHT_GRAY_TERRACOTTA,
        Blocks.CYAN_TERRACOTTA,
        Blocks.PURPLE_TERRACOTTA,
        Blocks.BLUE_TERRACOTTA,
        Blocks.BROWN_TERRACOTTA,
        Blocks.GREEN_TERRACOTTA,
        Blocks.RED_TERRACOTTA,
        Blocks.BLACK_TERRACOTTA);

    private static final List<Block> INFESTED = List.of(
        Blocks.INFESTED_CHISELED_STONE_BRICKS,
        Blocks.INFESTED_COBBLESTONE,
        Blocks.INFESTED_CRACKED_STONE_BRICKS,
        Blocks.INFESTED_DEEPSLATE,
        Blocks.INFESTED_STONE,
        Blocks.INFESTED_STONE_BRICKS,
        Blocks.INFESTED_MOSSY_STONE_BRICKS);

    private static final List<Block> DEEPSLATE = List.of(
        Blocks.DEEPSLATE,
        Blocks.DEEPSLATE_BRICKS,
        Blocks.DEEPSLATE_BRICK_SLAB,
        Blocks.DEEPSLATE_TILES,
        Blocks.DEEPSLATE_TILE_SLAB,
        Blocks.CHISELED_DEEPSLATE,
        Blocks.COBBLED_DEEPSLATE,
        Blocks.COBBLED_DEEPSLATE_SLAB,
        Blocks.POLISHED_DEEPSLATE,
        Blocks.POLISHED_DEEPSLATE_SLAB,
        Blocks.CRACKED_DEEPSLATE_TILES,
        Blocks.CRACKED_DEEPSLATE_BRICKS);

    private static final List<Block> CONCRETE = List.of(
        Blocks.BLACK_CONCRETE,
        Blocks.BLUE_CONCRETE,
        Blocks.CYAN_CONCRETE,
        Blocks.BROWN_CONCRETE,
        Blocks.GRAY_CONCRETE,
        Blocks.GREEN_CONCRETE,
        Blocks.LIME_CONCRETE,
        Blocks.MAGENTA_CONCRETE,
        Blocks.ORANGE_CONCRETE,
        Blocks.PINK_CONCRETE,
        Blocks.LIGHT_GRAY_CONCRETE,
        Blocks.PURPLE_CONCRETE,
        Blocks.RED_CONCRETE,
        Blocks.YELLOW_CONCRETE,
        Blocks.LIGHT_BLUE_CONCRETE,
        Blocks.WHITE_CONCRETE);

    @Override
    protected void addTags() {
        TagAppender<Block> tagProv = tag(EIOTags.Blocks.DARK_STEEL_EXPLODABLE_STONE)
            .addTag(Tags.Blocks.STONE)
            .addTag(Tags.Blocks.NETHERRACK)
            .addTag(Tags.Blocks.END_STONES)
            .addTag(Tags.Blocks.SANDSTONE)
            .addTag(Tags.Blocks.COBBLESTONE)
            .addTag(BlockTags.STONE_BRICKS)
            .addTag(BlockTags.BASE_STONE_NETHER)
            .addTag(BlockTags.SLABS)
            .add(Blocks.NETHER_BRICKS)
            .add(Blocks.END_STONE_BRICKS)
            .add(Blocks.POLISHED_BASALT)
            .add(Blocks.SMOOTH_BASALT)
            .add(Blocks.MOSS_BLOCK)
            .add(Blocks.TUFF)
            .add(Blocks.RED_NETHER_BRICKS)
            .add(Blocks.GILDED_BLACKSTONE)
            .add(Blocks.POLISHED_BLACKSTONE)
            .add(Blocks.CHISELED_POLISHED_BLACKSTONE)
            .add(Blocks.POLISHED_BLACKSTONE_BRICKS)
            .add(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        addAll(tagProv, INFESTED);
        addAll(tagProv, TERRECOTTA);
        addAll(tagProv, DEEPSLATE);
        addAll(tagProv, CONCRETE);

        //TODO: What about these? quartz blocks, prismarine blocks, coral blocks, amethyst blocks, glazed terracota

        tag(EIOTags.Blocks.DARK_STEEL_EXPLODABLE_DIRT)
            .addTag(Tags.Blocks.DIRT)
            .addTag(Tags.Blocks.SAND)
            .addTag(Tags.Blocks.GRAVEL)
            .add(Blocks.DIRT_PATH)
            .add(Blocks.ROOTED_DIRT)
            .add(Blocks.FARMLAND)
            .add(Blocks.CRIMSON_NYLIUM)
            .add(Blocks.WARPED_NYLIUM)
            .add(Blocks.SOUL_SAND)
            .add(Blocks.SOUL_SOIL)
            .add(Blocks.SNOW_BLOCK)
            .add(Blocks.POWDER_SNOW)
            .add(Blocks.CLAY);
    }



    private void addAll(TagAppender<Block> tag, List<Block> blocks) {
        for(Block b : blocks) {
            tag.add(b);
        }
    }
}
