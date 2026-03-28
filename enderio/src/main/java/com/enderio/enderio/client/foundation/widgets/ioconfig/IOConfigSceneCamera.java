package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class IOConfigSceneCamera {
    private static final Quaternionf ROT_180_Z = Axis.ZP.rotation((float) Math.PI);

    private Vec3 sceneOrigin;

    private float scale = 20;
    private float pitch;
    private float yaw;

    private Quaternionf blockTransform;
    private Matrix4f viewMatrix;
    private boolean isDirty = true;

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

    public Matrix4f viewMatrix() {
        if (isDirty) {
            recompute();
        }

        return viewMatrix;
    }

    public Vec3 getEyePosition() {
        Matrix4f invView = new Matrix4f(viewMatrix()).invert();

        // Transform origin (0,0,0,1) into world space
        Vector4f origin = new Vector4f(0, 0, 0, 1);
        origin.mul(invView);

        return new Vec3(origin.x, origin.y, origin.z);
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

        // Create view matrix
        viewMatrix = new Matrix4f();
        viewMatrix.scale(scale, scale, scale);
        viewMatrix.rotate(blockTransform);
        viewMatrix.translate((float)-sceneOrigin.x, (float)-sceneOrigin.y, (float)-sceneOrigin.z);

        isDirty = false;
    }
}
