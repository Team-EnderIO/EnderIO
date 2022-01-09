package com.enderio.base.client.model;

import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;

public class ItemTransformUtil {
    public static final ItemTransforms DEFAULT;

    static {
        ItemTransform tpLeft = getTransform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
        ItemTransform tpRight = getTransform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        ItemTransform fpLeft = getTransform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
        ItemTransform fpRight = getTransform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
        ItemTransform head = getTransform(ItemTransforms.TransformType.HEAD);
        ItemTransform gui = getTransform(ItemTransforms.TransformType.GUI);
        ItemTransform ground = getTransform(ItemTransforms.TransformType.GROUND);
        ItemTransform fixed = getTransform(ItemTransforms.TransformType.FIXED);
        DEFAULT = new ItemTransforms(tpLeft, tpRight, fpLeft, fpRight, head, gui, ground, fixed);
    }

    private static ItemTransform getTransform(ItemTransforms.TransformType type) {
        switch (type) {
        case GUI:
            return new ItemTransform(new Vector3f(30, 225, 0), Vector3f.ZERO, new Vector3f(0.625f, 0.625f, 0.625f));
        case GROUND:
            return new ItemTransform(Vector3f.ZERO, Vector3f.ZERO, new Vector3f(0.25f, 0.25f, 0.25f));
        case FIXED:
            return new ItemTransform(Vector3f.ZERO, Vector3f.ZERO, new Vector3f(0.5f, 0.5f, 0.5f));
        case THIRD_PERSON_RIGHT_HAND:
        case THIRD_PERSON_LEFT_HAND:
            return new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.375f, 0.375f, 0.375f));
        case FIRST_PERSON_RIGHT_HAND:
            return new ItemTransform(new Vector3f(0, 45, 0), Vector3f.ZERO, new Vector3f(0.4f, 0.4f, 0.4f));
        case FIRST_PERSON_LEFT_HAND:
            return new ItemTransform(new Vector3f(0, 225, 0), Vector3f.ZERO, new Vector3f(0.4f, 0.4f, 0.4f));
        case NONE:
        case HEAD:
        default:
            return ItemTransform.NO_TRANSFORM;
        }
    }
}
