package com.enderio.conduits.data.tags;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.init.ConduitTags;
import com.enderio.conduits.common.init.EIOConduitTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ConduitTagsProvider extends TagsProvider<ConduitType<?, ?, ?>> {

    public ConduitTagsProvider(PackOutput packOutput,
        CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, EnderIORegistries.Keys.CONDUIT_TYPES, registries, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ConduitTags.ConduitTypes.ITEM)
            .add(EIOConduitTypes.Types.ITEM.getKey());

        tag(ConduitTags.ConduitTypes.FLUID)
            .add(EIOConduitTypes.Types.FLUID.getKey(),
                EIOConduitTypes.Types.FLUID2.getKey(),
                EIOConduitTypes.Types.FLUID3.getKey());

        tag(ConduitTags.ConduitTypes.ENERGY)
            .add(EIOConduitTypes.Types.ENERGY.getKey());

        tag(ConduitTags.ConduitTypes.REDSTONE)
            .add(EIOConduitTypes.Types.REDSTONE.getKey());
    }
}
