package com.enderio.conduits.data;

import com.enderio.EnderIO;
import com.enderio.conduits.common.ConduitTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ConduitTagProvider extends BlockTagsProvider {
    public ConduitTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
        @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ConduitTags.REDSTONE_CONNECTABLE)
            .add(Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.REDSTONE_LAMP, Blocks.NOTE_BLOCK, Blocks.DISPENSER, Blocks.DROPPER,
                Blocks.POWERED_RAIL, Blocks.ACTIVATOR_RAIL)
            .addTags(BlockTags.DOORS, BlockTags.TRAPDOORS, BlockTags.REDSTONE_ORES);
    }
}
