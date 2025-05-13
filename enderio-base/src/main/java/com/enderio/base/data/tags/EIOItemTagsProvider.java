package com.enderio.base.data.tags;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EIOItemTagsProvider extends ItemTagsProvider {

    public EIOItemTagsProvider(PackOutput pPackOutput, CompletableFuture<HolderLookup.Provider> pProvider,
            CompletableFuture<TagLookup<Block>> pLookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(pPackOutput, pProvider, pLookup, EnderIO.NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // copy(EIOTags.Blocks.CLEAR_GLASS, EIOTags.Items.CLEAR_GLASS);
        // copy(EIOTags.Blocks.FUSED_QUARTZ, EIOTags.Items.FUSED_QUARTZ);

        tag(Tags.Items.DUSTS).addTag(EIOTags.Items.DUSTS_COAL)
                .addTag(EIOTags.Items.DUSTS_LAPIS)
                .addTag(EIOTags.Items.DUSTS_QUARTZ)
                .addTag(EIOTags.Items.DUSTS_IRON)
                .addTag(EIOTags.Items.DUSTS_GOLD)
                .addTag(EIOTags.Items.DUSTS_COPPER)
                .addTag(EIOTags.Items.DUSTS_TIN)
                .addTag(EIOTags.Items.DUSTS_ENDER)
                .addTag(EIOTags.Items.DUSTS_OBSIDIAN)
                .addTag(EIOTags.Items.DUSTS_COBALT)
                .addTag(EIOTags.Items.DUSTS_PRISMARINE)
                .addTag(EIOTags.Items.DUSTS_GRAINS_OF_INFINITY)
                .addTag(EIOTags.Items.DUSTS_GRAINS_OF_PRESCIENCE)
                .addTag(EIOTags.Items.DUSTS_GRAINS_OF_VIBRANCY)
                .addTag(EIOTags.Items.DUSTS_GRAINS_OF_PIZEALLITY)
                .addTag(EIOTags.Items.DUSTS_GRAINS_OF_THE_END);

        tag(Tags.Items.INGOTS).addTag(EIOTags.Items.INGOTS_CONDUCTIVE_ALLOY)
                .addTag(EIOTags.Items.INGOTS_COPPER_ALLOY)
                .addTag(EIOTags.Items.INGOTS_DARK_STEEL)
                .addTag(EIOTags.Items.INGOTS_END_STEEL)
                .addTag(EIOTags.Items.INGOTS_ENERGETIC_ALLOY)
                .addTag(EIOTags.Items.INGOTS_PULSATING_ALLOY)
                .addTag(EIOTags.Items.INGOTS_REDSTONE_ALLOY)
                .addTag(EIOTags.Items.INGOTS_SOULARIUM)
                .addTag(EIOTags.Items.INGOTS_VIBRANT_ALLOY);

        tag(Tags.Items.NUGGETS).addTag(EIOTags.Items.NUGGETS_CONDUCTIVE_ALLOY)
                .addTag(EIOTags.Items.NUGGETS_COPPER_ALLOY)
                .addTag(EIOTags.Items.NUGGETS_DARK_STEEL)
                .addTag(EIOTags.Items.NUGGETS_END_STEEL)
                .addTag(EIOTags.Items.NUGGETS_ENERGETIC_ALLOY)
                .addTag(EIOTags.Items.NUGGETS_PULSATING_ALLOY)
                .addTag(EIOTags.Items.NUGGETS_REDSTONE_ALLOY)
                .addTag(EIOTags.Items.NUGGETS_SOULARIUM)
                .addTag(EIOTags.Items.NUGGETS_VIBRANT_ALLOY);

        tag(Tags.Items.STORAGE_BLOCKS).addTag(EIOTags.Items.STORAGE_BLOCKS_AMETHYST)
                .addTag(EIOTags.Items.STORAGE_BLOCKS_QUARTZ)
                .addTag(EIOTags.Items.BLOCKS_CONDUCTIVE_ALLOY)
                .addTag(EIOTags.Items.BLOCKS_COPPER_ALLOY)
                .addTag(EIOTags.Items.BLOCKS_DARK_STEEL)
                .addTag(EIOTags.Items.BLOCKS_END_STEEL)
                .addTag(EIOTags.Items.BLOCKS_ENERGETIC_ALLOY)
                .addTag(EIOTags.Items.BLOCKS_PULSATING_ALLOY)
                .addTag(EIOTags.Items.BLOCKS_REDSTONE_ALLOY)
                .addTag(EIOTags.Items.BLOCKS_SOULARIUM)
                .addTag(EIOTags.Items.BLOCKS_VIBRANT_ALLOY);

        tag(Tags.Items.GEMS).addTag(EIOTags.Items.GEMS_PULSATING_CRYSTAL)
                .addTag(EIOTags.Items.GEMS_VIBRANT_CRYSTAL)
                .addTag(EIOTags.Items.GEMS_ENDER_CRYSTAL)
                .addTag(EIOTags.Items.GEMS_ENTICING_CRYSTAL)
                .addTag(EIOTags.Items.GEMS_WEATHER_CRYSTAL)
                .addTag(EIOTags.Items.GEMS_PRESCIENT_CRYSTAL);

        tag(EIOTags.Items.GEARS)
                .addTag(EIOTags.Items.GEARS_IRON)
                .addTag(EIOTags.Items.GEARS_VIBRANT)
                .addTag(EIOTags.Items.GEARS_ENERGIZED)
                .addTag(EIOTags.Items.GEARS_DARK_STEEL);

        tag(EIOTags.Items.INSULATION_METAL).addTag(EIOTags.Items.DUSTS_IRON).addTag(EIOTags.Items.DUSTS_TIN);

        // Common tags
        tag(EIOTags.Items.DUSTS_PRISMARINE).add(Items.PRISMARINE_SHARD);

        tag(EIOTags.Items.STORAGE_BLOCKS_QUARTZ).add(Items.QUARTZ_BLOCK);

        tag(EIOTags.Items.STORAGE_BLOCKS_AMETHYST).add(Items.AMETHYST_BLOCK);
    }
}
