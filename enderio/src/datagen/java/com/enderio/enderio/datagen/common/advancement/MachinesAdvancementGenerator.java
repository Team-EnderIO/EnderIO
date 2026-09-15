package com.enderio.enderio.datagen.common.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.ItemUsedOnLocationTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;

import java.util.Optional;

public class MachinesAdvancementGenerator extends AdvancementSubProvider {

    private final HolderGetter<Block> blocks;

    public MachinesAdvancementGenerator(BootstrapContext<Advancement> output) {
        super(output);
        this.blocks = output.lookup(Registries.BLOCK);
    }

    @Override
    public void generate() {
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

    public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedBlock(HolderGetter<Block> blocks, Block... block) {
        var contextawarepredicate = placedBlockCondition(blocks, block);
        return new Criterion<>(
            CriteriaTriggers.PLACED_BLOCK,
            new ItemUsedOnLocationTrigger.TriggerInstance(
                Optional.empty(),
                Optional.of(Holder.direct(contextawarepredicate))));
    }

    public static LootItemCondition placedBlockCondition(HolderGetter<Block> blocks, Block... blocksToMatch) {
        if (blocksToMatch.length == 0) {
            throw new IllegalArgumentException("No valid blocks");
        }

        if (blocksToMatch.length == 1) {
            return MatchBlock.blockMatches(blocks, blocksToMatch[0]).build();
        }

        LootItemCondition.Builder mainBuilder = null;
        for (Block block: blocksToMatch) {
            var builder = MatchBlock.blockMatches(blocks, block);
            if (mainBuilder == null) {
                mainBuilder = builder;
            } else {
                mainBuilder = mainBuilder.or(builder);
            }
        }
        return mainBuilder.build();
    }
}
