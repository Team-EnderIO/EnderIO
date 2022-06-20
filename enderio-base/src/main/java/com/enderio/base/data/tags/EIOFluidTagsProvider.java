package com.enderio.base.data.tags;

import com.enderio.base.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EIOFluidTagsProvider extends FluidTagsProvider {

    public EIOFluidTagsProvider(DataGenerator p_126523_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126523_, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(EIOTags.Fluids.COLD_FIRE_IGNITER_FUEL).add(EIOFluids.VAPOR_OF_LEVITY.get().getSource());
        tag(EIOTags.Fluids.STAFF_OF_LEVITY_FUEL).add(EIOFluids.VAPOR_OF_LEVITY.get().getSource());
    }
}
