package com.enderio.enderio.client.content.conduits.model.bundle.port;

import com.enderio.enderio.api.EnderIODataComponents;
import com.enderio.enderio.client.content.conduits.model.ConduitAdditionalModels;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static net.minecraft.client.renderer.item.CuboidItemModelWrapper.computeExtents;

public class ConduitItemModel implements ItemModel {

    private final BakingContext context;

    public ConduitItemModel(BakingContext context) {
        this.context = context;
    }

    @Override
    public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver,
        ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner itemOwner, int seed) {

        if (!stack.has(EnderIODataComponents.CONDUIT)) {
            return;
        }

        var conduit = stack.get(EnderIODataComponents.CONDUIT);

        var resolvedmodel = context.blockModelBaker().getModel(ConduitAdditionalModels.CONDUIT_ITEM);
        TextureSlots textureslots = resolvedmodel.getTopTextureSlots();
        var baker = new ConduitBaker(context.blockModelBaker(), new Material(conduit.value().texture()));
        List<BakedQuad> list = resolvedmodel.getTopGeometry().bake(textureslots, baker, BlockModelRotation.IDENTITY, () -> "conduit_item").getAll();
        ModelRenderProperties modelrenderproperties = ModelRenderProperties.fromResolvedModel(baker, resolvedmodel, textureslots);

        var extents = Suppliers.memoize(() -> computeExtents(list));

        renderState.appendModelIdentityElement(this);
        renderState.appendModelIdentityElement(conduit);
        var state = renderState.newLayer();
        state.setExtents(extents);
        modelrenderproperties.applyToLayer(state, displayContext);
        state.prepareQuadList().addAll(list);
    }

    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return CODEC;
        }

        @Override
        public ItemModel bake(BakingContext bakingContext, Matrix4fc matrix4fc) {
            return new ConduitItemModel(bakingContext);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_ITEM);
        }
    }
}
