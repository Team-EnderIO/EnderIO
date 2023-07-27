package com.enderio.base.data.loot;

import com.enderio.EnderIO;
import com.enderio.base.common.enchantment.AutoSmeltModifier;
import com.enderio.base.common.init.EIOEnchantments;
import com.enderio.base.common.item.darksteel.upgrades.direct.DirectUpgradeLootCondition;
import com.enderio.base.common.item.darksteel.upgrades.direct.DirectUpgradeLootModifier;
import com.enderio.base.common.loot.BrokenSpawnerLootModifier;
import com.enderio.base.common.loot.ChestLootModifier;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

import java.util.stream.Stream;

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

        // TODO: Rarer loot, like nether and ancient city.
        //       Can wait until a further balance pass, maybe once we have tools and armor in.
        modifyChestLoot("common_loot", Stream.of(
            "chests/abandoned_mineshaft",
//            "chests/ancient_city",
//            "chests/ancient_city_ice_box",
//            "chests/bastion_bridge",
//            "chests/bastion_hoglin_stable",
//            "chests/bastion_other",
//            "chests/bastion_treasure",
            "chests/buried_treasure",
            "chests/desert_pyramid",
            "chests/end_city_treasure",
            "chests/igloo_chest",
            "chests/jungle_temple",
            "chests/jungle_temple_dispenser",
//            "chests/nether_bridge",
            "chests/pillager_outpost",
            "chests/ruined_portal",
            "chests/shipwreck_map",
            "chests/shipwreck_supply",
            "chests/shipwreck_treasure",
            "chests/simple_dungeon",
            "chests/stronghold_corridor",
            "chests/stronghold_crossing",
            "chests/stronghold_library",
            "chests/underwater_ruin_big",
            "chests/underwater_ruin_small",
            "chests/woodland_mansion"
        ));

        modifyChestLoot("alloy_loot", Stream.of(
            "chests/village/village_armorer",
            "chests/village/village_toolsmith",
            "chests/village/village_weaponsmith"
        ));

        add("direct_upgrade", new DirectUpgradeLootModifier(
            new LootItemCondition[]{
                new DirectUpgradeLootCondition()
            }
        ));
    }

    private void modifyChestLoot(String modifierName, Stream<String> targets) {
        var mappedTargetConditions = targets.map(r -> LootTableIdCondition.builder(new ResourceLocation(r))).toArray(LootTableIdCondition.Builder[]::new);
        add(modifierName, new ChestLootModifier(
            new LootItemCondition[]{
                AnyOfCondition.anyOf(mappedTargetConditions).build()
            },
            EnderIO.loc("chests/" + modifierName)
        ));
    }
}
