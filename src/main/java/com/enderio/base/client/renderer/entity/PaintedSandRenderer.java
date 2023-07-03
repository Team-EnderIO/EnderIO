package com.enderio.base.client.renderer.entity;

import com.enderio.base.common.entity.PaintedSandEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class PaintedSandRenderer extends FallingBlockRenderer {
    public PaintedSandRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    //copied with hate
    @Override
    public void render(FallingBlockEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        Block block = pEntity instanceof PaintedSandEntity entity ? entity.getPaint() : null;
        BlockState blockstate = block != null ? block.defaultBlockState() : pEntity.getBlockState();
        if (blockstate.getRenderShape() == RenderShape.MODEL) {
            Level level = pEntity.level();
            if (blockstate != level.getBlockState(pEntity.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                pMatrixStack.pushPose();
                BlockPos blockpos = new BlockPos((int) pEntity.getX(), (int) pEntity.getBoundingBox().maxY, (int) pEntity.getZ());
                pMatrixStack.translate(-0.5D, 0.0D, -0.5D);
                BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();
                BakedModel model = blockrenderdispatcher.getBlockModel(blockstate);

                for (RenderType type : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(pEntity.blockPosition())), ModelData.EMPTY)) {
                    blockrenderdispatcher
                        .getModelRenderer()
                        .tesselateBlock(level, blockrenderdispatcher.getBlockModel(blockstate), blockstate, blockpos, pMatrixStack, pBuffer.getBuffer(type),
                            false, RandomSource.create(), blockstate.getSeed(pEntity.getStartPos()), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, type);
                }
                pMatrixStack.popPose();

                //Super.super.render
                net.minecraftforge.client.event.RenderNameTagEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameTagEvent(pEntity,
                    pEntity.getDisplayName(), this, pMatrixStack, pBuffer, pPackedLight, pPartialTicks);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
                if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (
                    renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(pEntity))) {
                    this.renderNameTag(pEntity, renderNameplateEvent.getContent(), pMatrixStack, pBuffer, pPackedLight);
                }
            }
        }
    }
}
