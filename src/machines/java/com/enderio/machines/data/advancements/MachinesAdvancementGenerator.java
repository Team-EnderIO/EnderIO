package com.enderio.machines.data.advancements;

import com.enderio.machines.common.block.CapacitorBankBlock;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.lang.MachineLang;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;

public class MachinesAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        Advancement.Builder builder = Advancement.Builder
            .advancement()
            .parent(new Advancement(new ResourceLocation("adventure/root"), null, null, null, new HashMap<>(), null, false))
            .display(MachineBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC), MachineLang.PLACE_CAPACITOR_BANK_ADVANCEMENT_TITLE, MachineLang.PLACE_CAPACITOR_BANK_ADVANCEMENT_DESCRIPTION, null, FrameType.TASK, true,
                true, false)
            .addCriterion("place_capacitor_bank", placedBlock(MachineBlocks.CAPACITOR_BANKS.values().stream().map(RegistryEntry::get)
                .sorted(Comparator.comparing(ForgeRegistries.BLOCKS::getKey)).toArray(CapacitorBankBlock[]::new)));

        builder.save(saver, CapacitorBankBlock.PLACE_ADVANCEMENT_ID.toString());
    }
    public static ItemUsedOnLocationTrigger.TriggerInstance placedBlock(Block... block) {
        ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(placedBlockCondition(block));
        return new ItemUsedOnLocationTrigger.TriggerInstance(CriteriaTriggers.PLACED_BLOCK.getId(), ContextAwarePredicate.ANY, contextawarepredicate);
    }

    public static LootItemCondition placedBlockCondition(Block... blocks) {
        if (blocks.length == 0)
            throw new IllegalArgumentException("No valid blocks");
        if (blocks.length == 1)
            return LootItemBlockStatePropertyCondition.hasBlockStateProperties(blocks[0]).build();
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
