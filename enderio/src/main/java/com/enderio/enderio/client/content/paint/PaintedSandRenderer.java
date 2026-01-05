package com.enderio.enderio.client.content.paint;

import com.enderio.enderio.content.paint.PaintedSandEntity;
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
    public void render(FallingBlockEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        Block paintBlock = entity instanceof PaintedSandEntity paintedSandEntity ? paintedSandEntity.getPaint() : null;

        // See FallingBlockRenderer#render for reference.
        BlockState renderBlockState = paintBlock != null ? paintBlock.defaultBlockState() : entity.getBlockState();
        if (renderBlockState.getRenderShape() == RenderShape.MODEL) {
            Level level = entity.level();

            if (renderBlockState != level.getBlockState(entity.blockPosition()) && renderBlockState.getRenderShape() != RenderShape.INVISIBLE) {
                matrixStack.pushPose();
                BlockPos blockpos = new BlockPos((int) entity.getX(), (int) entity.getBoundingBox().maxY, (int) entity.getZ());
                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();
                BakedModel model = blockrenderdispatcher.getBlockModel(renderBlockState);

                for (RenderType type : model.getRenderTypes(renderBlockState, RandomSource.create(renderBlockState.getSeed(entity.blockPosition())), ModelData.EMPTY)) {
                    blockrenderdispatcher
                        .getModelRenderer()
                        .tesselateBlock(level, blockrenderdispatcher.getBlockModel(renderBlockState), renderBlockState, blockpos, matrixStack, buffer.getBuffer(type),
                            false, RandomSource.create(), renderBlockState.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, type);
                }

                matrixStack.popPose();
                super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FallingBlockEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
