package com.enderio.base.client.paint;

import com.enderio.base.common.paint.PaintedSandEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class PaintedSandRenderer extends EntityRenderer<FallingBlockEntity> {
    public PaintedSandRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(FallingBlockEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        Block paintBlock = pEntity instanceof PaintedSandEntity entity ? entity.getPaint() : null;

        // See FallingBlockRenderer#render for reference.
        BlockState renderBlockState = paintBlock != null ? paintBlock.defaultBlockState() : pEntity.getBlockState();
        if (renderBlockState.getRenderShape() == RenderShape.MODEL) {
            Level level = pEntity.level();

            if (renderBlockState != level.getBlockState(pEntity.blockPosition()) && renderBlockState.getRenderShape() != RenderShape.INVISIBLE) {
                pMatrixStack.pushPose();
                BlockPos blockpos = new BlockPos((int) pEntity.getX(), (int) pEntity.getBoundingBox().maxY, (int) pEntity.getZ());
                pMatrixStack.translate(-0.5D, 0.0D, -0.5D);
                BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();
                BakedModel model = blockrenderdispatcher.getBlockModel(renderBlockState);

                for (RenderType type : model.getRenderTypes(renderBlockState, RandomSource.create(renderBlockState.getSeed(pEntity.blockPosition())), ModelData.EMPTY)) {
                    blockrenderdispatcher
                        .getModelRenderer()
                        .tesselateBlock(level, blockrenderdispatcher.getBlockModel(renderBlockState), renderBlockState, blockpos, pMatrixStack, pBuffer.getBuffer(type),
                            false, RandomSource.create(), renderBlockState.getSeed(pEntity.getStartPos()), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, type);
                }

                pMatrixStack.popPose();
                super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FallingBlockEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
