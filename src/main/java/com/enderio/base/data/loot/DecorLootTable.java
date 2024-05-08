package com.enderio.base.data.loot;

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
                LootItem.lootTableItem(block)
                    //.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                    //    .include(EIODataComponents.BLOCK_PAINT.get()))
            )));
    }

    public static <T extends Block> void paintedSlab(RegiliteBlockLootProvider loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(LootItem
                .lootTableItem(block)
                //.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(EIONBTKeys.PAINT, EIONBTKeys.BLOCK_ENTITY_TAG + "." + EIONBTKeys.PAINT))
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(block).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.TOP))))))
            .withPool(new LootPool.Builder().add(LootItem
                .lootTableItem(block)
                //.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(EIONBTKeys.PAINT_2, EIONBTKeys.BLOCK_ENTITY_TAG + "." + EIONBTKeys.PAINT_2))
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(block).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.BOTTOM)))))));
    }
}
