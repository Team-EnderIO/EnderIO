package com.enderio.base.data.advancement;

import com.enderio.EnderIO;
import com.enderio.base.common.advancement.UseGliderAdvancementBenefit;
import com.enderio.base.common.advancement.UseGliderTrigger;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import com.tterrag.registrate.Registrate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.function.Consumer;

public class EIOAdvancementProvider extends AdvancementProvider {
    private final Registrate registrate;

    public EIOAdvancementProvider(Registrate registrate, DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
        this.registrate = registrate;
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        Advancement.Builder builder = Advancement.Builder
            .advancement()
            .parent(new Advancement(new ResourceLocation("adventure/root"), null, null, null, new HashMap<>(), null))
            .display(EIOItems.GLIDER.get(), EIOLang.USE_GLIDER_ADVANCEMENT_TITLE, EIOLang.USE_GLIDER_ADVANCEMENT_DESCRIPTION, null, FrameType.TASK, true,
                true, false)
            .addCriterion("use_glider", new UseGliderTrigger.TriggerInstance());

        builder.save(consumer, UseGliderAdvancementBenefit.USE_GLIDER_ADVANCEMENT.toString());
    }
}
