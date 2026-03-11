package com.enderio.modded_conduits.datagen;

import com.enderio.modded_conduits.common.ModdedConduits;
import com.enderio.modded_conduits.datagen.client.ModdedConduitsItemModelProvider;
import com.enderio.modded_conduits.datagen.client.ModdedConduitsLanguageProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ModdedConduits.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModdedConduitsDataGen {

    // TODO: Modular datagen so missing mods don't matter too much.
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        var lookupProvider = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();
        
        // Add language provider
        event.getGenerator().addProvider(
            event.includeClient(),
            new ModdedConduitsLanguageProvider(packOutput)
        );

        event.getGenerator().addProvider(event.includeClient(), new ModdedConduitsItemModelProvider(packOutput, existingFileHelper));
    }
}
