package com.enderio.enderio.client.content.conduits;

import com.enderio.enderio.client.content.conduits.model.facades.ClientFacadeVisibility;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
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
                .getBlockEntity(event.getHitResult().getBlockPos()) instanceof ConduitBundleBlockEntity conduit) {
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
            Vec3 camPos = event.getCamera().position();

            VoxelShape shape = conduit.getShape().getShapeFromHit(pos, result);

            // TODO: 1.21.11: Check this is equivalent to (double) pos.getX() - camPos.x, (double) pos.getY() - camPos.y, (double) pos.getZ() - camPos.z,
            Vec3 offset = Vec3.atLowerCornerOf(result.getBlockPos()).subtract(event.getCamera().position());

            event.addCustomRenderer((renderState, buffer, poseStack, translucentPass, levelRenderState) -> {
                if (translucentPass == renderState.isTranslucent()) {
                    boolean highContrast = renderState.highContrast();
                    if (highContrast)
                    {
                        VertexConsumer builder = buffer.getBuffer(RenderTypes.secondaryBlockOutline());
                        ShapeRenderer.renderShape(poseStack, builder, shape, offset.x, offset.y, offset.z, 0xFF000000, 7F);
                    }

                    VertexConsumer builder = buffer.getBuffer(RenderTypes.lines());
                    int lineColor = highContrast ? CommonColors.HIGH_CONTRAST_DIAMOND : DEFAULT_LINE_COLOR;
                    ShapeRenderer.renderShape(poseStack, builder, shape, offset.x, offset.y, offset.z, lineColor, Minecraft.getInstance().getWindow().getAppropriateLineWidth());
                }
                return true;
            });
        }
    }
}
