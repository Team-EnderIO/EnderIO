package com.enderio.base.common.item.darksteel.upgrades.explosive;

import com.enderio.base.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ExplosiveTagsProvider extends BlockTagsProvider {

    public ExplosiveTagsProvider(DataGenerator dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(EIOTags.Blocks.DARK_STEEL_EXPLODABLE_WHITELIST);
        tag(EIOTags.Blocks.DARK_STEEL_EXPLODABLE_BLACKLIST);
    }

}
