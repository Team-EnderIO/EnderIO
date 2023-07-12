package com.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.machines.client.rendering.model.ModelRenderUtil;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

/**
 * Thanks XFactHD for help and providing a demo for a preview widget and raycast example
 * <a href="https://gist.github.com/XFactHD/4b214f98a1b30a590c6e0de6bd84602a">Preview Widget Gist</a>
 * <p>
 * Definition  of {@link GhostBuffers}, {@link GhostRenderLayer} and initBuffers method are taken from Patchouli (License information: <a href="https://github.com/VazkiiMods/Patchouli">here</a>)
 */
public class IOConfigWidget<U extends EIOScreen<?>> extends AbstractWidget {

    private static final Quaternionf ROT_180_Z = Axis.ZP.rotation((float) Math.PI);
    private static final Vec3 RAY_ORIGIN = new Vec3(1.5, 1.5, 1.5);
    private static final Vec3 RAY_START = new Vec3(1.5, 1.5, -1);
    private static final Vec3 RAY_END = new Vec3(1.5, 1.5, 3);
    private static final BlockPos POS = new BlockPos(1, 1, 1);
    private static final int Z_OFFSET = 100;
    private static final ResourceLocation SELECTED_ICON = EnderIO.loc("block/overlay/selected_face");
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static MultiBufferSource.BufferSource ghostBuffers;
    private final U addedOn;
    private final Vector3f worldOrigin;
    private final Vector3f multiblockSize;
    private final List<BlockPos> configurable = new ArrayList<>();
    private final List<BlockPos> neighbours = new ArrayList<>();
    private final Font screenFont;
    private float SCALE = 20;
    private float pitch;
    private float yaw;
    private boolean neighbourVisible = true;
    private Optional<SelectedFace> selection = Optional.empty();

    public IOConfigWidget(U addedOn, int x, int y, int width, int height, BlockPos configurable, Font font) {
        this(addedOn, x, y, width, height, List.of(configurable), font);
    }

    public IOConfigWidget(U addedOn, int x, int y, int width, int height, List<BlockPos> _configurable, Font font) {
        super(x, y, width, height, Component.empty());
        this.addedOn = addedOn;
        this.configurable.addAll(_configurable);
        this.screenFont = font;

        if (configurable.size() == 1) {
            BlockPos bc = configurable.get(0);
            worldOrigin = new Vector3f(bc.getX() + 0.5f, bc.getY() + 0.5f, bc.getZ() + 0.5f);
            multiblockSize = new Vector3f(1, 1, 1);
        } else {
            Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
            Vector3f max = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
            for (BlockPos bc : configurable) {
                min.set(Math.min(bc.getX(), min.x()), Math.min(bc.getY(), min.y()), Math.min(bc.getZ(), min.z()));
                max.set(Math.max(bc.getX(), max.x()), Math.max(bc.getY(), max.y()), Math.max(bc.getZ(), max.z()));
            }
            multiblockSize = max;
            multiblockSize.sub(min);
            multiblockSize.mul(0.5f);
            worldOrigin = new Vector3f(min.x() + multiblockSize.x(), min.y() + multiblockSize.y(), min.z() + multiblockSize.z());
            multiblockSize.mul(2);
        }

        var radius = Math.max(Math.max(multiblockSize.x(), multiblockSize.y()), multiblockSize.z());
        SCALE -= (radius - 1) * 3; //adjust later
        SCALE = Math.min(40, Math.max(10, SCALE)); //clamp

        configurable.forEach(pos -> {
            for (Direction dir : Direction.values()) {
                BlockPos loc = pos.relative(dir);
                if (!configurable.contains(loc) && !neighbours.contains(loc)) {
                    neighbours.add(loc);
                }
            }

        });
        pitch = minecraft.player.getXRot();
        yaw = minecraft.player.getYRot();

        ghostBuffers = initBuffers(minecraft.renderBuffers().bufferSource());
    }

