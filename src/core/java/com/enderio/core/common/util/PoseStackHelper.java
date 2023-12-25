package com.enderio.core.common.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Vector3f;

//Helper class for PoseStack (Matrix) manipulation
public class PoseStackHelper {

    /**
     * Rotates the given poseStack in a pivot, around an axis for the given degrees.
     *
     * @param poseStack The PoseStack needed to rotate
     * @param pivot     The Point used as center for the rotation
     * @param axis      The axis around which is rotated
     * @param angle     The angle which it is rotated
     * @param degrees   If the angle is given in degrees
     */
    public static void rotateAroundPivot(PoseStack poseStack, Vector3f pivot, Axis axis, float angle, boolean degrees) {
        poseStack.translate(pivot.x(), pivot.y(), pivot.z());
        if (degrees) {
            angle = (float) Math.toRadians(angle);
        }

        poseStack.mulPose(axis.rotation(angle));
        poseStack.translate(-pivot.x(), -pivot.y(), -pivot.z());
    }
}
