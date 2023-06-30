package com.enderio.base.data.tags;

import com.enderio.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EIOItemTagsProvider extends ItemTagsProvider {

    public EIOItemTagsProvider(PackOutput pPackOutput, CompletableFuture<HolderLookup.Provider> pProvider, CompletableFuture<TagLookup<Block>> pLookup,
        @Nullable ExistingFileHelper existingFileHelper) {
        super(pPackOutput, pProvider, pLookup, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        //copy(EIOTags.Blocks.CLEAR_GLASS, EIOTags.Items.CLEAR_GLASS);
        //copy(EIOTags.Blocks.FUSED_QUARTZ, EIOTags.Items.FUSED_QUARTZ);
        tag(Tags.Items.DUSTS).addTag(EIOTags.Items.DUSTS_COAL).addTag(EIOTags.Items.DUSTS_LAPIS).addTag(EIOTags.Items.DUSTS_QUARTZ).addTag(EIOTags.Items.DUSTS_IRON).addTag(EIOTags.Items.DUSTS_GOLD).addTag(EIOTags.Items.DUSTS_COPPER).addTag(EIOTags.Items.DUSTS_TIN).addTag(EIOTags.Items.DUSTS_ENDER).addTag(EIOTags.Items.DUSTS_OBSIDIAN).addTag(EIOTags.Items.DUSTS_COBALT);
        tag(EIOTags.Items.GEARS).addTag(EIOTags.Items.GEARS_WOOD).addTag(EIOTags.Items.GEARS_STONE).addTag(EIOTags.Items.GEARS_IRON).addTag(EIOTags.Items.GEARS_VIBRANT).addTag(EIOTags.Items.GEARS_ENERGIZED).addTag(EIOTags.Items.GEARS_DARK_STEEL);
        tag(EIOTags.Items.INSULATION_METAL).addTag(EIOTags.Items.DUSTS_IRON).addTag(EIOTags.Items.DUSTS_TIN);
    }
}
