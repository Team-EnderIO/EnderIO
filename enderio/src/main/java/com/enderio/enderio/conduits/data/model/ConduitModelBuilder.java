package com.enderio.enderio.conduits.data.model;

import com.enderio.enderio.common.EnderIO;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ConduitModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    public static <T extends ModelBuilder<T>> ConduitModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new ConduitModelBuilder<>(parent, existingFileHelper);
    }

    protected ConduitModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(EnderIO.rl("conduit"), parent, existingFileHelper, false);
    }
}
