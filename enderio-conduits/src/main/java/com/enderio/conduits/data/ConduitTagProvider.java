package com.enderio.conduits.data;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ConduitTagProvider extends BlockTagsProvider {
    public ConduitTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
        @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnderIOConduits.REGISTRY_NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ConduitTags.Blocks.REDSTONE_CONNECTABLE)
            .add(Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.REDSTONE_LAMP, Blocks.NOTE_BLOCK, Blocks.DISPENSER, Blocks.DROPPER, Blocks.POWERED_RAIL,
                Blocks.ACTIVATOR_RAIL, Blocks.MOVING_PISTON, Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB,
                Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB,
                Blocks.WAXED_OXIDIZED_COPPER_BULB)
            .addTags(BlockTags.DOORS, BlockTags.TRAPDOORS, BlockTags.REDSTONE_ORES);

        tag(ConduitTags.Blocks.RELOCATION_NOT_SUPPORTED).add(ConduitBlocks.CONDUIT.get());
    }
}
