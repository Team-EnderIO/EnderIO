package com.enderio.enderio.client.content.conduits;

import com.enderio.enderio.client.content.conduits.model.facades.ClientFacadeVisibility;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
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

            BlockHitResult result = event.getHitResult();
            BlockPos pos = result.getBlockPos();
            VoxelShape shape = conduit.getShape().getShapeFromHit(pos, result);
            Vec3 offset = Vec3.atLowerCornerOf(result.getBlockPos()).subtract(event.getCamera().position());

            boolean translucent = event.isInTranslucentPass();
            float lineWidth = minecraft.getWindow().getAppropriateLineWidth();
            event.addCustomRenderer((renderState, collector, poseStack, levelRenderState) -> {
                if (translucent == renderState.isTranslucent()) {
                    boolean highContrast = renderState.highContrast();

                    poseStack.pushPose();
                    poseStack.translate(offset.x, offset.y, offset.z);

                    if (highContrast) {
                        collector.submitShapeOutline(poseStack, shape, RenderTypes.secondaryBlockOutline(), 0xFF000000, 7F, translucent);
                    }

                    int lineColor = highContrast ? CommonColors.HIGH_CONTRAST_DIAMOND : DEFAULT_LINE_COLOR;
                    collector.submitShapeOutline(poseStack, shape, RenderTypes.lines(), lineColor, lineWidth, translucent);
                    poseStack.popPose();
                }
                return true;
            });
        }
    }
}
