package com.enderio.conduits.data;

import com.enderio.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ConduitItemTagProvider extends ItemTagsProvider {
    public ConduitItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
        CompletableFuture<TagLookup<Block>> blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsProvider, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // Items that should hide facades when held
        tag(ConduitTags.Items.HIDE_FACADES)
            .addTag(EIOTags.Items.WRENCH)
            .add(ConduitItems.ENERGY.get())
            .add(ConduitItems.FLUID.get())
            .add(ConduitItems.PRESSURIZED_FLUID.get())
            .add(ConduitItems.ENDER_FLUID.get())
            .add(ConduitItems.REDSTONE.get())
            .add(ConduitItems.ITEM.get());
    }
}
