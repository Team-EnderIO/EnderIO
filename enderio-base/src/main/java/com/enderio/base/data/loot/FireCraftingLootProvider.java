package com.enderio.base.data.loot;

import com.enderio.EnderIOBase;
import com.enderio.base.common.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class FireCraftingLootProvider implements LootTableSubProvider {
    private final HolderLookup.Provider registries;

    public static ResourceKey<LootTable> BEDROCK_CRAFTING = ResourceKey.create(Registries.LOOT_TABLE, EnderIOBase.loc("fire_crafting/bedrock_infinity"));
    public static ResourceKey<LootTable> DEEPSLATE_CRAFTING = ResourceKey.create(Registries.LOOT_TABLE, EnderIOBase.loc("fire_crafting/deepslate_infinity"));

    public FireCraftingLootProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> writer) {
        LootTable.Builder bedrock = LootTable
            .lootTable()
            .withPool(LootPool
                .lootPool()
                .name("infinity_in_world_crafting")
                .setRolls(UniformGenerator.between(1.0f, 3.0f))
                .add(LootItem.lootTableItem(EIOItems.GRAINS_OF_INFINITY.get()).when(LootItemRandomChanceCondition.randomChance(0.8f))))
            .setParamSet(LootContextParamSet.builder().build());

        writer.accept(BEDROCK_CRAFTING, bedrock);

        LootTable.Builder deepslate = LootTable
            .lootTable()
            .withPool(LootPool
                .lootPool()
                .name("infinity_in_world_crafting")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(EIOItems.GRAINS_OF_INFINITY.get()).when(LootItemRandomChanceCondition.randomChance(0.4f))))
            .setParamSet(LootContextParamSet.builder().build());

        writer.accept(DEEPSLATE_CRAFTING, deepslate);
    }
}
