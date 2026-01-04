package com.enderio.modded_conduits.datagen.client;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import com.enderio.modded_conduits.common.modules.appeng.AE2ConduitsModule;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import com.enderio.modded_conduits.common.modules.refinedstorage.RefinedStorageCommonModule;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModdedConduitsLanguageProvider extends LanguageProvider {
    
    public ModdedConduitsLanguageProvider(PackOutput output) {
        super(output, EnderIO.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addMekanismTranslations();
        addAE2Translations();
        addRefinedStorageTranslations();
    }
    
    private void addMekanismTranslations() {
        // Items
        add(MekanismModule.BASIC_CHEMICAL_FILTER.get(), "Basic Chemical Filter");
        
        // Conduits
        add(MekanismModule.HEAT, "Heat Conduit");
        add(MekanismModule.CHEMICAL, "Chemical Conduit");
        add(MekanismModule.PRESSURIZED_CHEMICAL, "Pressurized Chemical Conduit");
        add(MekanismModule.ENDER_CHEMICAL, "Ender Chemical Conduit");

        // GUI
        add(MekanismModule.LANG_MULTI_CHEMICAL_TOOLTIP, "Allows multiple chemical types to be transported on the same line");
        add(MekanismModule.CHEMICAL_CONDUIT_CHANGE_FLUID1, "Locked Chemical:");
        add(MekanismModule.CHEMICAL_CONDUIT_CHANGE_FLUID2, "Click to reset!");
        add(MekanismModule.CHEMICAL_CONDUIT_CHANGE_FLUID3, "Chemical: %s");
    }
    
    private void addAE2Translations() {
        // Conduits
        add(AE2ConduitsModule.NORMAL, "ME Conduit");
        add(AE2ConduitsModule.DENSE, "Dense ME Conduit");
    }
    
    private void addRefinedStorageTranslations() {
        // Conduits
        add(RefinedStorageCommonModule.ConduitKeys.RS, "Refined Storage Conduit");
    }

    private void add(ResourceKey<Conduit<?, ?>> key, String translation) {
        add(ConduitApi.INSTANCE.makeDescriptionId(key), translation);
    }

    private void add(Component component, String translation) {
        if (component.getContents() instanceof TranslatableContents translatableContents) {
            add(translatableContents.getKey(), translation);
        } else {
            throw new IllegalArgumentException("Component " + component + " is not translatable");
        }
    }
}
