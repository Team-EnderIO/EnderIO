package com.enderio.enderio.client.content.conduits.model.facades.port;

import com.enderio.enderio.init.EIODataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class FacadeItemModel implements ItemModel {

    private final BakingContext context;

    public FacadeItemModel(BakingContext context) {
        this.context = context;
    }

    @Override
    public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext,
        @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {

        if (!stack.has(EIODataComponents.BLOCK_PAINT)) {
            return;
        }

        var paintData = stack.get(EIODataComponents.BLOCK_PAINT);
        renderState.appendModelIdentityElement(this);
        renderState.appendModelIdentityElement(paintData);
        itemModelResolver.appendItemLayers(renderState, new ItemStack(paintData.paint().asItem()), displayContext, level, owner, seed);
    }

    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return CODEC;
        }

        @Override
        public ItemModel bake(BakingContext bakingContext, Matrix4fc matrix4fc) {
            return new FacadeItemModel(bakingContext);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
//            resolver.markDependency(ConduitAdditionalModels.CONDUIT_FACADE_OVERLAY);
//            resolver.markDependency(ConduitAdditionalModels.CONDUIT_FACADE);
        }
    }
}
