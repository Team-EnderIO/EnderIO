package com.enderio.base.data.advancement;

import com.enderio.EnderIO;
import com.enderio.base.common.advancement.PaintingTrigger;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.HashMap;
import java.util.function.Consumer;

public class EIOAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
//        Advancement.Builder builder = Advancement.Builder
//            .advancement()
//            .parent(new Advancement(new ResourceLocation("adventure/root"), null, null, null, new HashMap<>(), null, false))
//            .display(EIOItems.GLIDER.get(), EIOLang.USE_GLIDER_ADVANCEMENT_TITLE, EIOLang.USE_GLIDER_ADVANCEMENT_DESCRIPTION, null, FrameType.TASK, true,
//                true, false)
//            .addCriterion("use_glider", new UseGliderTrigger.TriggerInstance());
//        builder.save(saver, UseGliderAdvancementBenefit.USE_GLIDER_ADVANCEMENT.toString());
        Advancement rich = Advancement.Builder.advancement()
            .parent(new Advancement(new ResourceLocation("adventure/root"), null, null, null, new HashMap<>(), null, false))
            .display(Items.DIAMOND_BLOCK, EIOLang.RICH_ADVANCEMENT_TITLE, EIOLang.RICH_ADVANCEMENT_DESCRIPTION, null, FrameType.TASK, true,
                true, false)
            .addCriterion("paint", new PaintingTrigger.TriggerInstance(Blocks.DIAMOND_BLOCK)).save(saver, EnderIO.loc("adventure/rich").toString());
        Advancement.Builder.advancement()
            .parent(rich)
            .display(Items.NETHERITE_BLOCK, EIOLang.RICHER_ADVANCEMENT_TITLE, EIOLang.RICHER_ADVANCEMENT_DESCRIPTION, null, FrameType.TASK, true,
                true, false)
            .addCriterion("paint", new PaintingTrigger.TriggerInstance(Blocks.NETHERITE_BLOCK)).save(saver, EnderIO.loc("adventure/richer").toString());

    }
}
