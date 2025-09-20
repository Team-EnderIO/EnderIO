package com.enderio.conduits.client;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.client.model.conduit.facades.ClientFacadeVisibility;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, value = Dist.CLIENT)
public class ConduitFacadeRendering {

    private static final ThreadLocal<RandomSource> RANDOM = ThreadLocal
            .withInitial(() -> new SingleThreadedRandomSource(42L));

    @SubscribeEvent
    static void renderFacade(AddSectionGeometryEvent event) {
        LongSet blockList = ConduitBundleBlockEntity.CHUNK_FACADES
                .getOrDefault(SectionPos.asLong(event.getSectionOrigin()), null);

        if (blockList == null) {
            return;
        }

        Map<BlockPos, BlockState> facades = new Object2ObjectOpenHashMap<>();

        for (long entry : blockList) {
            facades.put(BlockPos.of(entry), ConduitBundleBlockEntity.FACADES.get(entry));
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
            VertexConsumerWrapper wrapper = opaque ? null : new AlphaWrapper(context);

            RandomSource random = RANDOM.get();

            for (Map.Entry<BlockPos, BlockState> entry : facades.entrySet()) {
                context.getPoseStack().pushPose();
                context.getPoseStack()
                        .translate(entry.getKey().getX() & 15, entry.getKey().getY() & 15, entry.getKey().getZ() & 15);

                var state = entry.getValue();
                var pos = entry.getKey();

                random.setSeed(42L);

                var model = Minecraft.getInstance()
                        .getModelManager()
                        .getBlockModelShaper()
                        .getBlockModel(entry.getValue());

                var modelData = context.getRegion().getModelData(pos);

                modelData = model.getModelData(context.getRegion(), pos, state, modelData);

                for (var renderType : model.getRenderTypes(entry.getValue(), random, modelData)) {
                    VertexConsumer consumer = wrapper == null ? context.getOrCreateChunkBuffer(renderType) : wrapper;
                    Minecraft.getInstance()
                            .getBlockRenderer()
                            .getModelRenderer()
                            .tesselateBlock(context.getRegion(), model, state, pos, context.getPoseStack(), consumer,
                                    true, random, 42L, OverlayTexture.NO_OVERLAY, modelData, renderType);
                }

                context.getPoseStack().popPose();
            }
        }

        private static class AlphaWrapper extends VertexConsumerWrapper {
            public AlphaWrapper(AddSectionGeometryEvent.SectionRenderingContext context) {
                super(context.getOrCreateChunkBuffer(RenderType.translucent()));
            }

            @Override
            public VertexConsumer setColor(int r, int g, int b, int a) {
                super.setColor(r, g, b, 85);
                return this;
            }
        }
    }
}
