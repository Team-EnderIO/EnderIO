package com.enderio.machines.data.tag;

import com.enderio.EnderIO;
import com.enderio.machines.common.tag.MachineTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraftforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class MachineEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public MachineEntityTypeTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider, net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        super(packOutput, provider, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(MachineTags.EntityTypes.SPAWNER_BLACKLIST).addTag(Tags.EntityTypes.BOSSES);
    }
}
