package com.enderio.enderio.datagen.common.advancement;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.advancements.AdvancementsLang;
import com.enderio.enderio.content.paint.PaintingTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.AdvancementProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class EIOAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
//        Advancement.Builder builder = Advancement.Builder
//            .advancement()
//            .parent(new ResourceLocation("adventure/root"))
//            .display(EIOItems.GLIDER.get(), EIOLang.USE_GLIDER_ADVANCEMENT_TITLE, EIOLang.USE_GLIDER_ADVANCEMENT_DESCRIPTION, null, FrameType.TASK, true,
//                true, false)
//            .addCriterion("use_glider", new UseGliderTrigger.TriggerInstance());
//        builder.save(saver, UseGliderAdvancementBenefit.USE_GLIDER_ADVANCEMENT.toString());

        AdvancementHolder rich = Advancement.Builder.advancement()
            .parent(ResourceLocation.withDefaultNamespace("adventure/root"))
            .display(Items.DIAMOND_BLOCK, AdvancementsLang.RICH_ADVANCEMENT_TITLE, AdvancementsLang.RICH_ADVANCEMENT_DESCRIPTION, null, AdvancementType.TASK, true,
                true, false)
            .addCriterion("paint", PaintingTrigger.TriggerInstance.painted(Blocks.DIAMOND_BLOCK))
            .save(consumer, EnderIO.rl("adventure/rich").toString());

        Advancement.Builder.advancement()
            .parent(rich)
            .display(Items.NETHERITE_BLOCK, AdvancementsLang.RICHER_ADVANCEMENT_TITLE, AdvancementsLang.RICHER_ADVANCEMENT_DESCRIPTION, null, AdvancementType.TASK, true,
                true, false)
            .addCriterion("paint", PaintingTrigger.TriggerInstance.painted(Blocks.NETHERITE_BLOCK))
            .save(consumer, EnderIO.rl("adventure/richer").toString());
    }
}
