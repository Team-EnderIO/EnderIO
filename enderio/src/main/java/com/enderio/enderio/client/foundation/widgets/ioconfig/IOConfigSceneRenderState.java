package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record IOConfigSceneRenderState(
    int x0,
    int x1,
    int y0,
    int y1,
    float scale,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds,
    Matrix4f viewMatrix,
    List<IOConfigSceneBlock> primaryBlocks,
    List<IOConfigSceneBlock> neighborBlocks,
    @Nullable Pair<BlockPos, Direction> selection
) implements PictureInPictureRenderState {

    public static IOConfigSceneRenderState create(ClientLevel level, int x, int y, int width, int height, @Nullable ScreenRectangle scissorArea,
        Matrix4f viewMatrix, List<BlockPos> primaryBlockPositions, List<BlockPos> neighborBlockPositions, boolean shouldRenderNeighbors,
        @Nullable Pair<BlockPos, Direction> selection) {

        List<IOConfigSceneBlock> primaryBlocks = new ArrayList<>();
        for (BlockPos pos : primaryBlockPositions) {
            primaryBlocks.add(IOConfigSceneBlock.create(level, pos));
        }

        List<IOConfigSceneBlock> neighborBlocks = new ArrayList<>();
        if (shouldRenderNeighbors) {
            for (BlockPos pos : neighborBlockPositions) {
                neighborBlocks.add(IOConfigSceneBlock.create(level, pos));
            }
        }

        return new IOConfigSceneRenderState(x, x + width, y, y + height, 1, scissorArea,
            PictureInPictureRenderState.getBounds(x, y, x + width, y + height, scissorArea), new Matrix4f(viewMatrix), primaryBlocks, neighborBlocks, selection);
    }

}
