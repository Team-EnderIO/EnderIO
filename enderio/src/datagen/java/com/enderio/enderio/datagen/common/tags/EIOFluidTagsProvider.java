package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class EIOFluidTagsProvider extends FluidTagsProvider {

    public EIOFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, EnderIOAPI.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 26.2-port: TagAppender.add() takes ResourceKey<Fluid>; convert via builtInRegistryHolder().unwrapKey()
        tag(Tags.Fluids.EXPERIENCE).add(EIOFluids.XP_JUICE.source().get().builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Fluids.SOLAR_PANEL_LIGHT).add(EIOFluids.LIQUID_SUNSHINE.source().get().builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Fluids.SOLAR_PANEL_DARK).add(EIOFluids.LIQUID_DARKNESS.source().get().builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Fluids.COLD_FIRE_IGNITER_FUEL).add(EIOFluids.VAPOR_OF_LEVITY.source().get().builtInRegistryHolder().unwrapKey().orElseThrow());
        tag(EIOTags.Fluids.STAFF_OF_LEVITY_FUEL).add(EIOFluids.VAPOR_OF_LEVITY.source().get().builtInRegistryHolder().unwrapKey().orElseThrow());
    }
}