    private static Vec3 transform(Vec3 vec, Matrix4f transform) {
        // Move vector to a (0,0,0) origin as the transformation matrix expects
        Vector4f vec4 = new Vector4f((float) (vec.x - RAY_ORIGIN.x), (float) (vec.y - RAY_ORIGIN.y), (float) (vec.z - RAY_ORIGIN.z), 1F);
        // Apply the transformation matrix
        vec4.mul(transform);
        // Move transformed vector back to the actual origin
        return new Vec3(vec4.x() + RAY_ORIGIN.x, vec4.y() + RAY_ORIGIN.y, vec4.z() + RAY_ORIGIN.z);
    }

    @Nullable
    private BlockHitResult raycast(BlockPos pos, BlockState state, float diffX, float diffY, Matrix4f transform) {
        // Add mouse offset to start and end vectors
        Vec3 start = RAY_START.add(diffX, diffY, 0);
        Vec3 end = RAY_END.add(diffX, diffY, 0);

        // Rotate start and end vectors around the block
        start = transform(start, transform);
        end = transform(end, transform);

        // Get block's shape and cast a ray through it
        VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        Vector3f centerPos = new Vector3f(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).sub(worldOrigin);
        shape = shape.move(centerPos.x(), centerPos.y(), centerPos.z());
        return shape.clip(start, end, POS);
    }

    private static MultiBufferSource.BufferSource initBuffers(MultiBufferSource.BufferSource original) {
        BufferBuilder fallback = original.builder;
        Map<RenderType, BufferBuilder> layerBuffers = original.fixedBuffers;
        Map<RenderType, BufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap<>();
        for (Map.Entry<RenderType, BufferBuilder> e : layerBuffers.entrySet()) {
            remapped.put(GhostRenderLayer.remap(e.getKey()), e.getValue());
        }
        return new GhostBuffers(fallback, remapped);
    }

