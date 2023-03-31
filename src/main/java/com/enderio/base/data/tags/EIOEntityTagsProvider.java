package com.enderio.base.data.tags;

import com.enderio.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EIOEntityTagsProvider extends EntityTypeTagsProvider {
    public EIOEntityTagsProvider(DataGenerator p_126517_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126517_, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(EIOTags.Entitytypes.SOUL_VIAL_BLACKLIST).addTag(Tags.EntityTypes.BOSSES);
    }
}
