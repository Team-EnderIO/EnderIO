package com.enderio.enderio.api.conduits.model;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;

//TODO Use ValueInputs?
public interface ConduitModelModifier {

    /**
     * Create additional quads to be rendered at the point of conduit connection.
     */
    default List<BlockModelPart> createConnectionQuads(ModelBaker baker, ModelState modelState, Holder<Conduit<?, ?>> conduit,
        @Nullable CompoundTag extraWorldData) {
        return List.of();
    }

    /**
     * Gets the conduit texture to display, given the data.
     * @param extraWorldData client data from {@link Conduit#getExtraWorldData(ConduitBundle, ConduitNode)}.
     */
    default Identifier getTexture(Holder<Conduit<?, ?>> conduit, @Nullable CompoundTag extraWorldData) {
        return conduit.value().texture();
    }

    default List<Identifier> getModelDependencies() {
        return List.of();
    }
}
