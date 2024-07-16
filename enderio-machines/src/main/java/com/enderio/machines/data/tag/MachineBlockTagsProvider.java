package com.enderio.machines.data.tag;

import com.enderio.EnderIOBase;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class MachineBlockTagsProvider extends BlockTagsProvider {

    public MachineBlockTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider, net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        super(packOutput, provider, EnderIOBase.REGISTRY_NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

    }
}
