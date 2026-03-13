package com.enderio.enderio.client.foundation.renderer;

import com.enderio.enderio.EnderIO;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .build();

    public static final RenderType LINES_NO_DEPTH = RenderType.create("lines_no_depth", RenderSetup.builder(LINES_NO_DEPTH_SNIPPET)
        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
        .createRenderSetup());

    public static final RenderPipeline CUTOUT_NO_DEPTH_SNIPPET = RenderPipeline.builder(RenderPipelines.BLOCK_SNIPPET)
        .withLocation(EnderIO.id("pipeline/cutout_no_depth"))
        .withShaderDefine("ALPHA_CUTOUT", 0.5F)
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .build();

    private static final Supplier<GpuSampler> LINEAR_FILTERING_SAMPLER = () -> RenderSystem.getSamplerCache()
        .getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, false);

    public static final RenderType CUTOUT_NO_DEPTH = RenderType.create(
        "cutout_no_depth",
        RenderSetup.builder(CUTOUT_NO_DEPTH_SNIPPET)
            .useLightmap()
            .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, LINEAR_FILTERING_SAMPLER)
            .affectsCrumbling()
            .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
            .createRenderSetup());

    @SubscribeEvent
    public static void onRegisterRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(LINES_NO_DEPTH_SNIPPET);
        event.registerPipeline(CUTOUT_NO_DEPTH_SNIPPET);
    }

}
