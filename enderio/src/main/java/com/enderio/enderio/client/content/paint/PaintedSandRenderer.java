package com.enderio.enderio.client.content.paint;

import com.enderio.enderio.content.paint.PaintedSandEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;

public class PaintedSandRenderer extends FallingBlockRenderer {
    public PaintedSandRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    // Thanks to the new render state stuff, we can reuse the vanilla FallingBlockRenderer almost entirely!
    @Override
    public void extractRenderState(FallingBlockEntity entity, FallingBlockRenderState renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);

        if (entity instanceof PaintedSandEntity paintedSandEntity) {
            Block paintBlock = paintedSandEntity.getPaint();
            if (paintBlock != null) {
                renderState.blockState = paintBlock.defaultBlockState();
            }
        }
    }
}
