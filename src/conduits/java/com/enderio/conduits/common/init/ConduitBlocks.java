package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.enderio.conduits.common.conduit.block.ConduitBlock;
import com.enderio.conduits.data.model.ConduitBlockState;
import com.enderio.regilite.holder.RegiliteBlock;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;

public class ConduitBlocks {
    private static final ItemRegistry ITEM_REGISTRY = EnderIO.getRegilite().itemRegistry();
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
        .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE)
        .createBlockItem(ITEM_REGISTRY, b -> new ConduitBlockItem(b, new Item.Properties()),
            item -> item.setModelProvider((prov, ctx) -> {}));

    public static void register(IEventBus bus) {
        BLOCK_REGISTRY.register(bus);
        ITEM_REGISTRY.register(bus);
    }
}
