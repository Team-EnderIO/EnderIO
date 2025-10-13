package com.enderio.enderio.datagen.common.loot;

import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class EIOBlockLootProvider extends BlockLootSubProvider {
    public EIOBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return EIOBlocks.BLOCKS.getEntries()
            .stream()
            .map(e -> (Block) e.value())
            .toList();
    }

    @Override
    protected void generate() {
        // Alloys
        dropSelf(EIOBlocks.COPPER_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.DARK_STEEL_BLOCK.get());
        dropSelf(EIOBlocks.SOULARIUM_BLOCK.get());
        dropSelf(EIOBlocks.END_STEEL_BLOCK.get());

        // Chassis
        dropSelf(EIOBlocks.VOID_CHASSIS.get());
        dropSelf(EIOBlocks.ENSOULED_CHASSIS.get());

        // Dark Steel Building Blocks
        dropSelf(EIOBlocks.DARK_STEEL_LADDER.get());
        dropSelf(EIOBlocks.DARK_STEEL_BARS.get());
        add(EIOBlocks.DARK_STEEL_DOOR.get(), createDoorTable(EIOBlocks.DARK_STEEL_DOOR.get()));
        dropSelf(EIOBlocks.DARK_STEEL_TRAPDOOR.get());
        dropSelf(EIOBlocks.END_STEEL_BARS.get());
        dropSelf(EIOBlocks.REINFORCED_OBSIDIAN.get());

        // Resetting Levers
        for (var lever : EIOBlocks.RESETTING_LEVERS) {
            dropSelf(lever.get());
        }

        // Miscellaneous
        dropSelf(EIOBlocks.SOUL_CHAIN.get());
        dropSelf(EIOBlocks.ENDERMAN_HEAD.get());
        dropSelf(EIOBlocks.INDUSTRIAL_INSULATION.get());
    }
}
