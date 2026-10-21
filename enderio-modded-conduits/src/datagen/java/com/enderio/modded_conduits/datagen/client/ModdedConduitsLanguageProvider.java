package com.enderio.modded_conduits.datagen.client;

import com.enderio.modded_conduits.common.ModdedConduits;
import com.enderio.modded_conduits.common.ModuleModIds;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import net.neoforged.fml.ModList;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import com.enderio.modded_conduits.common.modules.appeng.AE2ConduitsModule;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModdedConduitsLanguageProvider extends LanguageProvider {
    
    public ModdedConduitsLanguageProvider(PackOutput output) {
        super(output, ModdedConduits.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addMekanismTranslations();
        addAE2Translations();
        addRefinedStorageTranslations();
    }
    
    private void addMekanismTranslations() {
        if (!ModList.get().isLoaded(ModuleModIds.MEKANISM)) {
            return;
        }

        add(MekanismModule.BASIC_CHEMICAL_FILTER.get(), "Basic Chemical Filter");
        add(MekanismModule.HEAT, "Heat Conduit");
        add(MekanismModule.CHEMICAL, "Chemical Conduit");
        add(MekanismModule.PRESSURIZED_CHEMICAL, "Pressurized Chemical Conduit");
        add(MekanismModule.ENDER_CHEMICAL, "Ender Chemical Conduit");
        add(MekanismModule.CHEMICAL_FILTER_SLOT_TOOLTIP, "Chemical Filter");
    }

    private void addAE2Translations() {
        if (!ModList.get().isLoaded(ModuleModIds.APPLIED_ENERGISTICS)) {
            return;
        }
//        // Conduits
        add(AE2ConduitsModule.NORMAL, "ME Conduit");
        add(AE2ConduitsModule.DENSE, "Dense ME Conduit");
    }
    
    private void addRefinedStorageTranslations() {
        // Conduits
//        add(RefinedStorageCommonModule.ConduitKeys.RS, "Refined Storage Conduit");
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
