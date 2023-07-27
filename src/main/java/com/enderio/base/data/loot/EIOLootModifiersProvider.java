package com.enderio.base.data.loot;

import com.enderio.EnderIO;
import com.enderio.base.common.enchantment.AutoSmeltModifier;
import com.enderio.base.common.init.EIOEnchantments;
import com.enderio.base.common.item.darksteel.upgrades.direct.DirectUpgradeLootCondition;
import com.enderio.base.common.item.darksteel.upgrades.direct.DirectUpgradeLootModifier;
import com.enderio.base.common.loot.BrokenSpawnerLootModifier;
import com.enderio.base.common.loot.CapacitorLootModifier;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class EIOLootModifiersProvider extends GlobalLootModifierProvider {
    public EIOLootModifiersProvider(PackOutput output) {
        super(output, EnderIO.MODID);
    }

    @Override
    protected void start() {
        add("auto_smelt", new AutoSmeltModifier(
            new LootItemCondition[]{
                MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(
                    new EnchantmentPredicate(EIOEnchantments.AUTO_SMELT.get(), MinMaxBounds.Ints.atLeast(1)))
                ).build()
            }
        ));

        add("broken_spawner", new BrokenSpawnerLootModifier(
            new LootItemCondition[]{
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SPAWNER).build()
            }
        ));

        add("capacitor_loot", new CapacitorLootModifier(
            new LootItemCondition[]{
                LootTableIdCondition.builder(new ResourceLocation("chests/simple_dungeon")).build(),
                LootItemRandomChanceCondition.randomChance(0.25f).build()
            }, 1, 4
        ));

        add("direct_upgrade", new DirectUpgradeLootModifier(
            new LootItemCondition[]{
                new DirectUpgradeLootCondition()
            }
        ));
    }
}
