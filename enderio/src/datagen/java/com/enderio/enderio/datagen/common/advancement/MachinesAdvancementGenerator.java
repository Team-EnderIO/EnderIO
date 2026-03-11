package com.enderio.enderio.datagen.common.advancement;

import com.enderio.enderio.content.advancements.AdvancementsLang;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankBlock;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorTier;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.AdvancementProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;

public class MachinesAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {

        Advancement.Builder builder = Advancement.Builder
            .advancement()
            .parent(ResourceLocation.withDefaultNamespace("adventure/root"))
            .display(EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC), AdvancementsLang.PLACE_CAPACITOR_BANK_ADVANCEMENT_TITLE, AdvancementsLang.PLACE_CAPACITOR_BANK_ADVANCEMENT_DESCRIPTION, null, AdvancementType.TASK, true,
                true, false)
            .addCriterion("place_capacitor_bank", placedBlock(EIOBlocks.CAPACITOR_BANKS.values().stream().map(DeferredHolder::get)
                .sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey)).toArray(CapacitorBankBlock[]::new)));

        builder.save(saver, CapacitorBankBlock.PLACE_ADVANCEMENT_ID.toString());
    }

    public static Criterion placedBlock(Block... block) {
        var contextawarepredicate = ContextAwarePredicate.create(placedBlockCondition(block));
        return new Criterion(
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
