package com.enderio.enderio.client.foundation.renderer;

import com.enderio.enderio.EnderIO;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
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

    public static final RenderPipeline LINES_NO_DEPTH_SNIPPET = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
        .withLocation(EnderIO.id("pipeline/lines_no_depth"))
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build();

    public static final RenderType LINES_NO_DEPTH = RenderType.create("lines_no_depth", RenderSetup.builder(LINES_NO_DEPTH_SNIPPET)
        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
        .createRenderSetup());

    public static final RenderPipeline CUTOUT_NO_DEPTH_SNIPPET = RenderPipeline.builder(RenderPipelines.BLOCK_SNIPPET)
        .withLocation(EnderIO.id("pipeline/cutout_no_depth"))
        .withShaderDefine("ALPHA_CUTOUT", 0.5F)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build();

    public static final RenderType CUTOUT_NO_DEPTH = RenderType.create(
        "cutout_no_depth",
        RenderSetup.builder(CUTOUT_NO_DEPTH_SNIPPET)
            .useLightmap()
            .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, RenderTypes.MOVING_BLOCK_SAMPLER)
            .affectsCrumbling()
            .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
            .createRenderSetup());

    @SubscribeEvent
    public static void onRegisterRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(LINES_NO_DEPTH_SNIPPET);
        event.registerPipeline(CUTOUT_NO_DEPTH_SNIPPET);
    }

}
