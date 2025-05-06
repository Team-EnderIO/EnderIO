package com.enderio.machines.data.datamap;

import com.enderio.machines.common.datamap.RangeExtender;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.tag.MachineTags;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;

public class RangeExtenderDataProvider extends DataMapProvider {
    private final Map<Block, Map<TagKey<Block>, Integer>> data = new HashMap<>();

    public RangeExtenderDataProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    public void addData(TagKey<Block> tag, Block block, int value) {
        data.computeIfAbsent(block, it -> new HashMap<>()).put(tag, value);
    }

    @Override
    protected void gather() {
        addData(MachineTags.Blocks.RANGE_EXTENDER, MachineBlocks.WIRELESS_CHARGER_ANTENNA.get(), 16);
        addData(MachineTags.Blocks.RANGE_EXTENDER, MachineBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get(), 32);

        var builder = builder(RangeExtender.DATA_MAP);
        data.forEach((block, map) -> {
            builder.add(block.builtInRegistryHolder(), map, false);
        });
    }
}
