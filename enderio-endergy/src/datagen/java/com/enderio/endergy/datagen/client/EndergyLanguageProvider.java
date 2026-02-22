package com.enderio.endergy.datagen.client;

import com.enderio.core.common.registries.FluidDeferredHolders;
import com.enderio.endergy.common.EnderIOEndergy;
import com.enderio.endergy.common.EndergyConduits;
import com.enderio.endergy.common.init.EndergyBlocks;
import com.enderio.endergy.common.init.EndergyItems;
import com.enderio.endergy.common.lang.EndergyCommonComponents;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;

public class EndergyLanguageProvider extends LanguageProvider {
    public EndergyLanguageProvider(PackOutput output) {
        super(output, EnderIOEndergy.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addConduitDescriptions();
        addItems();
        addBlocks();
        addCommonLang();
    }

    private void addConduitDescriptions() {
        add(EndergyConduits.CRUDE_ENERGY, "Crude Energy Conduit");
        add(EndergyConduits.COPPER_ENERGY, "Copper Energy Conduit");
        add(EndergyConduits.IRON_ENERGY, "Iron Energy Conduit");
        add(EndergyConduits.GOLD_ENERGY, "Gold Energy Conduit");
        add(EndergyConduits.CRYSTALLINE_ENERGY, "Crystalline Energy Conduit");
        add(EndergyConduits.MELODIC_ENERGY, "Melodic Energy Conduit");
        add(EndergyConduits.STELLAR_ENERGY, "Stellar Energy Conduit");
    }
    
    private void addItems() {
        // Alloys
        add(EndergyItems.CRUDE_STEEL_INGOT.get(), "Crude Steel Ingot");
        add(EndergyItems.CRYSTALLINE_ALLOY_INGOT.get(), "Crystalline Alloy Ingot");
        add(EndergyItems.MELODIC_ALLOY_INGOT.get(), "Melodic Alloy Ingot");
        add(EndergyItems.STELLAR_ALLOY_INGOT.get(), "Stellar Alloy Ingot");
        add(EndergyItems.VIVID_ALLOY_INGOT.get(), "Vivid Alloy Ingot");

        add(EndergyItems.CRUDE_STEEL_NUGGET.get(), "Crude Steel Nugget");
        add(EndergyItems.CRYSTALLINE_ALLOY_NUGGET.get(), "Crystalline Alloy Nugget");
        add(EndergyItems.MELODIC_ALLOY_NUGGET.get(), "Melodic Alloy Nugget");
        add(EndergyItems.STELLAR_ALLOY_NUGGET.get(), "Stellar Alloy Nugget");
        add(EndergyItems.VIVID_ALLOY_NUGGET.get(), "Vivid Alloy Nugget");

        // Grinding balls
//        add(EndergyItems.SOULARIUM_BALL.get(), "Soularium Grinding Ball");
//        add(EndergyItems.CONDUCTIVE_ALLOY_BALL.get(), "Conductive Alloy Grinding Ball");
//        add(EndergyItems.PULSATING_ALLOY_BALL.get(), "Pulsating Alloy Grinding Ball");
//        add(EndergyItems.REDSTONE_ALLOY_BALL.get(), "Redstone Alloy Grinding Ball");
//        add(EndergyItems.ENERGETIC_ALLOY_BALL.get(), "Energetic Alloy Grinding Ball");

        // Capacitors
        add(EndergyItems.GRAINY_CAPACITOR.get(), "Grainy Capacitor");
        add(EndergyItems.VIVID_CAPACITOR.get(), "Vivid Capacitor");
        add(EndergyItems.CRYSTALLINE_CAPACITOR.get(), "Crystalline Capacitor");
        add(EndergyItems.MELODIC_CAPACITOR.get(), "Melodic Capacitor");
        add(EndergyItems.STELLAR_CAPACITOR.get(), "Stellar Capacitor");
        add(EndergyItems.TOTEMIC_CAPACITOR.get(), "Totemic Capacitor");
    }

    private void addBlocks() {
        // Alloys
        add(EndergyBlocks.CRUDE_STEEL_BLOCK.get(), "Crude Steel Block");
        add(EndergyBlocks.CRYSTALLINE_ALLOY_BLOCK.get(), "Crystalline Alloy Block");
        add(EndergyBlocks.MELODIC_ALLOY_BLOCK.get(), "Melodic Alloy Block");
        add(EndergyBlocks.STELLAR_ALLOY_BLOCK.get(), "Stellar Alloy Block");
        add(EndergyBlocks.VIVID_ALLOY_BLOCK.get(), "Vivid Alloy Block");
    }

    private void addCommonLang() {
        add(EndergyCommonComponents.CREATIVE_TAB_TITLE, "Endergy");
        add(EndergyCommonComponents.TOTEMIC_CAPACITOR_TOOLTIP, "Can be enchanted with Efficiency to increase the modifier.");
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

    public void add(FluidDeferredHolders key, String name) {
        if (key.bucket() != null) {
            add(key.bucket().get(), name + " Bucket");
        }

        add(key.block().get(), name);
        add(key.type().get(), name);
    }

    public void add(FluidType key, String name) {
        this.add(key.getDescriptionId(), name);
    }
}