    public void toggleNeighbourVisibility() {
        neighbourVisible = !neighbourVisible;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.active && this.visible) {
            if (pButton == 1) {
                if (selection.isPresent()) {
                    var selectedFace = selection.get();
                    BlockEntity entity = minecraft.level.getBlockEntity(selectedFace.blockPos);
                    if (entity instanceof MachineBlockEntity machine) {
                        machine.getIOConfig().cycleMode(selectedFace.side);
                        this.playDownSound(Minecraft.getInstance().getSoundManager());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (visible && isValidClickButton(pButton) && isMouseOver(pMouseX, pMouseY)) {
            double dx = pDragX / (double) addedOn.width;
            double dy = pDragY / (double) addedOn.height;
            yaw += 4 * dx * 180;
            pitch += 2 * dy * 180;

            pitch = Math.min(80, Math.max(-80, pitch)); //clamp

        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (visible) {
            SCALE -= pDelta;
            SCALE = Math.min(40, Math.max(10, SCALE)); //clamp
            return true;
        }
        return false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (visible) {
            // render black bg
            if (isMouseOver(mouseX, mouseY)) {
                addedOn.setFocused(this);
            }
            guiGraphics.enableScissor(getX(), getY(), getX() + width, getY() + height);
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000);

            // Calculate widget center
            int centerX = getX() + (width / 2);
            int centerY = getY() + (height / 2);
            // Calculate mouse offset from center and scale to the block space
            float diffX = (mouseX - centerX) / SCALE;
            float diffY = (mouseY - centerY) / SCALE;

            Quaternionf rotPitch = Axis.XN.rotationDegrees(pitch);
            Quaternionf rotYaw = Axis.YP.rotationDegrees(yaw);

            // Build block transformation matrix
            // Rotate 180 around Z, otherwise the block is upside down
            Quaternionf blockTransform = new Quaternionf(ROT_180_Z);
            // Rotate around X (pitch) in negative direction
            blockTransform.mul(rotPitch);
            // Rotate around Y (yaw)
            blockTransform.mul(rotYaw);

            // Draw block
            renderWorld(guiGraphics, centerX, centerY, blockTransform, partialTick);

            // Build ray transformation matrix
            // Rotate 180 around Z, otherwise the block is upside down
            Matrix4f rayTransform = new Matrix4f();
            rayTransform.set(ROT_180_Z);
            // Rotate around Y (yaw)
            rayTransform.rotate(rotYaw);
            // Rotate around X (pitch) in negative direction
            rayTransform.rotate(rotPitch);

            // Ray-cast hit on block shape
            Map<BlockHitResult, BlockPos> hits = new HashMap<>();
            configurable.forEach(blockPos -> {
                BlockState state = minecraft.level.getBlockState(blockPos);
                BlockHitResult hit = raycast(blockPos, state, diffX, diffY, rayTransform);
                if (hit != null && hit.getType() != HitResult.Type.MISS) {
                    hits.put(hit, blockPos);
                }

            });

            Vec3 eyePosition = transform(RAY_START, rayTransform).add(worldOrigin.x, worldOrigin.y, worldOrigin.z);
            selection = hits
                .entrySet()
                .stream()
                .min(Comparator.comparingDouble(entry -> entry.getValue().distToCenterSqr(eyePosition))) // find closest to eye
                .map(closest -> new SelectedFace(closest.getValue(), closest.getKey().getDirection()));

            renderSelection(guiGraphics, centerX, centerY, blockTransform);
            renderOverlay(guiGraphics);

            guiGraphics.disableScissor();
        }
    }

    private void renderWorld(GuiGraphics guiGraphics, int centerX, int centerY, Quaternionf transform, float partialTick) {
        Lighting.setupForEntityInInventory();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, Z_OFFSET);
        guiGraphics.pose().scale(SCALE, SCALE, -SCALE);
        guiGraphics.pose().mulPose(transform);

        // RenderNeighbours
        if (neighbourVisible) {
            for (var neighbour : neighbours) {
                Vector3f pos = new Vector3f(neighbour.getX() - worldOrigin.x(), neighbour.getY() - worldOrigin.y(), neighbour.getZ() - worldOrigin.z());
                renderBlock(guiGraphics, neighbour, pos, ghostBuffers, partialTick);
            }

        }
        ghostBuffers.endBatch();

        // Render configurable
        MultiBufferSource.BufferSource normalBuffers = minecraft.renderBuffers().bufferSource();
        for (var configurable : configurable) {
            Vector3f pos = new Vector3f(configurable.getX() - worldOrigin.x(), configurable.getY() - worldOrigin.y(), configurable.getZ() - worldOrigin.z());
            renderBlock(guiGraphics, configurable, pos, normalBuffers, partialTick);
        }
        normalBuffers.endBatch();

        guiGraphics.pose().popPose();
    }

    private void renderBlock(GuiGraphics guiGraphics, BlockPos blockPos, Vector3f renderPos, MultiBufferSource.BufferSource buffers, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(renderPos.x(), renderPos.y(), renderPos.z());

        ModelData modelData = Optional.ofNullable(minecraft.level.getModelDataManager().getAt(blockPos)).orElse(ModelData.EMPTY);

        BlockState blockState = minecraft.level.getBlockState(blockPos);
        RenderShape rendershape = blockState.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            var renderer = minecraft.getBlockRenderer();
            BakedModel bakedmodel = renderer.getBlockModel(blockState);
            modelData = bakedmodel.getModelData(minecraft.level, blockPos, blockState, modelData);
            int blockColor = minecraft.getBlockColors().getColor(blockState, minecraft.level, blockPos, 0);
            float r = FastColor.ARGB32.red(blockColor) / 255F;
            float g = FastColor.ARGB32.green(blockColor) / 255F;
            float b = FastColor.ARGB32.blue(blockColor) / 255F;
            for (RenderType renderType : bakedmodel.getRenderTypes(blockState, RandomSource.create(42), modelData)) {
                renderer
                    .getModelRenderer()
                    .renderModel(guiGraphics.pose().last(), buffers.getBuffer(RenderTypeHelper.getEntityRenderType(renderType, false)), blockState, bakedmodel, r, g, b,
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, modelData, renderType);
            }
            BlockEntity blockEntity = minecraft.level.getBlockEntity(blockPos);
            if (blockEntity != null) {
                var beRenderer = minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
                if (beRenderer != null) {
                    beRenderer.render(blockEntity, partialTick, guiGraphics.pose(), buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                }

            }
        }
        guiGraphics.pose().popPose();
    }

    private void renderSelection(GuiGraphics guiGraphics, int centerX, int centerY, Quaternionf transform) {
        if (selection.isEmpty()) {
            return;
        }
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, Z_OFFSET);
        guiGraphics.pose().scale(SCALE, SCALE, -SCALE);
        guiGraphics.pose().mulPose(transform);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        TextureAtlasSprite tex = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SELECTED_ICON);
        RenderSystem.setShaderTexture(0, tex.atlasLocation());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        var selectedFace = selection.get();
        BlockPos blockPos = selectedFace.blockPos;
        guiGraphics.pose().translate(blockPos.getX() - worldOrigin.x(), blockPos.getY() - worldOrigin.y(), blockPos.getZ() - worldOrigin.z());
        Vector3f[] vec = Arrays.stream(ModelRenderUtil.createQuadVerts(selectedFace.side, 0, 1, 1)).map(Vec3::toVector3f).toArray(Vector3f[]::new);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        bufferbuilder.vertex(matrix4f, vec[0].x(), vec[0].y(), vec[0].z()).color(1F, 1F, 1F, 1F).uv(tex.getU0(), tex.getV0()).endVertex();
        bufferbuilder.vertex(matrix4f, vec[1].x(), vec[1].y(), vec[1].z()).color(1F, 1F, 1F, 1F).uv(tex.getU0(), tex.getV1()).endVertex();
        bufferbuilder.vertex(matrix4f, vec[2].x(), vec[2].y(), vec[2].z()).color(1F, 1F, 1F, 1F).uv(tex.getU1(), tex.getV1()).endVertex();
        bufferbuilder.vertex(matrix4f, vec[3].x(), vec[3].y(), vec[3].z()).color(1F, 1F, 1F, 1F).uv(tex.getU1(), tex.getV0()).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());

