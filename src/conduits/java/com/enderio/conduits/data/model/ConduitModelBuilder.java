package com.enderio.conduits.data.model;

import com.enderio.EnderIO;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ConduitModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    public static <T extends ModelBuilder<T>> ConduitModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new ConduitModelBuilder<>(parent, existingFileHelper);
    }

    protected ConduitModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(EnderIO.loc("conduit"), parent, existingFileHelper);
    }
}
