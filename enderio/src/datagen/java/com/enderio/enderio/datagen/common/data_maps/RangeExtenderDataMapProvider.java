package com.enderio.enderio.datagen.common.data_maps;

import com.enderio.enderio.foundation.datamap.RangeExtender;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.DataMapProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RangeExtenderDataMapProvider extends DataMapProvider {
    private final Map<Block, Map<TagKey<Block>, Integer>> data = new HashMap<>();

    public RangeExtenderDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    public void addData(TagKey<Block> tag, Block block, int value) {
        data.computeIfAbsent(block, it -> new HashMap<>()).put(tag, value);
    }

    @Override
    protected void gather() {
        addData(EIOTags.Blocks.RANGE_EXTENDER, EIOBlocks.WIRELESS_CHARGER_ANTENNA.get(), 16);
        addData(EIOTags.Blocks.RANGE_EXTENDER, EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get(), 32);

        var builder = builder(RangeExtender.DATA_MAP);
        data.forEach((block, map) -> {
            builder.add(block.builtInRegistryHolder(), map, false);
        });
    }
}