        guiGraphics.pose().popPose();
    }

    private void renderOverlay(GuiGraphics guiGraphics) {
        if (selection.isPresent()) {
            var selectedFace = selection.get();
            BlockEntity entity = minecraft.level.getBlockEntity(selectedFace.blockPos);
            if (entity instanceof MachineBlockEntity machine) {
                var ioMode = machine.getIOConfig().getMode(selectedFace.side);
                IOModeMap map = IOModeMap.getMapFromMode(ioMode);
                Rect2i iconBounds = map.getRect();
                guiGraphics.blit(IOConfigButton.IOCONFIG, getX() + 4, getY() + height - 4 - screenFont.lineHeight - iconBounds.getHeight(), iconBounds.getX(), iconBounds.getY(),
                    iconBounds.getWidth(), iconBounds.getHeight(), 48, 32);
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0, 0, 1000); // to ensure that string is drawn on top
                guiGraphics.drawString(screenFont, map.getComponent(), getX() + 4, getY() + height - 2 - screenFont.lineHeight, 0xFFFFFFFF);
                guiGraphics.pose().popPose();
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    private record SelectedFace(BlockPos blockPos, Direction side) {}

    private static class GhostBuffers extends MultiBufferSource.BufferSource {
        private GhostBuffers(BufferBuilder fallback, Map<RenderType, BufferBuilder> layerBuffers) {
            super(fallback, layerBuffers);
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            return super.getBuffer(GhostRenderLayer.remap(type));
        }
    }

    private static class GhostRenderLayer extends RenderType {
        private static final Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();

        private GhostRenderLayer(RenderType original) {
            super(String.format("%s_%s_ghost", original.toString(), EnderIO.MODID), original.format(), original.mode(), original.bufferSize(),
                original.affectsCrumbling(), true, () -> {
                    original.setupRenderState();

                    RenderSystem.disableDepthTest();
                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(1, 1, 1, MachinesConfig.CLIENT.IO_CONFIG_NEIGHBOUR_TRANSPARENCY.get());
                },
                () -> {
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                    RenderSystem.disableBlend();
                    RenderSystem.enableDepthTest();

                    original.clearRenderState();
                });
        }

        public static RenderType remap(RenderType in) {
            if (in instanceof GhostRenderLayer) {
                return in;
            } else {
                return remappedTypes.computeIfAbsent(in, GhostRenderLayer::new);
            }
        }
    }

}
