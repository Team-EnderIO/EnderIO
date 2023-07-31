package com.enderio.base.data.loot;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.loot.SetLootCapacitorFunction;
import com.mojang.datafixers.kinds.Const;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ChestLootProvider implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> writer) {
        writer.accept(EnderIO.loc("chests/common_loot"), LootTable
            .lootTable()
            .withPool(LootPool
                .lootPool()
                .name("Ender IO")
                .setRolls(UniformGenerator.between(1.0f, 3.0f))
                .add(LootItem.lootTableItem(EIOItems.DARK_STEEL_INGOT.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.25f))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f)))
                )
                .add(LootItem.lootTableItem(Items.ENDER_PEARL)
                    .when(LootItemRandomChanceCondition.randomChance(0.3f))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f)))
                )
                .add(LootItem.lootTableItem(EIOItems.DARK_STEEL_SWORD.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(1.0f, 2000.0f)))
                    // TODO: When upgrades are done, set energy and upgrade
                )
                // TODO: boots
                .add(LootItem.lootTableItem(EIOItems.GEAR_WOOD.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.5f))
                )
                .add(LootItem.lootTableItem(EIOItems.LOOT_CAPACITOR.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.15f))
                    .apply(SetLootCapacitorFunction.setLootCapacitor(UniformGenerator.between(1.0f, 4.0f)))
                )
                // TODO: Add these additionals to rarer pools
//                .add(LootItem.lootTableItem(EIOItems.LOOT_CAPACITOR.get())
//                    .when(LootItemRandomChanceCondition.randomChance(0.15f))
//                    .apply(SetLootCapacitorFunction.setLootCapacitor(UniformGenerator.between(1.0f, 4.0f)))
//                )
            )
            .setParamSet(LootContextParamSet.builder().build()));

        // Only includes stuff not present in common
        writer.accept(EnderIO.loc("chests/alloy_loot"), LootTable
            .lootTable()
            .withPool(LootPool
                .lootPool()
                .name("Ender IO")
                .setRolls(UniformGenerator.between(0.0f, 2.0f))
                .add(LootItem.lootTableItem(EIOItems.COPPER_ALLOY_INGOT.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.2f))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f)))
                )
                .add(LootItem.lootTableItem(EIOItems.REDSTONE_ALLOY_INGOT.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.35f))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f)))
                )
                .add(LootItem.lootTableItem(EIOItems.PULSATING_ALLOY_INGOT.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.3f))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f)))
                )
                .add(LootItem.lootTableItem(EIOItems.VIBRANT_ALLOY_INGOT.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.2f))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f)))
                )
                .add(LootItem.lootTableItem(EIOItems.GEAR_STONE.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.4f))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f)))
                )
                .add(LootItem.lootTableItem(EIOItems.GEAR_IRON.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.25f))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f)))
                )
                .add(LootItem.lootTableItem(EIOItems.GEAR_ENERGIZED.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.125f))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f)))
                )
                .add(LootItem.lootTableItem(EIOItems.GEAR_VIBRANT.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.0625f))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f)))
                )
                // TODO: Random upgrade
            )
            .setParamSet(LootContextParamSet.builder().build()));
    }
}
