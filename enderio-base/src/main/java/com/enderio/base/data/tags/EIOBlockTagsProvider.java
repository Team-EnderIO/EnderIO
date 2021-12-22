package com.enderio.base.data.tags;

import com.enderio.base.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class EIOBlockTagsProvider extends BlockTagsProvider {

    public EIOBlockTagsProvider(DataGenerator dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(EIOTags.Blocks.DARK_STEEL_EXPLODABLE_ALLOW_LIST);
        tag(EIOTags.Blocks.DARK_STEEL_EXPLODABLE_DENY_LIST);
    }

}
