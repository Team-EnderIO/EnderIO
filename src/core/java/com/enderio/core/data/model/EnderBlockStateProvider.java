package com.enderio.core.data.model;

import com.enderio.core.common.registry.EnderBlockRegistry;
import com.enderio.core.common.registry.EnderDeferredBlock;
import com.enderio.core.data.loot.EnderBlockLootProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.BiConsumer;

public class EnderBlockStateProvider extends BlockStateProvider {
    private final EnderBlockRegistry registry;

    public EnderBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper, EnderBlockRegistry registry) {
        super(output, modid, exFileHelper);
        this.registry = registry;
    }

    @Override
    protected void registerStatesAndModels() {
        for (DeferredHolder<Block, ? extends Block> block : registry.getEntries()) {
            BiConsumer<BlockStateProvider, Block> blockstate = ((EnderDeferredBlock<Block>) block).getBlockStateProvider();
            if (blockstate != null) {
                blockstate.accept(this, block.get());
            }
        }
    }
}
