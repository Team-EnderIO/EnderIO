package com.enderio.enderio.client.content.conduits;

import com.enderio.enderio.client.content.conduits.model.facades.ClientFacadeVisibility;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ConduitHighlightEvent {

    private static final int DEFAULT_LINE_COLOR = ARGB.color(0x66, 0xFF000000);

    @SubscribeEvent
    public static void highlight(ExtractBlockOutlineRenderStateEvent event) {
        var minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        if (minecraft.level
                .getBlockEntity(event.getBlockPos()) instanceof ConduitBundleBlockEntity conduit) {
            // Use standard block highlights for facades.
            if (conduit.hasFacade() && ClientFacadeVisibility.areFacadesVisible()) {
                return;
            }

            // If the conduit is bugged, don't do this.
            if (conduit.isEmpty()) {
                return;
            }

            // 26.2-port: CustomBlockOutlineRenderer lambda signature changed:
            //   26.1: (BlockOutlineRenderState, MultiBufferSource.BufferSource, PoseStack, boolean, LevelRenderState)
            //   26.2: (BlockOutlineRenderState, SubmitNodeCollector, PoseStack, LevelRenderState)
            //   Additionally, ShapeRenderer and MultiBufferSource.BufferSource were removed in 26.2.
            //   The custom outline renderer is disabled until the new render pipeline is implemented.
            BlockHitResult result = event.getHitResult();
            BlockPos pos = result.getBlockPos();
            VoxelShape shape = conduit.getShape().getShapeFromHit(pos, result);
            Vec3 offset = Vec3.atLowerCornerOf(result.getBlockPos()).subtract(event.getCamera().position());

            // event.addCustomRenderer((renderState, submitNodeCollector, poseStack, levelRenderState) -> {
            //     if (event.isInTranslucentPass() == renderState.isTranslucent()) {
            //         boolean highContrast = renderState.highContrast();
            //         // ShapeRenderer.renderShape and the buffer-based approach are gone in 26.2;
            //         // outline rendering now uses SubmitNodeCollector + new FeatureRenderDispatcher.
            //     }
            //     return true;
            // });
        }
    }
}
