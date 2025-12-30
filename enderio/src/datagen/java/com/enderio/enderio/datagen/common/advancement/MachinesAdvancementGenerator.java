package com.enderio.enderio.datagen.common.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.ItemUsedOnLocationTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;
import java.util.function.Consumer;

public class MachinesAdvancementGenerator implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer) {

//        Advancement.Builder builder = Advancement.Builder
//            .advancement()
//            .parent(Identifier.withDefaultNamespace("adventure/root"))
//            .display(EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC), AdvancementsLang.PLACE_CAPACITOR_BANK_ADVANCEMENT_TITLE, AdvancementsLang.PLACE_CAPACITOR_BANK_ADVANCEMENT_DESCRIPTION, null, AdvancementType.TASK, true,
//                true, false)
//            .addCriterion("place_capacitor_bank", placedBlock(EIOBlocks.CAPACITOR_BANKS.values().stream().map(DeferredHolder::get)
//                .sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey)).toArray(CapacitorBankBlock[]::new)));
//
//        builder.save(consumer, CapacitorBankBlock.PLACE_ADVANCEMENT_ID.toString());
    }

    public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedBlock(Block... block) {
        var contextawarepredicate = ContextAwarePredicate.create(placedBlockCondition(block));
        return new Criterion<>(
            CriteriaTriggers.PLACED_BLOCK,
            new ItemUsedOnLocationTrigger.TriggerInstance(
                Optional.empty(),
                Optional.of(contextawarepredicate)));
    }

    public static LootItemCondition placedBlockCondition(Block... blocks) {
        if (blocks.length == 0) {
            throw new IllegalArgumentException("No valid blocks");
        }

        if (blocks.length == 1) {
            return LootItemBlockStatePropertyCondition.hasBlockStateProperties(blocks[0]).build();
        }

        LootItemCondition.Builder mainBuilder = null;
        for (Block block: blocks) {
            var builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(block);
            if (mainBuilder == null) {
                mainBuilder = builder;
            } else {
                mainBuilder = mainBuilder.or(builder);
            }
        }
        return mainBuilder.build();
    }
}
