package com.enderio.machines.data.loot;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.regilite.data.RegiliteBlockLootProvider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
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
                    .include(EIODataComponents.ENERGY.get())
                    .include(MachineDataComponents.IO_CONFIG.get())
                    .include(MachineDataComponents.REDSTONE_CONTROL.get())
                    .include(DataComponents.CONTAINER)
                ))));
    }
}
