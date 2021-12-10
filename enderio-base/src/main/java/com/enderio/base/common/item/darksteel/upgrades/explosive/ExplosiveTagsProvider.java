package com.enderio.base.common.item.darksteel.upgrades.explosive;

import com.enderio.base.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ExplosiveTagsProvider extends BlockTagsProvider {

    public ExplosiveTagsProvider(DataGenerator dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(EIOTags.Blocks.DARK_STEEL_EXPLODABLE).addTags(Tags.Blocks.STONE, Tags.Blocks.NETHERRACK, Tags.Blocks.END_STONES, Tags.Blocks.SANDSTONE,
            Tags.Blocks.COBBLESTONE, BlockTags.STONE_BRICKS, BlockTags.BASE_STONE_NETHER).add(Blocks.NETHER_BRICKS);

        //TODO: Sand tags and check list below from 1.12
    }

    //    private static final Things STONES =.
    //        add(Blocks.END_BRICKS).add(Blocks.END_STONE).add(Blocks.MOSSY_COBBLESTONE)
    //        .add(Blocks.MONSTER_EGG).add(Blocks.HARDENED_CLAY).add(Blocks.NETHER_BRICK).add(Blocks.NETHER_BRICK_FENCE).add(Blocks.NETHER_BRICK_STAIRS)
    //        .add(Blocks.SANDSTONE_STAIRS).add(Blocks.STAINED_HARDENED_CLAY).add(Blocks.STONE_BRICK_STAIRS).add(Blocks.STONE_STAIRS).add(Blocks.STONEBRICK)
    //        .add(Blocks.STONE_SLAB).add(Blocks.STONE_SLAB2).add(Blocks.DOUBLE_STONE_SLAB).add(Blocks.DOUBLE_STONE_SLAB2);
    //    private static final Things DIRTS = new Things().add(Blocks.DIRT).add(Blocks.GRAVEL).add(Blocks.GRASS).add(Blocks.SOUL_SAND).add(Blocks.MYCELIUM)
    //        .add(Blocks.GRASS_PATH).add(Blocks.FARMLAND).add(Blocks.CLAY).add(Blocks.SAND);

}
