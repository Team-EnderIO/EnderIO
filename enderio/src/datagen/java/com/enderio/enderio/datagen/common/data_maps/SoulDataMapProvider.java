package com.enderio.enderio.datagen.common.data_maps;

import com.enderio.enderio.foundation.souldata.EngineSoul;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIODataMaps;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.concurrent.CompletableFuture;

public class SoulDataMapProvider extends DataMapProvider {
    public SoulDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        HolderGetter<Fluid> fluids = provider.lookupOrThrow(Registries.FLUID);

        builder(EIODataMaps.ENGINE_SOULS)
            .add(EntityTypeIds.BLAZE, new EngineSoul(FluidIngredient.of(fluids.getOrThrow(FluidTags.LAVA)), 800, 18), false)
            .add(EIOTags.EntityTypes.ZOMBIES, new EngineSoul(FluidIngredient.of(EIOFluids.NUTRIENT_DISTILLATION.source().get()), 1000, 18), false)
            .add(EIOTags.EntityTypes.ENDERMEN, new EngineSoul(FluidIngredient.of(EIOFluids.DEW_OF_THE_VOID.source().get()), 1200, 12), false)
            .add(EIOTags.EntityTypes.CREEPERS, new EngineSoul(FluidIngredient.of(EIOFluids.ROCKET_FUEL.source().get()), 800, 12), false)
            .build();
    }

    @Override
    public String getName() {
        return "EnderIO Engine Soul Data Map";
    }
}
