package com.enderio.enderio.data.loot;

import com.enderio.enderio.content.paint.CopyPaintFunction;
import com.enderio.regilite.data.RegiliteBlockLootProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;

public class DecorLootTable {

    public static <T extends Block> void withPaint(RegiliteBlockLootProvider loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyPaintFunction.copyPrimary())
            )));
    }
}
