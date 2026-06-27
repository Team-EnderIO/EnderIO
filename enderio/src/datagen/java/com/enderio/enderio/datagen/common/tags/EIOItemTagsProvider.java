package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.content.glass.GlassLighting;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.WeatheringCopper;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EIOItemTagsProvider extends ItemTagsProvider {

    // 26.2-port: IntrinsicHolderTagsProvider was removed in 26.2. Items are now
    //   added to tags via the ResourceKey overload of TagAppender.add (see the
    //   .getKey() / .builtInRegistryHolder() calls below).

    public EIOItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, com.enderio.enderio.api.EnderIOAPI.MOD_ID);
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

        tag(ItemTags.SWORDS).add(EIOItems.DARK_STEEL_SWORD.getKey());
        tag(Tags.Items.CHAINS).add(EIOBlocks.SOUL_CHAIN.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());

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
            .add(Items.WOODEN_AXE.builtInRegistryHolder().unwrapKey().orElseThrow());

        addBlockItemTags();
    }

    private void addIngotTags() {
        tag(EIOTags.Items.INGOTS_ENERGETIC_ALLOY).add(EIOItems.ENERGETIC_ALLOY_INGOT.getKey());
        tag(EIOTags.Items.INGOTS_VIBRANT_ALLOY).add(EIOItems.VIBRANT_ALLOY_INGOT.getKey());
        tag(EIOTags.Items.INGOTS_REDSTONE_ALLOY).add(EIOItems.REDSTONE_ALLOY_INGOT.getKey());
        tag(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY).add(EIOItems.CONDUCTIVE_ALLOY_INGOT.getKey());
        tag(EIOTags.Items.INGOTS_PULSATING_ALLOY).add(EIOItems.PULSATING_ALLOY_INGOT.getKey());
        tag(EIOTags.Items.INGOTS_DARK_STEEL).add(EIOItems.DARK_STEEL_INGOT.getKey());
        tag(EIOTags.Items.INGOTS_SOULARIUM).add(EIOItems.SOULARIUM_INGOT.getKey());
        tag(EIOTags.Items.INGOTS_END_STEEL).add(EIOItems.END_STEEL_INGOT.getKey());

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
        tag(EIOTags.Items.NUGGETS_ENERGETIC_ALLOY).add(EIOItems.ENERGETIC_ALLOY_NUGGET.getKey());
        tag(EIOTags.Items.NUGGETS_VIBRANT_ALLOY).add(EIOItems.VIBRANT_ALLOY_NUGGET.getKey());
        tag(EIOTags.Items.NUGGETS_REDSTONE_ALLOY).add(EIOItems.REDSTONE_ALLOY_NUGGET.getKey());
        tag(EIOTags.Items.NUGGETS_CONDUCTIVE_ALLOY).add(EIOItems.CONDUCTIVE_ALLOY_NUGGET.getKey());
        tag(EIOTags.Items.NUGGETS_PULSATING_ALLOY).add(EIOItems.PULSATING_ALLOY_NUGGET.getKey());
        tag(EIOTags.Items.NUGGETS_DARK_STEEL).add(EIOItems.DARK_STEEL_NUGGET.getKey());
        tag(EIOTags.Items.NUGGETS_SOULARIUM).add(EIOItems.SOULARIUM_NUGGET.getKey());
        tag(EIOTags.Items.NUGGETS_END_STEEL).add(EIOItems.END_STEEL_NUGGET.getKey());

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
        tag(EIOTags.Items.GEMS_PULSATING_CRYSTAL).add(EIOItems.PULSATING_CRYSTAL.getKey());
        tag(EIOTags.Items.GEMS_VIBRANT_CRYSTAL).add(EIOItems.VIBRANT_CRYSTAL.getKey());
        tag(EIOTags.Items.GEMS_ENDER_CRYSTAL).add(EIOItems.ENDER_CRYSTAL.getKey());
        tag(EIOTags.Items.GEMS_ENTICING_CRYSTAL).add(EIOItems.ENTICING_CRYSTAL.getKey());
        tag(EIOTags.Items.GEMS_WEATHER_CRYSTAL).add(EIOItems.WEATHER_CRYSTAL.getKey());
        tag(EIOTags.Items.GEMS_PRESCIENT_CRYSTAL).add(EIOItems.PRESCIENT_CRYSTAL.getKey());

        tag(Tags.Items.GEMS)
            .addTag(EIOTags.Items.GEMS_PULSATING_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_ENDER_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_ENTICING_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_WEATHER_CRYSTAL)
            .addTag(EIOTags.Items.GEMS_PRESCIENT_CRYSTAL);
    }

    private void addDustsTags() {
        tag(EIOTags.Items.DUSTS_COAL).add(EIOItems.POWDERED_COAL.getKey());
        tag(EIOTags.Items.DUSTS_IRON).add(EIOItems.POWDERED_IRON.getKey());
        tag(EIOTags.Items.DUSTS_GOLD).add(EIOItems.POWDERED_GOLD.getKey());
        tag(EIOTags.Items.DUSTS_COPPER).add(EIOItems.POWDERED_COPPER.getKey());
        tag(EIOTags.Items.DUSTS_TIN).add(EIOItems.POWDERED_TIN.getKey());
        tag(EIOTags.Items.DUSTS_ENDER).add(EIOItems.POWDERED_ENDER_PEARL.getKey());
        tag(EIOTags.Items.DUSTS_OBSIDIAN).add(EIOItems.POWDERED_OBSIDIAN.getKey());
        tag(EIOTags.Items.DUSTS_LAPIS).add(EIOItems.POWDERED_LAPIS_LAZULI.getKey());
        tag(EIOTags.Items.DUSTS_QUARTZ).add(EIOItems.POWDERED_QUARTZ.getKey());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY).add(EIOItems.GRAINS_OF_INFINITY.getKey());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_PRESCIENCE).add(EIOItems.PRESCIENT_POWDER.getKey());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_VIBRANCY).add(EIOItems.VIBRANT_POWDER.getKey());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_PIZEALLITY).add(EIOItems.PULSATING_POWDER.getKey());
        tag(EIOTags.Items.DUSTS_GRAINS_OF_THE_END).add(EIOItems.ENDER_CRYSTAL_POWDER.getKey());

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
        tag(EIOTags.Items.GEARS_IRON).add(EIOItems.GEAR_IRON.getKey());
        tag(EIOTags.Items.GEARS_ENERGIZED).add(EIOItems.GEAR_ENERGIZED.getKey());
        tag(EIOTags.Items.GEARS_VIBRANT).add(EIOItems.GEAR_VIBRANT.getKey());
        tag(EIOTags.Items.GEARS_DARK_STEEL).add(EIOItems.GEAR_DARK_STEEL.getKey());

        tag(EIOTags.Items.GEARS)
            .addTag(EIOTags.Items.GEARS_IRON)
            .addTag(EIOTags.Items.GEARS_VIBRANT)
            .addTag(EIOTags.Items.GEARS_ENERGIZED)
            .addTag(EIOTags.Items.GEARS_DARK_STEEL);
    }

    private void addCommonItems() {
        // Ensure common tags are populated
        tag(EIOTags.Items.DUSTS_PRISMARINE).add(Items.PRISMARINE_SHARD.builtInRegistryHolder().unwrapKey().orElseThrow());

        // TODO: I disagree with this, they're not really storage blocks..
        tag(EIOTags.Items.STORAGE_BLOCKS_QUARTZ).add(Items.QUARTZ_BLOCK.builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Items.STORAGE_BLOCKS_AMETHYST).add(Items.AMETHYST_BLOCK.builtInRegistryHolder().unwrapKey().orElseThrow());

        tag(EIOTags.Items.SILICON).add(EIOItems.SILICON.getKey());
        tag(EIOTags.Items.WRENCH).add(EIOItems.YETA_WRENCH.getKey());
    }

    private void addHideFacadesTags() {
        tag(EIOTags.Items.HIDE_FACADES)
            .add(EIOItems.YETA_WRENCH.getKey())
            .add(EIOBlocks.CONDUIT_BUNDLE.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
    }

    private void addGliderTags() {
        tag(EIOTags.Items.GLIDER).add(EIOItems.GLIDER.getKey());
    }

    private void addReagentTags() {
        tag(EIOTags.Items.CROPS).addTag(Tags.Items.CROPS);
        tag(EIOTags.Items.SEEDS).addTag(Tags.Items.SEEDS);
        tag(EIOTags.Items.MEAT).addTag(ItemTags.MEAT);
        // 26.2-port: TagAppender.add() takes ResourceKey<T>[]; convert via builtInRegistryHolder().unwrapKey()
        tag(EIOTags.Items.EXPLOSIVES).add(
            Items.TNT.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.FIREWORK_STAR.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.FIREWORK_ROCKET.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.FIRE_CHARGE.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.GUNPOWDER.builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Items.NATURAL_LIGHTS).add(
            Items.GLOWSTONE_DUST.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.GLOWSTONE.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.SEA_LANTERN.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.SEA_PICKLE.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.GLOW_LICHEN.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.GLOW_BERRIES.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.GLOW_INK_SAC.builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Items.SUNFLOWER).add(
            Items.SUNFLOWER.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.TORCHFLOWER.builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Items.BLAZE_POWDER).add(Items.BLAZE_POWDER.builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Items.AMETHYST).add(Items.AMETHYST_SHARD.builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Items.PRISMARINE).add(Items.PRISMARINE_SHARD.builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Items.CLOUD_COLD).add(
            Items.SNOW.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.SNOW_BLOCK.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.SNOWBALL.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.ICE.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.PACKED_ICE.builtInRegistryHolder().unwrapKey().orElseThrow(),
            Items.BLUE_ICE.builtInRegistryHolder().unwrapKey().orElseThrow());
        // 26.2-port: Items.LIGHTNING_ROD is now a WeatheringCopperCollection<Item>
        tag(EIOTags.Items.LIGHTNING_ROD).add(Items.LIGHTNING_ROD.weathering().pick(WeatheringCopper.WeatherState.UNAFFECTED).builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Items.WIND_CHARGES).add(Items.WIND_CHARGE.builtInRegistryHolder().unwrapKey().orElseThrow());
    }

    private void addBlockItemTags() {
        this.tag(EIOTags.Items.BLOCKS_CONDUCTIVE_ALLOY).add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
        this.tag(EIOTags.Items.BLOCKS_ENERGETIC_ALLOY).add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
        this.tag(EIOTags.Items.BLOCKS_VIBRANT_ALLOY).add(EIOBlocks.VIBRANT_ALLOY_BLOCK.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
        this.tag(EIOTags.Items.BLOCKS_REDSTONE_ALLOY).add(EIOBlocks.REDSTONE_ALLOY_BLOCK.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
        this.tag(EIOTags.Items.BLOCKS_PULSATING_ALLOY).add(EIOBlocks.PULSATING_ALLOY_BLOCK.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
        this.tag(EIOTags.Items.BLOCKS_DARK_STEEL).add(EIOBlocks.DARK_STEEL_BLOCK.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
        this.tag(EIOTags.Items.BLOCKS_SOULARIUM).add(EIOBlocks.SOULARIUM_BLOCK.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
        this.tag(EIOTags.Items.BLOCKS_END_STEEL).add(EIOBlocks.END_STEEL_BLOCK.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());

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
                    fusedQuartzTag.add(block.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());

                    if (block.glassIdentifier().lighting() == GlassLighting.EMITTING) {
                        enlightenedFusedQuartzTag.add(block.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
                    }

                    if (block.glassIdentifier().lighting() == GlassLighting.BLOCKING) {
                        darkFusedQuartzTag.add(block.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
                    }
                } else {
                    clearGlassTag.add(block.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
                }
                this.tag(EIOTags.Items.GLASS_TAGS.get(block.glassIdentifier())).add(block.asItem().builtInRegistryHolder().unwrapKey().orElseThrow());
            }
        }
    }
}
