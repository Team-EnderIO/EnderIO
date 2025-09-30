package com.enderio.enderio.machines.data.tag;

import com.enderio.enderio.api.EnderIO;
import com.enderio.enderio.machines.common.tag.MachineTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;

public class MachineEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public MachineEntityTypeTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider,
            net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        super(packOutput, provider, EnderIO.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(MachineTags.EntityTypes.SPAWNER_BLACKLIST).addTag(Tags.EntityTypes.BOSSES).add(EntityType.WARDEN);
    }
}
