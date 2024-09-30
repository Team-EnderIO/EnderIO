package com.enderio.conduits.common.init;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.enderio.conduits.common.conduit.block.ConduitBundleBlock;
import com.enderio.conduits.data.model.ConduitBlockState;
import com.enderio.regilite.blocks.RegiliteBlocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ConduitBlocks {
    private static final RegiliteBlocks BLOCKS = EnderIOConduits.REGILITE.blocks();

    public static final DeferredBlock<ConduitBundleBlock> CONDUIT = BLOCKS
        .create("conduit", ConduitBundleBlock::new,
            BlockBehaviour.Properties.of()
                .strength(1.5f, 10)
                .noLootTable()
                .noOcclusion()
                .dynamicShape()
                .mapColor(MapColor.STONE))
        .translation("Conduit Bundle")
        .blockState(ConduitBlockState::conduit)
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .createBlockItem(b -> new ConduitBlockItem(b, new Item.Properties()),
            item -> item
                .translation("<MISSING> Conduit")
                .noModel())
        .finish();

    public static void register() {
    }
}
