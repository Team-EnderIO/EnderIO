package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.common.blocks.ConduitBlock;
import com.enderio.conduits.data.model.ConduitBlockState;
import com.enderio.regilite.holder.RegiliteBlock;
import com.enderio.regilite.registry.BlockRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;

public class ConduitBlocks {
    private static final BlockRegistry BLOCK_REGISTRY = EnderIO.getRegilite().blockRegistry();

    public static final RegiliteBlock<ConduitBlock> CONDUIT = BLOCK_REGISTRY
        .registerBlock("conduit", ConduitBlock::new,
            BlockBehaviour.Properties.of()
                .strength(1.5f, 10)
                .noLootTable()
                .noOcclusion()
                .dynamicShape()
                .mapColor(MapColor.STONE))
        .setBlockStateProvider(ConduitBlockState::conduit)
        .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE);

    public static void register(IEventBus bus) {
        BLOCK_REGISTRY.register(bus);
    }
}
