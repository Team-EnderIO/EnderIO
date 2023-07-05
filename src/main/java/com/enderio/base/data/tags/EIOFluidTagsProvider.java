package com.enderio.base.data.tags;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EIOFluidTagsProvider extends FluidTagsProvider {

    public EIOFluidTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider,
        @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(EIOTags.Fluids.COLD_FIRE_IGNITER_FUEL).add(EIOFluids.VAPOR_OF_LEVITY.get().getSource());
        tag(EIOTags.Fluids.STAFF_OF_LEVITY_FUEL).add(EIOFluids.VAPOR_OF_LEVITY.get().getSource());
    }
}
