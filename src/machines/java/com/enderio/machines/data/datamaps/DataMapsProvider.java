package com.enderio.machines.data.datamaps;

import com.enderio.machines.common.datamap.VatReagent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DataMapsProvider extends DataMapProvider {

    public DataMapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void gather() {
        builder(VatReagent.DATA_MAP)
            .add(Tags.Items.CROPS, Map.of(Tags.Items.CROPS, new VatReagent.ReagentType(0.5)), false)
            .add(Items.WHEAT.builtInRegistryHolder(), Map.of(Tags.Items.CROPS, new VatReagent.ReagentType(0.8)), false);
    }
}
