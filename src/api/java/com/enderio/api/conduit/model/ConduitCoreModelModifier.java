package com.enderio.api.conduit.model;

import com.enderio.api.conduit.ConduitData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ConduitCoreModelModifier<T extends ConduitData<T>> {
    /**
     * Create additional quads to be rendered at the point of conduit connection.
     */
    default List<BakedQuad> createConnectionQuads(T data, @Nullable Direction facing, Direction connectionDirection, RandomSource rand,
        @Nullable RenderType type) {
        return List.of();
    }

    default List<ModelResourceLocation> getModelDependencies() {
        return List.of();
    }
}
