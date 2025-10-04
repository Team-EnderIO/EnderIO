package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.common.tag.EIOTags;
import com.enderio.enderio.common.init.ConduitBlocks;
import com.enderio.enderio.machines.common.init.MachineBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EIOBlockTagsProvider extends BlockTagsProvider {

    public EIOBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnderIOAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(Tags.Blocks.STORAGE_BLOCKS).addTag(EIOTags.Blocks.BLOCKS_CONDUCTIVE_ALLOY)
                .addTag(EIOTags.Blocks.BLOCKS_COPPER_ALLOY)
                .addTag(EIOTags.Blocks.BLOCKS_DARK_STEEL)
                .addTag(EIOTags.Blocks.BLOCKS_END_STEEL)
                .addTag(EIOTags.Blocks.BLOCKS_ENERGETIC_ALLOY)
                .addTag(EIOTags.Blocks.BLOCKS_PULSATING_ALLOY)
                .addTag(EIOTags.Blocks.BLOCKS_REDSTONE_ALLOY)
                .addTag(EIOTags.Blocks.BLOCKS_SOULARIUM)
                .addTag(EIOTags.Blocks.BLOCKS_VIBRANT_ALLOY);

        tag(EIOTags.Blocks.BLOCKS_TELEPORTATION)
            .add(MachineBlocks.TRAVEL_ANCHOR.get())
            .add(MachineBlocks.PAINTED_TRAVEL_ANCHOR.get());

        tag(EIOTags.Blocks.REDSTONE_CONNECTABLE)
            .add(Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.REDSTONE_LAMP, Blocks.NOTE_BLOCK, Blocks.DISPENSER,
                Blocks.DROPPER, Blocks.POWERED_RAIL, Blocks.ACTIVATOR_RAIL, Blocks.MOVING_PISTON,
                Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB,
                Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB,
                Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB, Blocks.CRAFTER)
            .addTags(BlockTags.DOORS, BlockTags.TRAPDOORS, BlockTags.REDSTONE_ORES);

        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(ConduitBlocks.CONDUIT.get());
    }
}
