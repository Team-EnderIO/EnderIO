package com.enderio.machines.data.loot;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.regilite.data.RegiliteBlockLootProvider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;

public class MachinesLootTable {

    public static <T extends Block> void copyComponents(RegiliteBlockLootProvider loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)))));
    }

    public static <T extends Block> void copyStandardComponentsWith(RegiliteBlockLootProvider loot, T block, DataComponentType<?> componentType) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                    .include(componentType)
                    // TODO: 20.6 - Need to add support for read/writing these in the BE's.
                    .include(EIODataComponents.ENERGY.get())
                    //.include(MachineNBTKeys.IO_CONFIG, BlockItem.BLOCK_ENTITY_TAG + "." + MachineNBTKeys.IO_CONFIG)
                    //.include(MachineNBTKeys.REDSTONE_CONTROL, BlockItem.BLOCK_ENTITY_TAG + "." + MachineNBTKeys.REDSTONE_CONTROL)
                    //.include(MachineNBTKeys.ITEMS, BlockItem.BLOCK_ENTITY_TAG + "." + MachineNBTKeys.ITEMS)
                ))));
    }
}
