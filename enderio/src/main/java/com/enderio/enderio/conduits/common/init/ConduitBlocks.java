package com.enderio.enderio.conduits.common.init;

import com.enderio.enderio.common.tag.EIOTags;
import com.enderio.enderio.conduits.EnderIOConduits;
import com.enderio.enderio.client.color.ConduitFacadeColor;
import com.enderio.enderio.common.conduits.ConduitBlockItem;
import com.enderio.enderio.common.conduits.bundle.ConduitBundleBlock;
import com.enderio.enderio.conduits.data.model.ConduitBlockState;
import com.enderio.regilite.holder.RegiliteBlock;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;

public class ConduitBlocks {
    private static final ItemRegistry ITEM_REGISTRY = EnderIOConduits.REGILITE.itemRegistry();
    private static final BlockRegistry BLOCK_REGISTRY = EnderIOConduits.REGILITE.blockRegistry();

    public static final RegiliteBlock<ConduitBundleBlock> CONDUIT = BLOCK_REGISTRY
            .registerBlock("conduit", ConduitBundleBlock::new,
                    BlockBehaviour.Properties.of()
                            .strength(1.5f, 10)
                            .noLootTable()
                            .noOcclusion()
                            .dynamicShape()
                            .mapColor(MapColor.STONE))
            .setColorSupplier(() -> ConduitFacadeColor::new)
            .setTranslation("Conduit Bundle")
            .setBlockStateProvider(ConduitBlockState::conduit)
            .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .createBlockItem(ITEM_REGISTRY, b -> new ConduitBlockItem(b, new Item.Properties()),
                    item -> item.setTranslation("<MISSING> Conduit").setModelProvider((prov, ctx) -> {
                    }).addItemTags(EIOTags.Items.HIDE_FACADES));

    public static void register(IEventBus bus) {
        BLOCK_REGISTRY.register(bus);
        ITEM_REGISTRY.register(bus);
    }
}
