package com.enderio.enderio.client.content.machines;

import com.enderio.enderio.client.foundation.widgets.ioconfig.IOConfigBlockDisplayContext;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.joml.Matrix4fc;

import java.util.List;
import java.util.Optional;

public class IOOverlayBlockModel implements BlockModel {
    private final BlockStateModel model;
    private final Matrix4fc transformation;

    public IOOverlayBlockModel(BlockStateModel model, Matrix4fc transformation)
    {
        this.model = model;
        this.transformation = transformation;
    }

    @Override
    public void update(BlockModelRenderState output, BlockState state, BlockDisplayContext context, long seed) {
        BlockAndTintGetter level;
        BlockPos pos;
        if (context instanceof IOConfigBlockDisplayContext ctx)
        {
            level = ctx.realLevel();
            pos = ctx.pos();
        }
        else
        {
            level = BlockAndTintGetter.EMPTY;
            pos = BlockPos.ZERO;
        }
        int materialFlags = model.materialFlags(level, pos, state);
        List<BlockStateModelPart> partList = output.setupModel(transformation, (materialFlags & BakedQuad.FLAG_TRANSLUCENT) != 0);
        model.collectParts(level, pos, state, output.scratchRandomSource(seed), partList);
        IClientBlockExtensions.of(state).collectDynamicTintValues(state, level, pos, output.tintLayers());
    }

    public record Unbaked(BlockState state, Optional<Transformation> transformation) implements BlockModel.Unbaked {
        @Override
        public BlockModel bake(BakingContext context, Matrix4fc transformation)
        {
            Matrix4fc modelTransform = Transformation.compose(transformation, this.transformation);
            return new IOOverlayBlockModel(context.modelGetter().apply(state), modelTransform);
        }
    }
}
