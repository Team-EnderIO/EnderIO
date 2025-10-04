package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.common.init.EIOFluids;
import com.enderio.enderio.common.tag.EIOTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EIOFluidTagsProvider extends FluidTagsProvider {

    public EIOFluidTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, EnderIOAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(EIOTags.Fluids.COLD_FIRE_IGNITER_FUEL).add(EIOFluids.VAPOR_OF_LEVITY.getSource());
        tag(EIOTags.Fluids.STAFF_OF_LEVITY_FUEL).add(EIOFluids.VAPOR_OF_LEVITY.getSource());
    }
}
