package com.enderio.machines.data.loot;

import com.enderio.machines.common.MachineNBTKeys;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;

public class MachinesLootTable {

    public static <T extends Block> void copyNBT(RegistrateBlockLootTables loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("", BlockItem.BLOCK_ENTITY_TAG)))));
    }

    public static <T extends Block> void copyNBTSingleCap(RegistrateBlockLootTables loot, T block, String name) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                    .copy(name, BlockItem.BLOCK_ENTITY_TAG + "." + name)
                    .copy(MachineNBTKeys.ENERGY, BlockItem.BLOCK_ENTITY_TAG + "." + MachineNBTKeys.ENERGY)
                    .copy(MachineNBTKeys.IO_CONFIG, BlockItem.BLOCK_ENTITY_TAG + "." + MachineNBTKeys.IO_CONFIG)
                    .copy(MachineNBTKeys.REDSTONE_CONTROL, BlockItem.BLOCK_ENTITY_TAG + "." + MachineNBTKeys.REDSTONE_CONTROL)
                    .copy(MachineNBTKeys.ITEMS, BlockItem.BLOCK_ENTITY_TAG + "." + MachineNBTKeys.ITEMS)
                ))));
    }
}
