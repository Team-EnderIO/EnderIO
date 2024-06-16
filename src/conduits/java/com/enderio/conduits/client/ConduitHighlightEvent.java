package com.enderio.conduits.client;

import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ConduitHighlightEvent {

    @SubscribeEvent
    public static void highlight(RenderHighlightEvent.Block event) {
        if (Minecraft.getInstance().level.getBlockEntity(event.getTarget().getBlockPos()) instanceof ConduitBlockEntity conduit) {
            event.setCanceled(true);
            BlockPos pos = event.getTarget().getBlockPos();
            Vec3 camPos = event.getCamera().getPosition();
            LevelRenderer.renderShape(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()),
                conduit.getShape().getShapeFromHit(event.getTarget().getBlockPos(), event.getTarget()), (double) pos.getX() - camPos.x, (double) pos.getY() - camPos.y,
                (double) pos.getZ() - camPos.z, 0.0F, 0.0F, 0.0F, 0.4F);
        }
    }
}
