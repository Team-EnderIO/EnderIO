package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.enderio.core.client.gui.screen.BaseOverlay;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.foundation.network.packets.ServerboundCycleIOConfigPacket;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;

/**
 * Thanks XFactHD for help and providing a demo for a preview widget and raycast example
 * <a href="https://gist.github.com/XFactHD/4b214f98a1b30a590c6e0de6bd84602a">Preview Widget Gist</a>
 * <p>
 * Definition  of {@link GhostBuffers}, {@link GhostRenderLayer} and initBuffers method are taken from Patchouli (License information: <a href="https://github.com/VazkiiMods/Patchouli">here</a>)
 */
public class IOConfigOverlay extends BaseOverlay {
    private static final Identifier IO_CONFIG_OVERLAY = EnderIO.id("buttons/io_config_overlay");
    private static final Identifier SELECTED_ICON = EnderIO.id("block/overlay/selected_face");
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static MultiBufferSource.BufferSource ghostBuffers;
    private static MultiBufferSource.BufferSource solidBuffers;
    private final Vector3f worldOrigin;
    private final Vector3f multiblockSize;
    private final List<BlockPos> configurable = new ArrayList<>();
    private final List<BlockPos> neighbours = new ArrayList<>();
    private boolean neighbourVisible = true;
    private Optional<SelectedFace> selection = Optional.empty();

    private final IOConfigSceneCamera camera;

    // Neighbour Button
    public static final Identifier NEIGHBOURS_BTN = EnderIO.id("buttons/neighbour");
    private final Rect2i neighBtnRect;

    public IOConfigOverlay(int x, int y, int width, int height, List<BlockPos> _configurable) {
        super(x, y, width, height, Component.empty());
        this.configurable.addAll(_configurable);

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
            worldOrigin = new Vector3f(min.x() + multiblockSize.x(), min.y() + multiblockSize.y(),
                    min.z() + multiblockSize.z());
            multiblockSize.mul(2);
        }

        var radius = Math.max(Math.max(multiblockSize.x(), multiblockSize.y()), multiblockSize.z());

        float scale = 20f;
        scale -= (radius - 1) * 3; // adjust later
        scale = Math.min(40, Math.max(10, scale)); // clamp

        configurable.forEach(pos -> {
            for (Direction dir : Direction.values()) {
                BlockPos loc = pos.relative(dir);
                if (!configurable.contains(loc) && !neighbours.contains(loc)) {
                    neighbours.add(loc);
                }
            }

        });
        float pitch = MINECRAFT.player.getXRot();
        float yaw = MINECRAFT.player.getYRot();

        // TODO: properly integrate
        this.camera = new IOConfigSceneCamera(worldOrigin, scale, pitch, yaw);

