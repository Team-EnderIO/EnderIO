package com.enderio.enderio.client.content.conduits;

import com.enderio.enderio.client.content.conduits.model.facades.ClientFacadeVisibility;
import com.enderio.enderio.compat.ModCompatHelper;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT)
public class ConduitFacadeRendering {

    @SubscribeEvent
    static void renderFacade(AddSectionGeometryEvent event) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        var dimension = level.dimension();

        var facadesForDimension = ConduitBundleBlockEntity.CHUNK_FACADES.get(dimension);
        if (facadesForDimension == null) {
            return;
        }

        LongSet blockList = facadesForDimension.getOrDefault(SectionPos.asLong(event.getSectionOrigin()), null);
        if (blockList == null) {
            return;
        }

        var facadesForDim = ConduitBundleBlockEntity.FACADES.get(dimension);
        if (facadesForDim == null) {
            return;
        }

        Map<BlockPos, BlockState> facades = new Object2ObjectOpenHashMap<>();
        for (long entry : blockList) {
            BlockState state = facadesForDim.get(entry);
            if (state != null) {
                facades.put(BlockPos.of(entry), state);
            }
        }

        if (facades.isEmpty())
            return;

        event.addRenderer(new FacadeRenderer(facades, ClientFacadeVisibility.areFacadesVisible()));
    }

    private static class FacadeRenderer implements AddSectionGeometryEvent.AdditionalSectionRenderer {
        private final Map<BlockPos, BlockState> facades;
        private final boolean opaque;

        public FacadeRenderer(Map<BlockPos, BlockState> facades, boolean opaque) {
            this.facades = facades;
            this.opaque = opaque;
        }

        @Override
        public void render(AddSectionGeometryEvent.SectionRenderingContext context) {
            // Render nothing if a shader pack is in use - transparent facades do not render well with shaders.
            // See GH-1062 for more details.
            if (!this.opaque && ModCompatHelper.hasIris()) {
                if (IrisApi.getInstance().isShaderPackInUse()) {
                    return;
                }
            }

            VertexConsumerWrapper wrapper = opaque ? null : new AlphaWrapper(context);

            for (Map.Entry<BlockPos, BlockState> entry : facades.entrySet()) {
                var state = entry.getValue();
                var pos = entry.getKey();

                var model = Minecraft.getInstance()
                        .getModelManager()
                        .getBlockStateModelSet()
                        .get(entry.getValue());

                BlockQuadOutput output = (x, y, z, bakedQuad, quadInstance) -> {
                    VertexConsumer buffer = wrapper != null ? wrapper : context.getOrCreateChunkBuffer(bakedQuad.materialInfo().layer());
                    buffer.putBlockBakedQuad(x, y, z, bakedQuad, quadInstance);
                };

                context.getBlockRenderer().tesselateBlock(output,
                    SectionPos.sectionRelative(pos.getX()),
                    SectionPos.sectionRelative(pos.getY()),
                    SectionPos.sectionRelative(pos.getZ()),
                    context.getRegion(),
                    pos,
                    state,
                    model,
                    state.getSeed(pos));
            }
        }

        private static class AlphaWrapper extends VertexConsumerWrapper {
            public AlphaWrapper(AddSectionGeometryEvent.SectionRenderingContext context) {
                super(context.getOrCreateChunkBuffer(ChunkSectionLayer.TRANSLUCENT));
            }

            @Override
            public VertexConsumer setColor(int r, int g, int b, int a) {
                super.setColor(r, g, b, 85);
                return this;
            }

            @Override
            public VertexConsumer setColor(int packedColor) {
                super.setColor(ARGB.color(85, packedColor));
                return this;
            }
        }
    }
}
