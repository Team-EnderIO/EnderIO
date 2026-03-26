package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class EIOEntityTagsProvider extends EntityTypeTagsProvider {
    public EIOEntityTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider, EnderIOAPI.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EIOTags.EntityTypes.SOUL_VIAL_DENY_LIST)
                .add(EntityType.WARDEN)
                .addTag(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED)
                .addTag(Tags.EntityTypes.BOSSES);

        this.tag(EIOTags.EntityTypes.SOUL_VIAL_ALLOY_LIST);

        this.tag(EIOTags.EntityTypes.SPAWNER_DENY_LIST).addTag(Tags.EntityTypes.BOSSES).add(EntityType.WARDEN);

        this.tag(EIOTags.EntityTypes.SPAWNER_ALLOW_LIST);

    }
}
