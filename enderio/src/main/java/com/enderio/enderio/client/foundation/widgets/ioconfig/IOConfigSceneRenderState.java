package com.enderio.enderio.client.foundation.widgets.ioconfig;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record IOConfigSceneRenderState(
    int x0, // The left X
    int x1, // The right X
    int y0, // The top Y
    int y1, // The bottom Y
    float scale, // The scale factor when drawing to the picture
    @Nullable ScreenRectangle scissorArea, // The rendering area
    @Nullable ScreenRectangle bounds, // The bounds of the element
    Matrix4f viewMatrix,
    List<IOConfigSceneBlock> primaryBlocks,
    List<IOConfigSceneBlock> neighborBlocks,
    boolean shouldRenderNeighbors
) implements PictureInPictureRenderState {

    public static IOConfigSceneRenderState create(ClientLevel level, int x, int y, int width, int height, @Nullable ScreenRectangle scissorArea,
        Matrix4f viewMatrix, List<BlockPos> primaryBlockPositions, List<BlockPos> neighborBlockPositions, boolean shouldRenderNeighbors) {

        if (primaryBlockPositions.isEmpty()) {
            throw new IllegalArgumentException("You must provide at least one primary block to the renderer.");
        }

        List<IOConfigSceneBlock> primaryBlocks = new ArrayList<>();
        for (BlockPos pos : primaryBlockPositions) {
            primaryBlocks.add(IOConfigSceneBlock.create(level, pos));
        }

        List<IOConfigSceneBlock> neighborBlocks = new ArrayList<>();
        for (BlockPos pos : neighborBlockPositions) {
            neighborBlocks.add(IOConfigSceneBlock.create(level, pos));
        }

        return new IOConfigSceneRenderState(x, x + width, y, y + height, 1, scissorArea,
            PictureInPictureRenderState.getBounds(x, y, x + width, y + height, scissorArea), viewMatrix, primaryBlocks, neighborBlocks, shouldRenderNeighbors);
    }

}
