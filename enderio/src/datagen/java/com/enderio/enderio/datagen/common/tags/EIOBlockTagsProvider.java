package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.ConduitBlocks;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.MachineBlocks;
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
        tag(BlockTags.CLIMBABLE).add(EIOBlocks.DARK_STEEL_LADDER.get());
        tag(BlockTags.DOORS).add(EIOBlocks.DARK_STEEL_DOOR.get());
        tag(BlockTags.TRAPDOORS).add(EIOBlocks.DARK_STEEL_TRAPDOOR.get());
        tag(BlockTags.WITHER_IMMUNE).add(EIOBlocks.REINFORCED_OBSIDIAN.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.COPPER_ALLOY_BLOCK.get())
            .add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get())
            .add(EIOBlocks.VIBRANT_ALLOY_BLOCK.get())
            .add(EIOBlocks.REDSTONE_ALLOY_BLOCK.get())
            .add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get())
            .add(EIOBlocks.PULSATING_ALLOY_BLOCK.get())
            .add(EIOBlocks.DARK_STEEL_BLOCK.get())
            .add(EIOBlocks.SOULARIUM_BLOCK.get())
            .add(EIOBlocks.END_STEEL_BLOCK.get())
            .add(EIOBlocks.VOID_CHASSIS.get())
            .add(EIOBlocks.ENSOULED_CHASSIS.get())
            .add(EIOBlocks.DARK_STEEL_LADDER.get())
            .add(EIOBlocks.DARK_STEEL_BARS.get())
            .add(EIOBlocks.DARK_STEEL_DOOR.get())
            .add(EIOBlocks.DARK_STEEL_TRAPDOOR.get())
            .add(EIOBlocks.END_STEEL_BARS.get())
            .add(EIOBlocks.REINFORCED_OBSIDIAN.get())
            .add(ConduitBlocks.CONDUIT_BUNDLE.get());

        // Blocks that need stone tools
        tag(BlockTags.NEEDS_STONE_TOOL)
            .add(EIOBlocks.COPPER_ALLOY_BLOCK.get())
            .add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get())
            .add(EIOBlocks.VIBRANT_ALLOY_BLOCK.get())
            .add(EIOBlocks.REDSTONE_ALLOY_BLOCK.get())
            .add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get())
            .add(EIOBlocks.PULSATING_ALLOY_BLOCK.get())
            .add(EIOBlocks.DARK_STEEL_BLOCK.get())
            .add(EIOBlocks.SOULARIUM_BLOCK.get())
            .add(EIOBlocks.END_STEEL_BLOCK.get())
            .add(EIOBlocks.VOID_CHASSIS.get())
            .add(EIOBlocks.ENSOULED_CHASSIS.get());

        // Iron tools
        tag(BlockTags.NEEDS_IRON_TOOL)
            .add(EIOBlocks.DARK_STEEL_LADDER.get())
            .add(EIOBlocks.DARK_STEEL_BARS.get())
            .add(EIOBlocks.DARK_STEEL_DOOR.get())
            .add(EIOBlocks.DARK_STEEL_TRAPDOOR.get())
            .add(EIOBlocks.END_STEEL_BARS.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .add(EIOBlocks.REINFORCED_OBSIDIAN.get());

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

        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(ConduitBlocks.CONDUIT_BUNDLE.get());

        // Alloys
        tag(EIOTags.Blocks.BLOCKS_COPPER_ALLOY).add(EIOBlocks.COPPER_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_ENERGETIC_ALLOY).add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_VIBRANT_ALLOY).add(EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_REDSTONE_ALLOY).add(EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_CONDUCTIVE_ALLOY).add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_PULSATING_ALLOY).add(EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_DARK_STEEL).add(EIOBlocks.DARK_STEEL_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_SOULARIUM).add(EIOBlocks.SOULARIUM_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_END_STEEL).add(EIOBlocks.END_STEEL_BLOCK.get());
    }
}
