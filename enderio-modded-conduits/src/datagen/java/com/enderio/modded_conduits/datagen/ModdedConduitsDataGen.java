package com.enderio.modded_conduits.datagen;

import com.enderio.modded_conduits.common.ModdedConduits;
import com.enderio.modded_conduits.datagen.client.ModdedConduitsLanguageProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ModdedConduits.MOD_ID)
public class ModdedConduitsDataGen {

    // TODO: Modular datagen so missing mods don't matter too much.
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        // Add language provider
        event.getGenerator().addProvider(
            true,
            new ModdedConduitsLanguageProvider(packOutput)
        );

//        event.getGenerator().addProvider(event.includeClient(), new ModdedConduitsItemModelProvider(packOutput, existingFileHelper));
    }
}
