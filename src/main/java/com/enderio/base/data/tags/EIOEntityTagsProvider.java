package com.enderio.base.data.tags;

import com.enderio.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EIOEntityTagsProvider extends EntityTypeTagsProvider {
    public EIOEntityTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, provider, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(EIOTags.EntityTypes.SOUL_VIAL_BLACKLIST);
    }
}
