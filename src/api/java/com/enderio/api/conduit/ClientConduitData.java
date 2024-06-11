package com.enderio.api.conduit;

import com.enderio.api.misc.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ClientConduitData<T extends ConduitData<T>> {

    Vector2i ICON_TEXTURE_SIZE = new Vector2i(24, 24);
    Vector2i ICON_RENDER_SIZE = new Vector2i(12, 12);

    @FunctionalInterface
    interface UpdateExtendedData<T extends ConduitData<T>> {
        void update(Function<T, T> mapper);
    }

    /**
     * @param conduitDataSupplier       the conduit data the widgets are for, manipulate the state of it in the widgets
     * @param updateConduitData         call this to modify the conduit data.
     * @param direction                 the supplier to get the current direction for this extendedconduitdata
     * @param widgetsStart              the position on which widgets start
     * @return Widgets that manipulate the extended ConduitData, these changes are synced back to the server
     */
    default List<AbstractWidget> createWidgets(Screen screen, Supplier<T> conduitDataSupplier, UpdateExtendedData<T> updateConduitData,
        Supplier<Direction> direction, Vector2i widgetsStart) {
        return List.of();
    }

    /**
     * @param extendedConduitData your data
     * @param facing              the quads facing that is queried
     * @param connectionDirection direction you connecto to
     * @param rand                random
     * @param type                RenderType to be used
     * @return List of quads to be rendered. Those will be rotated in place
     */
    default List<BakedQuad> createConnectionQuads(T extendedConduitData, @Nullable Direction facing, Direction connectionDirection, RandomSource rand,
        @Nullable RenderType type) {
        return List.of();
    }

    default BakedModel getModel(ResourceLocation model) {
        return Minecraft.getInstance().getModelManager().getModel(model);
    }

    /**
     * @return a list that contains all models that should be baked
     */
    default List<ResourceLocation> modelsToLoad() {
        return List.of();
    }

    class Simple<T extends ConduitData<T>> implements ClientConduitData<T> {
        public Simple() {
        }
    }
}
