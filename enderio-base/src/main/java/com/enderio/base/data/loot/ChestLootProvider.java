package com.enderio.base.data.loot;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.event.EIOChestLootEvent;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.loot.SetLootCapacitorFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.fml.ModLoader;

import java.util.function.BiConsumer;

public class ChestLootProvider implements LootTableSubProvider {

    public static final String COMMON_LOOT_TABLE_NAME = "chests/common_loot";
    public static final String ALLOY_LOOT_TABLE_NAME = "chests/alloy_loot";

    private final HolderLookup.Provider registries;

    public ChestLootProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> writer) {
        generateCommonLoot(writer);
        generateAlloyLoot(writer);
    }

    private void generateCommonLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> writer) {
        var lootPool = LootPool
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
            .add(LootItem.lootTableItem(EIOItems.LOOT_CAPACITOR.get())
                .when(LootItemRandomChanceCondition.randomChance(0.15f))
                .apply(SetLootCapacitorFunction.setLootCapacitor(UniformGenerator.between(1.0f, 4.0f)))
            )
// TODO: Add these additionals to rarer pools
//          .add(LootItem.lootTableItem(EIOItems.LOOT_CAPACITOR.get())
//              .when(LootItemRandomChanceCondition.randomChance(0.15f))
//              .apply(SetLootCapacitorFunction.setLootCapacitor(UniformGenerator.between(1.0f, 4.0f)))
//          )
        ;

        EIOChestLootEvent event = new EIOChestLootEvent(COMMON_LOOT_TABLE_NAME, lootPool);
        ModLoader.postEvent(event);

        var lootTable = LootTable
            .lootTable()
            .withPool(lootPool)
            .setParamSet(LootContextParamSet.builder().build());

        writer.accept(ResourceKey.create(Registries.LOOT_TABLE, EnderIO.loc(COMMON_LOOT_TABLE_NAME)), lootTable);
    }

    private void generateAlloyLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> writer) {
        var lootPool = LootPool
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
            );

        EIOChestLootEvent event = new EIOChestLootEvent(ALLOY_LOOT_TABLE_NAME, lootPool);
        ModLoader.postEvent(event);

        var lootTable = LootTable
            .lootTable()
            .withPool(lootPool)
            .setParamSet(LootContextParamSet.builder().build());

        writer.accept(ResourceKey.create(Registries.LOOT_TABLE, EnderIO.loc(ALLOY_LOOT_TABLE_NAME)), lootTable);
    }
}
