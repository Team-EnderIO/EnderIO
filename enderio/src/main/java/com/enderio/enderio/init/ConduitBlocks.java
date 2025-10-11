package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConduitBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnderIO.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnderIO.MOD_ID);

    public static final DeferredBlock<ConduitBundleBlock> CONDUIT_BUNDLE = BLOCKS.registerBlock("conduit",
        ConduitBundleBlock::new, BlockBehaviour.Properties.of()
            .strength(1.5f, 10)
            .noLootTable()
            .noOcclusion()
            .dynamicShape()
            .mapColor(MapColor.STONE));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
