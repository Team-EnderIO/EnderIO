package com.enderio.base.data.tags;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EIOEntityTagsProvider extends EntityTypeTagsProvider {
    public EIOEntityTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, provider, EnderIO.NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(EIOTags.EntityTypes.SOUL_VIAL_BLACKLIST)
                .add(EntityType.WARDEN)
                .addTag(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED);

        this.tag(EIOTags.EntityTypes.SOUL_VIAL_WHITELIST);
    }
}