        initBuffers(MINECRAFT.renderBuffers().bufferSource());
        neighBtnRect = new Rect2i(getX() + getWidth() - 2 - 16, getY() + getHeight() - 2 - 16, 16, 16);
    }

    @Override
    public Object getValueForRestore() {
        return new RestoreData(this.visible, camera.yaw(), camera.pitch(), camera.scale());
    }

    @Override
    public void restoreValue(Object value) {
        if (value instanceof RestoreData restoreData) {
            this.visible = restoreData.isVisible;
            camera.setYaw(restoreData.yaw);
            camera.setPitch(restoreData.pitch);
            camera.setScale(restoreData.scale);
        }
    }

    private record RestoreData(boolean isVisible, float yaw, float pitch, float scale) {
    }

    private void initBuffers(MultiBufferSource.BufferSource original) {
        ByteBufferBuilder fallback = original.sharedBuffer;
        SequencedMap<RenderType, ByteBufferBuilder> layerBuffers = original.fixedBuffers;
        SequencedMap<RenderType, ByteBufferBuilder> ghostLayers = new Object2ObjectLinkedOpenHashMap<>();
        SequencedMap<RenderType, ByteBufferBuilder> solidLayers = new Object2ObjectLinkedOpenHashMap<>();

        for (Map.Entry<RenderType, ByteBufferBuilder> e : layerBuffers.entrySet()) {
//            ghostLayers.put(GhostRenderLayer.remap(e.getKey()), e.getValue());
//            solidLayers.put(SolidRenderLayer.remap(e.getKey()), e.getValue());
        }
//        ghostBuffers = new GhostBuffers(fallback, ghostLayers);
//        solidBuffers = new SolidBuffers(fallback, solidLayers);
    }

    private Ray createRay(float x, float y) {
        Matrix4f invView = new Matrix4f(camera.viewMatrix()).invert();

        // Determine mouse coordinate in-world
        var viewPoint = new Vector4f(x, y, 0, 1);
        viewPoint.mul(invView);

        var origin = new Vector3f(viewPoint.x, viewPoint.y, viewPoint.z);

        // Determine forward vector
        var direction = new Vector3f(0, 0, -1);
        invView.transformDirection(direction).normalize();

        return new Ray(origin, direction);
    }

    @Nullable
    private BlockHitResult raycast(BlockPos pos, BlockState state, Ray ray) {
        // Start .5 blocks behind the point
        Vector3f start = ray.origin.add(ray.direction().mul(-0.5f, new Vector3f()), new Vector3f());

        // Travel 3.5 blocks toward the point
        Vector3f end = ray.origin.add(ray.direction().mul(3.5f, new Vector3f()), new Vector3f());

        // Get block's shape and cast a ray through it
        VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        return shape.clip(new Vec3(start.x, start.y, start.z), new Vec3(end.x, end.y, end.z), pos);
    }

    private record Ray(Vector3f origin, Vector3f direction) {}

    public void toggleNeighbourVisibility() {
        neighbourVisible = !neighbourVisible;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (this.active && this.visible) {
            if (event.button() == 0) {
                if (neighBtnRect.contains((int) event.x(), (int) event.y())) {
                    toggleNeighbourVisibility();
                    this.playDownSound(MINECRAFT.getSoundManager());
                    return true;
                }
            }
            if (event.button() == 1) {
                if (selection.isPresent()) {
                    var selectedFace = selection.get();
                    BlockEntity entity = MINECRAFT.level.getBlockEntity(selectedFace.blockPos);
                    if (entity instanceof IOConfigurable) {
                        ClientPacketDistributor
                                .sendToServer(new ServerboundCycleIOConfigPacket(selectedFace.blockPos, selectedFace.side));
                        this.playDownSound(MINECRAFT.getSoundManager());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (visible && isValidClickButton(event.buttonInfo()) && isMouseOver(event.x(), event.y())
                && !neighBtnRect.contains((int) event.x(), (int) event.y())) {
            double dx = dragX / (double) MINECRAFT.getWindow().getGuiScaledWidth();
            double dy = dragY / (double) MINECRAFT.getWindow().getGuiScaledHeight();

            // Determine if we're panning or rotating.
            if (event.hasShiftDown()) {
                // TODO: Could use a little work...
                // 16 seems to be reasonable
                float dragSpeed = 16f;

                Vector3f delta = new Vector3f((float)-dx * dragSpeed, (float)-dy * dragSpeed, 0);
                var dragDelta = delta.rotate(camera.blockTransform());

                var sceneOrigin = camera.sceneOrigin();
                camera.setSceneOrigin(sceneOrigin.add(dragDelta.x, dragDelta.y, dragDelta.z));
            } else {
                float yaw = camera.yaw();
                float pitch = camera.pitch();

                yaw += 4 * (float) dx * 180;
                pitch += 2 * (float) dy * 180;

                pitch = Math.min(80, Math.max(-80, pitch)); // clamp

                camera.setYaw(yaw);
                camera.setPitch(pitch);
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (visible) {
            float scale = camera.scale();
            scale -= deltaY;
            scale = Math.min(40, Math.max(10, scale)); // clamp
            camera.setScale(scale);
            return true;
        }
        return false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (!visible) {
            return;
        }

        graphics.enableScissor(getX(), getY(), getX() + width, getY() + height);
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000);

        // Calculate widget center
        int centerX = getX() + (width / 2);
        int centerY = getY() + (height / 2);

        // Calculate mouse offset from center of overlay
        float adjustedMouseX = (mouseX - centerX);
        float adjustedMouseY = (mouseY - centerY);

        var ray = createRay(adjustedMouseX, adjustedMouseY);

        // Ray-cast hit on block shape
        Map<BlockHitResult, BlockPos> hits = new HashMap<>();
        configurable.forEach(blockPos -> {
            BlockState state = MINECRAFT.level.getBlockState(blockPos);
            BlockHitResult hit = raycast(blockPos, state, ray);
            if (hit != null && hit.getType() != HitResult.Type.MISS) {
                hits.put(hit, blockPos);
            }
        });

        // Find the hit that is closest to the camera
        Vec3 eyePosition = camera.getEyePosition();
        selection = hits.entrySet()
            .stream()
            .min(Comparator.comparingDouble(entry -> entry.getValue().distToCenterSqr(eyePosition)))
            .map(closest -> new SelectedFace(closest.getValue(), closest.getKey().getDirection()));

        // Render the scene
        graphics.submitPictureInPictureRenderState(
            IOConfigSceneRenderState.create(
                MINECRAFT.level,
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                graphics.peekScissorStack(),
                this.camera.viewMatrix(),
                configurable,
                neighbours,
                neighbourVisible
            ));

//        renderSelection(graphics, centerX, centerY, blockTransform);
        renderOverlay(graphics);

        graphics.disableScissor();

        // after scissor to prevent clipping the tooltip
        renderNeighbourButton(graphics, mouseX, mouseY);
    }

    private void renderSelection(GuiGraphicsExtractor graphics, int centerX, int centerY, Quaternionf transform) {
        if (selection.isEmpty()) {
            return;
        }
        graphics.pose().pushMatrix();
//        graphics.pose().translate(centerX, centerY, Z_OFFSET);
//        graphics.pose().scale(scale, scale, -scale);
//        graphics.pose().mulPose(transform);

        BufferBuilder bufferbuilder = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        // TODO: 1.21.4: Was this needed?
//        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        TextureAtlasSprite tex = MINECRAFT.getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(SELECTED_ICON);
//        RenderSystem.setShaderTexture(0, tex.atlasLocation());
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        var selectedFace = selection.get();
        BlockPos blockPos = selectedFace.blockPos;
//        graphics.pose()
//                .translate(blockPos.getX() - worldOrigin.x(), blockPos.getY() - worldOrigin.y(),
//                        blockPos.getZ() - worldOrigin.z());
//        Vector3f[] vec = ModelRenderUtil.createQuadVerts(selectedFace.side, 0, 1, 1);
//        Matrix4f matrix4f = graphics.pose().last().pose();
//        bufferbuilder.addVertex(matrix4f, vec[0].x(), vec[0].y(), vec[0].z())
//                .setColor(1F, 1F, 1F, 1F)
//                .setUv(tex.getU0(), tex.getV0());
//        bufferbuilder.addVertex(matrix4f, vec[1].x(), vec[1].y(), vec[1].z())
//                .setColor(1F, 1F, 1F, 1F)
//                .setUv(tex.getU0(), tex.getV1());
//        bufferbuilder.addVertex(matrix4f, vec[2].x(), vec[2].y(), vec[2].z())
//                .setColor(1F, 1F, 1F, 1F)
//                .setUv(tex.getU1(), tex.getV1());
//        bufferbuilder.addVertex(matrix4f, vec[3].x(), vec[3].y(), vec[3].z())
//                .setColor(1F, 1F, 1F, 1F)
//                .setUv(tex.getU1(), tex.getV0());
//        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

        graphics.pose().popMatrix();
    }

    private void renderOverlay(GuiGraphicsExtractor graphics) {
        if (selection.isPresent()) {
            var selectedFace = selection.get();
            BlockEntity entity = MINECRAFT.level.getBlockEntity(selectedFace.blockPos);
            if (entity instanceof IOConfigurable ioConfigurable) {
                var ioMode = ioConfigurable.getIOMode(selectedFace.side);
                IOModeMap map = IOModeMap.getMapFromMode(ioMode);
                Rect2i iconBounds = map.getRect();
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, IO_CONFIG_OVERLAY, 48, 16, iconBounds.getX(), iconBounds.getY(), getX() + 4,
                    getY() + height - 4 - MINECRAFT.font.lineHeight - iconBounds.getHeight(), iconBounds.getWidth(),
                    iconBounds.getHeight());
                graphics.pose().pushMatrix();
                graphics.text(MINECRAFT.font, map.getComponent(), getX() + 4,
                    getY() + height - 2 - MINECRAFT.font.lineHeight, CommonColors.DARK_GRAY);
                graphics.pose().popMatrix();
            }
        }
    }

    private void renderNeighbourButton(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, NEIGHBOURS_BTN, neighBtnRect.getX(), neighBtnRect.getY(), 16, 16);
        if (neighBtnRect.contains(mouseX, mouseY)) {
            graphics.tooltip(MINECRAFT.font, List.of(ClientTooltipComponent.create(EIOCommonLang.TOGGLE_NEIGHBOUR.copy().withStyle(ChatFormatting.WHITE).getVisualOrderText())),
                    mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    private record SelectedFace(BlockPos blockPos, Direction side) {
    }

//    private static class GhostBuffers extends MultiBufferSource.BufferSource {
//        private GhostBuffers(ByteBufferBuilder fallback, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
//            super(fallback, layerBuffers);
//        }
//
//        @Override
//        public VertexConsumer getBuffer(RenderType type) {
//            return super.getBuffer(GhostRenderLayer.remap(type));
//        }
//    }
//
//    private static class SolidBuffers extends MultiBufferSource.BufferSource {
//        private SolidBuffers(ByteBufferBuilder fallback, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
//            super(fallback, layerBuffers);
//        }
//
//        @Override
//        public VertexConsumer getBuffer(RenderType type) {
//            return super.getBuffer(SolidRenderLayer.remap(type));
//        }
//    }

    // Solid buffers, but without depth testing.
//    private static class SolidRenderLayer extends RenderType {
//        private static final Map<RenderType, RenderType> REMAPPED_TYPES = new IdentityHashMap<>();
//
//        private SolidRenderLayer(RenderType original) {
//            super(String.format("%s_%s_solid", original, EnderIO.MOD_ID), original.format(), original.mode(),
//                    original.bufferSize(), original.affectsCrumbling(), true, () -> {
//                        original.setupRenderState();
//
//                        RenderSystem.disableDepthTest();
//                    }, () -> {
//                        RenderSystem.enableDepthTest();
//
//                        original.clearRenderState();
//                    });
//        }
//
//        public static RenderType remap(RenderType in) {
//            if (in instanceof SolidRenderLayer) {
//                return in;
//            } else {
//                return REMAPPED_TYPES.computeIfAbsent(in, SolidRenderLayer::new);
//            }
//        }
//    }

//    private static class GhostRenderLayer extends RenderType {
//        private static final Map<RenderType, RenderType> REMAPPED_TYPES = new IdentityHashMap<>();
//
//        private GhostRenderLayer(RenderType original) {
//            super(String.format("%s_%s_ghost", original, EnderIO.MOD_ID), original.format(), original.mode(),
//                    original.bufferSize(), original.affectsCrumbling(), true, () -> {
//                        original.setupRenderState();
//
//                        RenderSystem.disableDepthTest();
//                        RenderSystem.enableBlend();
//                        RenderSystem.setShaderColor(1, 1, 1,
//                                MachinesConfig.CLIENT.IO_CONFIG_NEIGHBOUR_TRANSPARENCY.get().floatValue());
//                    }, () -> {
//                        RenderSystem.setShaderColor(1, 1, 1, 1);
//                        RenderSystem.disableBlend();
//                        RenderSystem.enableDepthTest();
//
//                        original.clearRenderState();
//                    });
//        }
//
//        public static RenderType remap(RenderType in) {
//            if (in instanceof GhostRenderLayer) {
//                return in;
//            } else {
//                return REMAPPED_TYPES.computeIfAbsent(in, GhostRenderLayer::new);
//            }
//        }
//    }

}
