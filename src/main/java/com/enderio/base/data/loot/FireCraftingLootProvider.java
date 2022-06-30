package com.enderio.base.data.loot;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FireCraftingLootProvider extends LootTableProvider {
    protected final DataGenerator generator;

    public FireCraftingLootProvider(DataGenerator generator) {
        super(generator);
        this.generator = generator;
    }

    @Override
    public void run(CachedOutput cachedOutput) {
        Map<ResourceLocation, LootTable> tables = new HashMap<>();

        LootTable infinity = LootTable
            .lootTable()
            .withPool(LootPool.lootPool().name("infinity_in_world_crafting").setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(EIOItems.GRAINS_OF_INFINITY.get()).when(LootItemRandomChanceCondition.randomChance(0.5f))))
            .setParamSet(LootContextParamSet.builder().build())
            .build();

        tables.put(EnderIO.loc("fire_crafting/infinity"), infinity);

        writeTables(cachedOutput, tables);
    }

    private void writeTables(CachedOutput cachedOutput, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                DataProvider.saveStable(cachedOutput, LootTables.serialize(lootTable), path);
            } catch (IOException e) {
                EnderIO.LOGGER.error("Couldn't write loot table {}", path, (Object) e);
            }
        });
    }
}
