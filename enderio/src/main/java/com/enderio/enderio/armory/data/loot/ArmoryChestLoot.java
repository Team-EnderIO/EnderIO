package com.enderio.enderio.armory.data.loot;

import com.enderio.enderio.armory.EnderIOArmory;
import com.enderio.enderio.armory.common.init.ArmoryItems;
import com.enderio.enderio.common.event.EIOChestLootEvent;
import com.enderio.enderio.data.loot.ChestLootProvider;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = EnderIOArmory.MODULE_MOD_ID)
public class ArmoryChestLoot {
    @SubscribeEvent
    public static void OnChestLootEvent(EIOChestLootEvent event) {
        if (event.getLootTableName().equals(ChestLootProvider.COMMON_LOOT_TABLE_NAME)) {
            event.add(LootItem.lootTableItem(ArmoryItems.DARK_STEEL_SWORD)
                .when(LootItemRandomChanceCondition.randomChance(0.1f))
                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(1.0f, 2000.0f))));
            // TODO: boots
        }
    }
}
