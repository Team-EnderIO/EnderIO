package com.enderio.base.data.advancement;

import com.enderio.EnderIOBase;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.paint.PaintingTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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
            .display(Items.DIAMOND_BLOCK, EIOLang.RICH_ADVANCEMENT_TITLE, EIOLang.RICH_ADVANCEMENT_DESCRIPTION, null, AdvancementType.TASK, true,
                true, false)
            .addCriterion("paint", PaintingTrigger.TriggerInstance.painted(Blocks.DIAMOND_BLOCK))
            .save(consumer, EnderIOBase.loc("adventure/rich").toString());

        Advancement.Builder.advancement()
            .parent(rich)
            .display(Items.NETHERITE_BLOCK, EIOLang.RICHER_ADVANCEMENT_TITLE, EIOLang.RICHER_ADVANCEMENT_DESCRIPTION, null, AdvancementType.TASK, true,
                true, false)
            .addCriterion("paint", PaintingTrigger.TriggerInstance.painted(Blocks.NETHERITE_BLOCK))
            .save(consumer, EnderIOBase.loc("adventure/richer").toString());
    }
}
