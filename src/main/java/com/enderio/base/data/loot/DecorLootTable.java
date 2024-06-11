package com.enderio.base.data.loot;

import com.enderio.base.common.paint.CopyPaintFunction;
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

    public static <T extends Block> void paintedSlab(RegiliteBlockLootProvider loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem
                    .lootTableItem(block)
                    .apply(CopyPaintFunction.copyPrimary())
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(block).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.TOP))))))
            .withPool(new LootPool.Builder().add(
                LootItem
                    .lootTableItem(block)
                    .apply(CopyPaintFunction.copySecondary())
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(block).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.BOTTOM)))))));
    }
}
