package com.enderio.conduits.client;

import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
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
            renderShape(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()),
                conduit.getShape().getShapeFromHit(event.getTarget().getBlockPos(), event.getTarget()), (double) pos.getX() - camPos.x, (double) pos.getY() - camPos.y,
                (double) pos.getZ() - camPos.z, 0.0F, 0.0F, 0.0F, 0.4F);
        }
    }

    //TODO AT
    private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green,
        float blue, float alpha) {
        PoseStack.Pose posestack$pose = poseStack.last();
        shape.forAllEdges((p_234280_, p_234281_, p_234282_, p_234283_, p_234284_, p_234285_) -> {
            float f = (float) (p_234283_ - p_234280_);
            float f1 = (float) (p_234284_ - p_234281_);
            float f2 = (float) (p_234285_ - p_234282_);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f /= f3;
            f1 /= f3;
            f2 /= f3;
            consumer
                .vertex(posestack$pose.pose(), (float) (p_234280_ + x), (float) (p_234281_ + y), (float) (p_234282_ + z))
                .color(red, green, blue, alpha)
                .normal(posestack$pose, f, f1, f2)
                .endVertex();
            consumer
                .vertex(posestack$pose.pose(), (float) (p_234283_ + x), (float) (p_234284_ + y), (float) (p_234285_ + z))
                .color(red, green, blue, alpha)
                .normal(posestack$pose, f, f1, f2)
                .endVertex();
        });
    }
}
