package com.enderio.base.data.model.builder;

import com.enderio.base.EnderIO;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DummyCustomModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    public static <T extends ModelBuilder<T>> DummyCustomModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new DummyCustomModelBuilder<>(parent, existingFileHelper);
    }

    protected DummyCustomModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(EnderIO.loc("dummy"), parent, existingFileHelper);
    }
}
