package com.enderio.enderio.datagen.common.data_maps;

import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class GrindingBallDataMapProvider extends DataMapProvider {

    public GrindingBallDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(GrindingBallData.DATA_MAP_TYPE)
            .add(Items.FLINT.builtInRegistryHolder(), new GrindingBallData(1.2F, 1.25F, 0.85F, 24000), false)
            .add(EIOItems.ENERGETIC_ALLOY_BALL.getId(), new GrindingBallData(1.6F, 1.1F, 1.1F, 80000), false)
            .add(EIOItems.VIBRANT_ALLOY_BALL.getId(), new GrindingBallData(1.75F, 1.35F, 1.13F, 80000), false)
            .add(EIOItems.REDSTONE_ALLOY_BALL.getId(), new GrindingBallData(1.00F, 1.00F, 0.35F, 30000), false)
            .add(EIOItems.CONDUCTIVE_ALLOY_BALL.getId(), new GrindingBallData(1.35F, 1.00F, 1.0F, 40000), false)
            .add(EIOItems.PULSATING_ALLOY_BALL.getId(), new GrindingBallData(1.00F, 1.85F, 1.0F, 100000), false)
            .add(EIOItems.DARK_STEEL_BALL.getId(), new GrindingBallData(1.35F, 2.00F, 0.7F, 125000), false)
            .add(EIOItems.SOULARIUM_BALL.getId(), new GrindingBallData(1.2F, 2.15F, 0.9F, 80000), false)
            .add(EIOItems.END_STEEL_BALL.getId(), new GrindingBallData(1.4F, 2.4F, 0.7F, 75000), false);
    }

    @Override
    public String getName() {
        return "Grinding Ball Datamaps";
    }
}
