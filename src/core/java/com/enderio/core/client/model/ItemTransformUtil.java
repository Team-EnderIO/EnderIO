package com.enderio.core.client.model;

import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

@Deprecated(forRemoval = true)
public class ItemTransformUtil {
    public static final ItemTransforms DEFAULT;

    static {
        ItemTransform tpLeft = getTransform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        ItemTransform tpRight = getTransform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        ItemTransform fpLeft = getTransform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        ItemTransform fpRight = getTransform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
        ItemTransform head = getTransform(ItemDisplayContext.HEAD);
        ItemTransform gui = getTransform(ItemDisplayContext.GUI);
        ItemTransform ground = getTransform(ItemDisplayContext.GROUND);
        ItemTransform fixed = getTransform(ItemDisplayContext.FIXED);
        DEFAULT = new ItemTransforms(tpLeft, tpRight, fpLeft, fpRight, head, gui, ground, fixed);
    }

    private static ItemTransform getTransform(ItemDisplayContext type) {
        switch (type) {
        case GUI:
            return new ItemTransform(new Vector3f(30, 225, 0), new Vector3f(), new Vector3f(0.625f, 0.625f, 0.625f));
        case GROUND:
            return new ItemTransform(new Vector3f(), new Vector3f(), new Vector3f(0.25f, 0.25f, 0.25f));
        case FIXED:
            return new ItemTransform(new Vector3f(), new Vector3f(), new Vector3f(0.5f, 0.5f, 0.5f));
        case THIRD_PERSON_RIGHT_HAND:
        case THIRD_PERSON_LEFT_HAND:
            return new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(), new Vector3f(0.375f, 0.375f, 0.375f));
        case FIRST_PERSON_RIGHT_HAND:
            return new ItemTransform(new Vector3f(0, 45, 0), new Vector3f(), new Vector3f(0.4f, 0.4f, 0.4f));
        case FIRST_PERSON_LEFT_HAND:
            return new ItemTransform(new Vector3f(0, 225, 0), new Vector3f(), new Vector3f(0.4f, 0.4f, 0.4f));
        case NONE:
        case HEAD:
        default:
            return ItemTransform.NO_TRANSFORM;
        }
    }
}
