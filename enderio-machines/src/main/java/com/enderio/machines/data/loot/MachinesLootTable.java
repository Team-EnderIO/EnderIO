package com.enderio.machines.data.loot;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;

public class MachinesLootTable {

    public static <T extends Block> void copyNBT(RegistrateBlockLootTables loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("", "BlockEntityTag")))));
    }

    // Ignores the top block.
    public static <T extends Block> void tallCopyNBT(RegistrateBlockLootTables loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(LootItem
                .lootTableItem(block)
                .when(LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(block)
                    .setProperties(StatePropertiesPredicate.Builder
                        .properties()
                        .hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)))
                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("", "BlockEntityTag")))));
    }
}
