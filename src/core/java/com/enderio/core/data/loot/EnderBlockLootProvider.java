package com.enderio.core.data.loot;

import com.enderio.core.common.registry.EnderBlockRegistry;
import com.enderio.core.common.registry.EnderDeferredBlock;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.function.BiConsumer;

public class EnderBlockLootProvider extends BlockLootSubProvider {

    private final EnderBlockRegistry registry;

    public EnderBlockLootProvider(Set<Item> explosionResistant, EnderBlockRegistry registry) {
        super(explosionResistant, FeatureFlags.REGISTRY.allFlags());
        this.registry = registry;
    }

    @Override
    protected void generate() {
        for (DeferredHolder<Block, ? extends Block> block : registry.getEntries()) {
            BiConsumer<EnderBlockLootProvider, Block> lootTable = ((EnderDeferredBlock<Block>) block).getLootTable();
            if (lootTable != null) {
                lootTable.accept(this, block.get());
            }
        }
    }

    @Override
    public void dropSelf(Block block) {
        super.dropSelf(block);
    }

    public void createDoor(Block block) {
        this.add(block, super::createDoorTable);
    }
}
