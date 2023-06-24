package com.enderio.conduits.data.model;

import com.enderio.EnderIO;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ConduitModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    public static <T extends ModelBuilder<T>> ConduitModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new ConduitModelBuilder<>(parent, existingFileHelper);
    }

    protected ConduitModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(EnderIO.loc("conduit"), parent, existingFileHelper);
    }
}
