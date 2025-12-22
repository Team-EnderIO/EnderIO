package com.enderio.enderio.client.content.conduits;

import com.enderio.enderio.client.content.conduits.model.facades.ClientFacadeVisibility;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ConduitHighlightEvent {

    @SubscribeEvent
    public static void highlight(RenderHighlightEvent.Block event) {
        var minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        if (minecraft.level
                .getBlockEntity(event.getTarget().getBlockPos()) instanceof ConduitBundleBlockEntity conduit) {
            // Use standard block highlights for facades.
            if (conduit.hasFacade() && ClientFacadeVisibility.areFacadesVisible()) {
                return;
            }

            // If the conduit is bugged, don't do this.
            if (conduit.isEmpty()) {
                return;
            }

            event.setCanceled(true);
            BlockPos pos = event.getTarget().getBlockPos();
            Vec3 camPos = event.getCamera().getPosition();
            ShapeRenderer.renderShape(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()),
                    conduit.getShape().getShapeFromHit(event.getTarget().getBlockPos(), event.getTarget()),
                    (double) pos.getX() - camPos.x, (double) pos.getY() - camPos.y, (double) pos.getZ() - camPos.z,
                ARGB.color(102, -16777216));
        }
    }
}
