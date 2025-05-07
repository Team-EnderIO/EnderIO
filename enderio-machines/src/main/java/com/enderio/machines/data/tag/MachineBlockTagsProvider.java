package com.enderio.machines.data.tag;

import com.enderio.base.api.EnderIO;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class MachineBlockTagsProvider extends BlockTagsProvider {

    public MachineBlockTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider,
            net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        super(packOutput, provider, EnderIO.NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

    }
}
