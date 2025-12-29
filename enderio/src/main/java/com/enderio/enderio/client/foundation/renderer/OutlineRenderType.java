package com.enderio.enderio.client.foundation.renderer;

import com.enderio.enderio.EnderIO;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

import java.util.HashMap;
import java.util.Map;

public class OutlineRenderType {

    private static final Map<RenderType, RenderType> TYPES = new HashMap<>();

    public static RenderType get(RenderType parent) {
        if (parent.toString().contains("glint")) {
            return parent;
        } else if (parent.toString().contains("eio")) {
            return parent;
        } else {
            if (!TYPES.containsKey(parent)) {
                TYPES.put(parent, parent);
            }
            return TYPES.get(parent);
        }
    }

    //TODO this probably is wrong
//    public static RenderType createLines(String name, int strength) {
//        return RenderType.create(EnderIO.MOD_ID + "_" + name, 1536, false, false, LINES_NO_CULL,
//            CompositeState.builder()
//                .setLineState(new LineStateShard(OptionalDouble.of(strength)))
//                .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
//                .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
//                .createCompositeState(false));
//    }

    public static RenderType createLines(String name, int strength) {
        return RenderType.create(EnderIO.MOD_ID + "_" + name, RenderSetup
            .builder(RenderPipelines.LINES)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            //.setLineState(new LineStateShard(OptionalDouble.of(strength)))
            .createRenderSetup());
    }

    public static final RenderPipeline LINES_NO_CULL = RenderPipelines.LINES.toBuilder()
        .withCull(false)
        .build();

}
