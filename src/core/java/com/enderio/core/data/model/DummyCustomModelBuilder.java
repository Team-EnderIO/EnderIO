package com.enderio.core.data.model;

import com.enderio.core.EnderCore;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DummyCustomModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    public static <T extends ModelBuilder<T>> DummyCustomModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new DummyCustomModelBuilder<>(parent, existingFileHelper);
    }

    protected DummyCustomModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(EnderCore.loc("dummy"), parent, existingFileHelper, false);
    }
}
