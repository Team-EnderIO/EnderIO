package com.enderio.enderio.client;

import com.enderio.enderio.EnderIO;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.pipeline.PipelineModifier;
import net.neoforged.neoforge.client.pipeline.RegisterPipelineModifiersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class EIOPipelineModifiers {
    /**
     * Force ENTITY_CUTOUT, ENTITY_CUTOUT_CULLED and ENTITY_SOLID to blend.
     */
    public static final ResourceKey<PipelineModifier> FORCE_TRANSLUCENT = ResourceKey.create(PipelineModifier.MODIFIERS_KEY, EnderIO.id("force_translucent"));

    @SubscribeEvent
    public static void onRegisterModifiers(RegisterPipelineModifiersEvent event)
    {
        event.register(FORCE_TRANSLUCENT, (pipeline, name) ->
        {
            if (pipeline == RenderPipelines.ENTITY_CUTOUT || pipeline == RenderPipelines.ENTITY_CUTOUT_CULL || pipeline == RenderPipelines.ENTITY_SOLID)
            {
                return pipeline.toBuilder()
                    .withLocation(name)
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .build();
            }
            return pipeline;
        });
    }
}
