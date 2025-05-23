package com.enderio.base.data.loot;

import com.enderio.base.common.block.plants.ExpPlant;
import com.enderio.regilite.data.RegiliteBlockLootProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;

public class EIOLootTableProvider {

    public static <T extends ExpPlant> void expPlant(RegiliteBlockLootProvider loot, T block, Item other) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem
                    .lootTableItem(block)
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ExpPlant.HALF, DoubleBlockHalf.LOWER))
                    )
                    .otherwise(LootItem.lootTableItem(other))
            )));
    }
}
