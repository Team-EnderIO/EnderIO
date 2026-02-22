package com.enderio.endergy.common.init;

import com.enderio.enderio.EnderIO;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class EndergyBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnderIO.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnderIO.MOD_ID);

    // region Alloy Blocks

    public static final DeferredBlock<Block> CRUDE_STEEL_BLOCK = registerMetalBlock("crude_steel_block");
    public static final DeferredBlock<Block> CRYSTALLINE_ALLOY_BLOCK = registerMetalBlock("crystalline_alloy_block");
    public static final DeferredBlock<Block> MELODIC_ALLOY_BLOCK = registerMetalBlock("melodic_alloy_block");
    public static final DeferredBlock<Block> STELLAR_ALLOY_BLOCK = registerMetalBlock("stellar_alloy_block");
    public static final DeferredBlock<Block> VIVID_ALLOY_BLOCK = registerMetalBlock("vivid_alloy_block");

    private static DeferredBlock<Block> registerMetalBlock(String name) {
        return registerWithItem(name, Block::new,
            BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.METAL).strength(5, 6).requiresCorrectToolForDrops());
    }

    // endregion

    private static <B extends Block> DeferredBlock<B> registerWithItem(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        var blockHolder = BLOCKS.<B>registerBlock(name, func, props);
        ITEMS.registerSimpleBlockItem(blockHolder);
        return blockHolder;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props, Function<Supplier<B>, Item> itemFactory) {
        var blockHolder = BLOCKS.<B>registerBlock(name, func, props);
        ITEMS.register(name, () -> itemFactory.apply(blockHolder));
        return blockHolder;
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
