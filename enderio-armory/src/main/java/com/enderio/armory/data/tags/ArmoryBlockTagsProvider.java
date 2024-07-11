package com.enderio.armory.data.tags;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.common.tag.ArmoryTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ArmoryBlockTagsProvider extends BlockTagsProvider {

    public ArmoryBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnderIOArmory.REGISTRY_NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ArmoryTags.Blocks.DARK_STEEL_EXPLODABLE_ALLOW_LIST);
        tag(ArmoryTags.Blocks.DARK_STEEL_EXPLODABLE_DENY_LIST);
    }
}
