package com.enderio.base.data.loot;

import com.enderio.base.EIONBTKeys;
import com.enderio.core.data.loot.EnderBlockLootProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;

public class DecorLootTable {

    public static void withPaint(EnderBlockLootProvider provider, Block block) {
        provider.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(EIONBTKeys.PAINT, EIONBTKeys.BLOCK_ENTITY_TAG + "." + EIONBTKeys.PAINT)))));
    }

    public static <T extends Block> void paintedSlab(EnderBlockLootProvider provider, T block) {
        provider.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(LootItem
                .lootTableItem(block)
                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(EIONBTKeys.PAINT, EIONBTKeys.BLOCK_ENTITY_TAG + "." + EIONBTKeys.PAINT))
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(block).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.TOP))))))
            .withPool(new LootPool.Builder().add(LootItem
                .lootTableItem(block)
                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(EIONBTKeys.PAINT_2, EIONBTKeys.BLOCK_ENTITY_TAG + "." + EIONBTKeys.PAINT_2))
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(block).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.BOTTOM)))))));
    }
}
