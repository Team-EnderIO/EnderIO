package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class IOConfigSceneCamera {
    private static final Quaternionf ROT_180_Z = Axis.ZP.rotation((float) Math.PI);

    // As we're using real block positions as our point of reference, we'll need to translate the middle block to 0,0,0.
    // TODO: I think holding ctrl and dragging with the mouse should shift the origin.
    private Vec3 sceneOrigin;

    // Zoom and pivot
    private float scale = 20;
    private float pitch;
    private float yaw;

    private Quaternionf blockTransform;
    private Matrix4f rayTransform;
    private IOConfigSceneCameraState state;
    private boolean isDirty = true;

    public IOConfigSceneCamera(BlockPos centerBlock) {
        sceneOrigin = new Vec3(
            centerBlock.getX() + 0.5f,
            centerBlock.getY() + 0.5f,
            centerBlock.getZ() + 0.5f);
    }

    public IOConfigSceneCamera(Vector3f sceneOrigin, float scale, float pitch, float yaw) {
        this.sceneOrigin = new Vec3(sceneOrigin.x, sceneOrigin.y, sceneOrigin.z);
        this.scale = scale;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Vec3 sceneOrigin() {
        return sceneOrigin;
    }

    public float scale() {
        return scale;
    }

    public float pitch() {
        return pitch;
    }

    public float yaw() {
        return yaw;
    }

    public void setSceneOrigin(Vec3 sceneOrigin) {
        this.sceneOrigin = sceneOrigin;
        isDirty = true;
    }

    public void setScale(float scale) {
        this.scale = scale;
        isDirty = true;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        isDirty = true;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        isDirty = true;
    }

    public Quaternionf blockTransform() {
        if (isDirty) {
            recompute();
        }

        return blockTransform;
    }

    public Matrix4f rayTransform() {
        if (isDirty) {
            recompute();
        }

        return rayTransform;
    }

    public IOConfigSceneCameraState state() {
        if (isDirty) {
            recompute();
        }

        return state;
    }

    private void recompute() {
        // Compute rotation
        Quaternionf rotPitch = Axis.XN.rotationDegrees(pitch);
        Quaternionf rotYaw = Axis.YP.rotationDegrees(yaw);

        // Build block transformation matrix
        // Rotate 180 around Z, otherwise the block is upside down
        blockTransform = new Quaternionf(ROT_180_Z);
        // Rotate around X (pitch) in negative direction
        blockTransform.mul(rotPitch);
        // Rotate around Y (yaw)
        blockTransform.mul(rotYaw);

        // Build ray transformation matrix
        // Rotate 180 around Z, otherwise the block is upside down
        rayTransform = new Matrix4f();
        rayTransform.set(ROT_180_Z);
        // Rotate around Y (yaw)
        rayTransform.rotate(rotYaw);
        // Rotate around X (pitch) in negative direction
        rayTransform.rotate(rotPitch);

        // Create state
        state = new IOConfigSceneCameraState(sceneOrigin, scale, blockTransform);
        isDirty = false;
    }
}
