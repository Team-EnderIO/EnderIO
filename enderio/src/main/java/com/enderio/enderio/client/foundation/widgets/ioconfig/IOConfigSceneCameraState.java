package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record IOConfigSceneCameraState(
    Vec3 sceneOrigin,
    float scale,
    Quaternionf blockTransform
) {
    public void apply(PoseStack poseStack) {
//        poseStack.translate(sceneOrigin);
        poseStack.scale(scale, scale, -scale);
        poseStack.mulPose(blockTransform);
    }
}
