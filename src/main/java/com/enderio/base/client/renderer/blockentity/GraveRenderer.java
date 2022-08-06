package com.enderio.base.client.renderer.blockentity;

import com.enderio.base.common.blockentity.GraveBlockEntity;
import com.enderio.base.common.init.EIOCapabilities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.concurrent.atomic.AtomicReference;

//renders grave as a playerskull
public class GraveRenderer implements BlockEntityRenderer<GraveBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public GraveRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(GraveBlockEntity grave, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int light, int pCombinedOverlay) {
        Direction direction = null;//TODO if we make the grave rotatable
        SkullModelBase skullmodelbase = new SkullModel(this.context.bakeLayer(ModelLayers.PLAYER_HEAD));
        AtomicReference<RenderType> rendertype = new AtomicReference<>(RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin()));
        grave.getCapability(EIOCapabilities.OWNER).ifPresent(cap -> {
            if (cap.getProfile() != null) {
                rendertype.set(SkullBlockRenderer.getRenderType(SkullBlock.Types.PLAYER, cap.getProfile()));
            }
        });
        poseStack.pushPose();
        poseStack.translate(1, 1, 0);
        poseStack.popPose();
        SkullBlockRenderer.renderSkull(direction, 0.0F, 0.0F, poseStack, pBuffer, light, skullmodelbase, rendertype.get());
    }

}
