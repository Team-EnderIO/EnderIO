package com.enderio.enderio.client.foundation.widgets.ioconfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public record IOConfigSceneBlock(
    BlockPos pos,
    BlockModelRenderState blockModelRenderState) {

    public static IOConfigSceneBlock create(BlockAndTintGetter level, BlockPos pos) {
        BlockModelRenderState modelRenderState = new BlockModelRenderState();

        var state = level.getBlockState(pos);
        // TODO: Unsure how we funnel ModelData through at this stage.
//        ModelData modelData = blockEntity != null ? blockEntity.getModelData() : ModelData.EMPTY;
        BlockDisplayContext context = BlockDisplayContext.create();
        Minecraft.getInstance().getBlockModelResolver().update(modelRenderState, state, context);

        return new IOConfigSceneBlock(pos, modelRenderState);
    }

}
