package com.enderio.machines.data.tag;

import com.enderio.EnderIOBase;
import com.enderio.machines.common.tag.MachineTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class MachineEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public MachineEntityTypeTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider, net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        super(packOutput, provider, EnderIOBase.REGISTRY_NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(MachineTags.EntityTypes.SPAWNER_BLACKLIST).addTag(Tags.EntityTypes.BOSSES).add(EntityType.WARDEN);
    }
}
