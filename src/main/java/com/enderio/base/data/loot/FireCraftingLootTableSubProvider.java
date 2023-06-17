package com.enderio.base.data.loot;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class FireCraftingLootTableSubProvider implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> writer) {
        writer.accept(EnderIO.loc("fire_crafting/infinity"), LootTable
            .lootTable()
            .withPool(LootPool
                .lootPool()
                .name("infinity_in_world_crafting")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(EIOItems.GRAINS_OF_INFINITY.get()).when(LootItemRandomChanceCondition.randomChance(0.5f))))
            .setParamSet(LootContextParamSet.builder().build()));
    }
}
