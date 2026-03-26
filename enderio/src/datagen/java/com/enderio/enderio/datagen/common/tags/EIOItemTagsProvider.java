package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.content.glass.GlassLighting;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EIOItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {

    public EIOItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider, p_421303_ -> p_421303_.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // TODO
//        copy(EIOTags.Blocks.CLEAR_GLASS, EIOTags.Items.CLEAR_GLASS);
//        copy(EIOTags.Blocks.FUSED_QUARTZ, EIOTags.Items.FUSED_QUARTZ);

        addDustsTags();
        addIngotTags();
        addNuggetTags();

        tag(Tags.Items.STORAGE_BLOCKS)
            .addTag(EIOTags.Items.STORAGE_BLOCKS_AMETHYST)
            .addTag(EIOTags.Items.STORAGE_BLOCKS_QUARTZ)
            .addTag(EIOTags.Items.BLOCKS_CONDUCTIVE_ALLOY)
            .addTag(EIOTags.Items.BLOCKS_DARK_STEEL)
            .addTag(EIOTags.Items.BLOCKS_END_STEEL)
            .addTag(EIOTags.Items.BLOCKS_ENERGETIC_ALLOY)
            .addTag(EIOTags.Items.BLOCKS_PULSATING_ALLOY)
            .addTag(EIOTags.Items.BLOCKS_REDSTONE_ALLOY)
            .addTag(EIOTags.Items.BLOCKS_SOULARIUM)
            .addTag(EIOTags.Items.BLOCKS_VIBRANT_ALLOY);

        tag(ItemTags.SWORDS).add(EIOItems.DARK_STEEL_SWORD.get());
        tag(Tags.Items.CHAINS).add(EIOBlocks.SOUL_CHAIN.asItem());

        addCrystalTags();
        addGearTags();

        tag(EIOTags.Items.INSULATION_METAL)
            .addTag(EIOTags.Items.DUSTS_IRON)
            .addTag(EIOTags.Items.DUSTS_TIN);

        addCommonItems();
        addHideFacadesTags();
        addGliderTags();
        addReagentTags();

        tag(EIOTags.Items.SLICER_INCOMPATIBLE_AXE)
            .add(Items.WOODEN_AXE);

        addBlockItemTags();
    }

    private void addIngotTags() {
        tag(EIOTags.Items.INGOTS_ENERGETIC_ALLOY).add(EIOItems.ENERGETIC_ALLOY_INGOT.get());
        tag(EIOTags.Items.INGOTS_VIBRANT_ALLOY).add(EIOItems.VIBRANT_ALLOY_INGOT.get());
        tag(EIOTags.Items.INGOTS_REDSTONE_ALLOY).add(EIOItems.REDSTONE_ALLOY_INGOT.get());
        tag(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY).add(EIOItems.CONDUCTIVE_ALLOY_INGOT.get());
        tag(EIOTags.Items.INGOTS_PULSATING_ALLOY).add(EIOItems.PULSATING_ALLOY_INGOT.get());
        tag(EIOTags.Items.INGOTS_DARK_STEEL).add(EIOItems.DARK_STEEL_INGOT.get());
        tag(EIOTags.Items.INGOTS_SOULARIUM).add(EIOItems.SOULARIUM_INGOT.get());
        tag(EIOTags.Items.INGOTS_END_STEEL).add(EIOItems.END_STEEL_INGOT.get());

        tag(Tags.Items.INGOTS)
            .addTag(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
            .addTag(EIOTags.Items.INGOTS_DARK_STEEL)
            .addTag(EIOTags.Items.INGOTS_END_STEEL)
            .addTag(EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
            .addTag(EIOTags.Items.INGOTS_PULSATING_ALLOY)
            .addTag(EIOTags.Items.INGOTS_REDSTONE_ALLOY)
            .addTag(EIOTags.Items.INGOTS_SOULARIUM)
            .addTag(EIOTags.Items.INGOTS_VIBRANT_ALLOY);
    }

    private void addNuggetTags() {
        tag(EIOTags.Items.NUGGETS_ENERGETIC_ALLOY).add(EIOItems.ENERGETIC_ALLOY_NUGGET.get());
        tag(EIOTags.Items.NUGGETS_VIBRANT_ALLOY).add(EIOItems.VIBRANT_ALLOY_NUGGET.get());
        tag(EIOTags.Items.NUGGETS_REDSTONE_ALLOY).add(EIOItems.REDSTONE_ALLOY_NUGGET.get());
        tag(EIOTags.Items.NUGGETS_CONDUCTIVE_ALLOY).add(EIOItems.CONDUCTIVE_ALLOY_NUGGET.get());
        tag(EIOTags.Items.NUGGETS_PULSATING_ALLOY).add(EIOItems.PULSATING_ALLOY_NUGGET.get());
        tag(EIOTags.Items.NUGGETS_DARK_STEEL).add(EIOItems.DARK_STEEL_NUGGET.get());
        tag(EIOTags.Items.NUGGETS_SOULARIUM).add(EIOItems.SOULARIUM_NUGGET.get());
        tag(EIOTags.Items.NUGGETS_END_STEEL).add(EIOItems.END_STEEL_NUGGET.get());

        tag(Tags.Items.NUGGETS)
            .addTag(EIOTags.Items.NUGGETS_CONDUCTIVE_ALLOY)
            .addTag(EIOTags.Items.NUGGETS_DARK_STEEL)
            .addTag(EIOTags.Items.NUGGETS_END_STEEL)
            .addTag(EIOTags.Items.NUGGETS_ENERGETIC_ALLOY)
            .addTag(EIOTags.Items.NUGGETS_PULSATING_ALLOY)
            .addTag(EIOTags.Items.NUGGETS_REDSTONE_ALLOY)
            .addTag(EIOTags.Items.NUGGETS_SOULARIUM)
            .addTag(EIOTags.Items.NUGGETS_VIBRANT_ALLOY);
    }

    private void addCrystalTags() {
        tag(EIOTags.Items.GEMS_PULSATING_CRYSTAL).add(EIOItems.PULSATING_CRYSTAL.get());
        tag(EIOTags.Items.GEMS_VIBRANT_CRYSTAL).add(EIOItems.VIBRANT_CRYSTAL.get());
        tag(EIOTags.Items.GEMS_ENDER_CRYSTAL).add(EIOItems.ENDER_CRYSTAL.get());
        tag(EIOTags.Items.GEMS_ENTICING_CRYSTAL).add(EIOItems.ENTICING_CRYSTAL.get());
        tag(EIOTags.Items.GEMS_WEATHER_CRYSTAL).add(EIOItems.WEATHER_CRYSTAL.get());
        tag(EIOTags.Items.GEMS_PRESCIENT_CRYSTAL).add(EIOItems.PRESCIENT_CRYSTAL.get());

        tag(Tags.Items.GEMS)
            .addTag(EIOTags.Items.GEMS_PULSATING_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_ENDER_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_ENTICING_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_WEATHER_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_PRESCIENT_CRYSTAL);
    }

    private void addDustsTags() {
        tag(EIOTags.Items.DUSTS_COAL).add(EIOItems.POWDERED_COAL.get());
        tag(EIOTags.Items.DUSTS_IRON).add(EIOItems.POWDERED_IRON.get());
        tag(EIOTags.Items.DUSTS_GOLD).add(EIOItems.POWDERED_GOLD.get());
        tag(EIOTags.Items.DUSTS_COPPER).add(EIOItems.POWDERED_COPPER.get());
        tag(EIOTags.Items.DUSTS_TIN).add(EIOItems.POWDERED_TIN.get());
        tag(EIOTags.Items.DUSTS_ENDER).add(EIOItems.POWDERED_ENDER_PEARL.get());
        tag(EIOTags.Items.DUSTS_OBSIDIAN).add(EIOItems.POWDERED_OBSIDIAN.get());
        tag(EIOTags.Items.DUSTS_LAPIS).add(EIOItems.POWDERED_LAPIS_LAZULI.get());
        tag(EIOTags.Items.DUSTS_QUARTZ).add(EIOItems.POWDERED_QUARTZ.get());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY).add(EIOItems.GRAINS_OF_INFINITY.get());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_PRESCIENCE).add(EIOItems.PRESCIENT_POWDER.get());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_VIBRANCY).add(EIOItems.VIBRANT_POWDER.get());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_PIZEALLITY).add(EIOItems.PULSATING_POWDER.get());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_THE_END).add(EIOItems.ENDER_CRYSTAL_POWDER.get());

        tag(Tags.Items.DUSTS)
            .addTag(EIOTags.Items.DUSTS_COAL)
            .addTag(EIOTags.Items.DUSTS_LAPIS)
            .addTag(EIOTags.Items.DUSTS_QUARTZ)
            .addTag(EIOTags.Items.DUSTS_IRON)
            .addTag(EIOTags.Items.DUSTS_GOLD)
            .addTag(EIOTags.Items.DUSTS_COPPER)
            .addTag(EIOTags.Items.DUSTS_TIN)
            .addTag(EIOTags.Items.DUSTS_ENDER)
            .addTag(EIOTags.Items.DUSTS_OBSIDIAN)
            .addTag(EIOTags.Items.DUSTS_PRISMARINE)
            .addTag(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
            .addTag(EIOTags.Items.DUSTS_GRAINS_OF_PRESCIENCE)
            .addTag(EIOTags.Items.DUSTS_GRAINS_OF_VIBRANCY)
            .addTag(EIOTags.Items.DUSTS_GRAINS_OF_PIZEALLITY)
            .addTag(EIOTags.Items.DUSTS_GRAINS_OF_THE_END);
    }

    private void addGearTags() {
        tag(EIOTags.Items.GEARS_IRON).add(EIOItems.GEAR_IRON.get());
        tag(EIOTags.Items.GEARS_ENERGIZED).add(EIOItems.GEAR_ENERGIZED.get());
        tag(EIOTags.Items.GEARS_VIBRANT).add(EIOItems.GEAR_VIBRANT.get());
        tag(EIOTags.Items.GEARS_DARK_STEEL).add(EIOItems.GEAR_DARK_STEEL.get());

        tag(EIOTags.Items.GEARS)
            .addTag(EIOTags.Items.GEARS_IRON)
            .addTag(EIOTags.Items.GEARS_VIBRANT)
            .addTag(EIOTags.Items.GEARS_ENERGIZED)
            .addTag(EIOTags.Items.GEARS_DARK_STEEL);
    }

    private void addCommonItems() {
        // Ensure common tags are populated
        tag(EIOTags.Items.DUSTS_PRISMARINE).add(Items.PRISMARINE_SHARD);

        // TODO: I disagree with this, they're not really storage blocks..
        tag(EIOTags.Items.STORAGE_BLOCKS_QUARTZ).add(Items.QUARTZ_BLOCK);
        tag(EIOTags.Items.STORAGE_BLOCKS_AMETHYST).add(Items.AMETHYST_BLOCK);

        tag(EIOTags.Items.SILICON).add(EIOItems.SILICON.get());
        tag(EIOTags.Items.WRENCH).add(EIOItems.YETA_WRENCH.get());
    }

    private void addHideFacadesTags() {
        tag(EIOTags.Items.HIDE_FACADES)
            .add(EIOItems.YETA_WRENCH.get())
            .add(EIOBlocks.CONDUIT_BUNDLE.asItem());
    }

    private void addGliderTags() {
        tag(EIOTags.Items.GLIDER).add(EIOItems.GLIDER.get());
    }

    private void addReagentTags() {
        tag(EIOTags.Items.CROPS).addTag(Tags.Items.CROPS);
        tag(EIOTags.Items.SEEDS).addTag(Tags.Items.SEEDS);
        tag(EIOTags.Items.MEAT).addTag(ItemTags.MEAT);
        tag(EIOTags.Items.EXPLOSIVES).add(Items.TNT, Items.FIREWORK_STAR, Items.FIREWORK_ROCKET, Items.FIRE_CHARGE, Items.GUNPOWDER);
        tag(EIOTags.Items.NATURAL_LIGHTS).add(Items.GLOWSTONE_DUST, Items.GLOWSTONE, Items.SEA_LANTERN,
            Items.SEA_PICKLE, Items.GLOW_LICHEN, Items.GLOW_BERRIES, Items.GLOW_INK_SAC);
        tag(EIOTags.Items.SUNFLOWER).add(Items.SUNFLOWER, Items.TORCHFLOWER);
        tag(EIOTags.Items.BLAZE_POWDER).add(Items.BLAZE_POWDER);
        tag(EIOTags.Items.AMETHYST).add(Items.AMETHYST_SHARD);
        tag(EIOTags.Items.PRISMARINE).add(Items.PRISMARINE_SHARD);
        tag(EIOTags.Items.CLOUD_COLD).add(Items.SNOW, Items.SNOW_BLOCK, Items.SNOWBALL, Items.ICE, Items.PACKED_ICE,
            Items.BLUE_ICE);
        tag(EIOTags.Items.LIGHTNING_ROD).add(Items.LIGHTNING_ROD);
        tag(EIOTags.Items.WIND_CHARGES).add(Items.WIND_CHARGE);
    }

    private void addBlockItemTags() {
        this.tag(EIOTags.Items.BLOCKS_CONDUCTIVE_ALLOY).add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.asItem());
        this.tag(EIOTags.Items.BLOCKS_ENERGETIC_ALLOY).add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.asItem());
        this.tag(EIOTags.Items.BLOCKS_VIBRANT_ALLOY).add(EIOBlocks.VIBRANT_ALLOY_BLOCK.asItem());
        this.tag(EIOTags.Items.BLOCKS_REDSTONE_ALLOY).add(EIOBlocks.REDSTONE_ALLOY_BLOCK.asItem());
        this.tag(EIOTags.Items.BLOCKS_PULSATING_ALLOY).add(EIOBlocks.PULSATING_ALLOY_BLOCK.asItem());
        this.tag(EIOTags.Items.BLOCKS_DARK_STEEL).add(EIOBlocks.DARK_STEEL_BLOCK.asItem());
        this.tag(EIOTags.Items.BLOCKS_SOULARIUM).add(EIOBlocks.SOULARIUM_BLOCK.asItem());
        this.tag(EIOTags.Items.BLOCKS_END_STEEL).add(EIOBlocks.END_STEEL_BLOCK.asItem());

        var fusedQuartzTag = tag(EIOTags.Items.FUSED_QUARTZ);
        var enlightenedFusedQuartzTag = tag(EIOTags.Items.ENLIGHTENED_FUSED_QUARTZ);
        var darkFusedQuartzTag = tag(EIOTags.Items.DARK_FUSED_QUARTZ);
        var clearGlassTag = tag(EIOTags.Items.CLEAR_GLASS);

        var glassBlockCollections = EIOBlocks.GLASS_BLOCKS.entrySet()
            .stream()
            .sorted(Comparator.comparing(a -> a.getKey().glassName()))
            .map(Map.Entry::getValue)
            .toList();

        for (var glassBlocks : glassBlockCollections) {
            var glassItems = new ArrayList<>(glassBlocks.getAllBlocks()
                .sorted(Comparator.comparing(DeferredHolder::getKey))
                .map(DeferredHolder::get)
                .toList());

            for (var block : glassItems) {
                if (block.glassIdentifier().explosionResistance()) {
                    fusedQuartzTag.add(block.asItem());

                    if (block.glassIdentifier().lighting() == GlassLighting.EMITTING) {
                        enlightenedFusedQuartzTag.add(block.asItem());
                    }

                    if (block.glassIdentifier().lighting() == GlassLighting.BLOCKING) {
                        darkFusedQuartzTag.add(block.asItem());
                    }
                } else {
                    clearGlassTag.add(block.asItem());
                }
                this.tag(EIOTags.Items.GLASS_TAGS.get(block.glassIdentifier())).add(block.asItem());
            }
        }
    }
}
