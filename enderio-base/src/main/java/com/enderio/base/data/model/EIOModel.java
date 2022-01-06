package com.enderio.base.data.model;

import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;

import java.util.function.Consumer;

public class EIOModel {
    public static <T extends ModelProvider<B>, B extends ModelBuilder<B>> ModelFile compositeModel(T prov, String name, Consumer<CompositeModelBuilder<B>> compositeBuilder) {
        var builder = prov.getBuilder(name)
            .customLoader(CompositeModelBuilder::begin);
        compositeBuilder.accept(builder);
        return builder.end();
    }
}
