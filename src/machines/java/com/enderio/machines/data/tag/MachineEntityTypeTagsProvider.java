package com.enderio.machines.data.tag;

import com.enderio.EnderIO;
import com.enderio.machines.common.tag.MachineTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class MachineEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public MachineEntityTypeTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(MachineTags.EntityTypes.SPAWNER_BLACKLIST).addTag(Tags.EntityTypes.BOSSES);
    }
}
