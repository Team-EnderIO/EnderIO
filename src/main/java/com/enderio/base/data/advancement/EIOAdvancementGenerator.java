package com.enderio.base.data.advancement;

import com.enderio.base.common.advancement.UseGliderAdvancementBenefit;
import com.enderio.base.common.advancement.UseGliderTrigger;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.HashMap;
import java.util.function.Consumer;

public class EIOAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        Advancement.Builder builder = Advancement.Builder
            .advancement()
            .parent(new Advancement(new ResourceLocation("adventure/root"), null, null, null, new HashMap<>(), null))
            .display(EIOItems.GLIDER.get(), EIOLang.USE_GLIDER_ADVANCEMENT_TITLE, EIOLang.USE_GLIDER_ADVANCEMENT_DESCRIPTION, null, FrameType.TASK, true,
                true, false)
            .addCriterion("use_glider", new UseGliderTrigger.TriggerInstance());

        builder.save(saver, UseGliderAdvancementBenefit.USE_GLIDER_ADVANCEMENT.toString());
    }
}
