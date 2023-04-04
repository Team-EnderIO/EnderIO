package com.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.machines.client.rendering.model.ModelRenderUtil;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Thanks XFactHD for help and providing a demo for a preview widget and raycast example
 * <a href="https://gist.github.com/XFactHD/4b214f98a1b30a590c6e0de6bd84602a">Preview Widget Gist</a>
 */
public class IOConfigWidget<U extends EIOScreen<?>> extends AbstractWidget {

    private static final Quaternion ROT_180_Z = Vector3f.ZP.rotation((float) Math.PI);
    private static final Vec3 RAY_ORIGIN = new Vec3(1.5, 1.5, 1.5);
    private static final Vec3 RAY_START = new Vec3(1.5, 1.5, -1);
    private static final Vec3 RAY_END = new Vec3(1.5, 1.5, 3);
    private static final BlockPos POS = new BlockPos(1, 1, 1);
    private static final int Z_OFFSET = 100;
    private static final ResourceLocation SELECTED_ICON = EnderIO.loc("block/overlay/selected_face");
    private static final float NEIGHBOUR_TRANSPARENCY = 0.4F; // range 0-1
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static MultiBufferSource.BufferSource ghostBuffers;
    private final U addedOn;
    private final @NotNull Vector3f worldOrigin;
    private final Vector3f multiblockSize;
    private final List<BlockPos> configurable = new ArrayList<>();
    private final List<BlockPos> neighbours = new ArrayList<>();
    private final Font screenFont;
    private float SCALE = 20;
    private float pitch;
    private float yaw;
    private boolean neighbourVisible = true;
    private @NotNull Optional<SelectedFace> selection = Optional.empty();

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

        configurable.forEach(pos -> {
            for (Direction dir : Direction.values()) {
                BlockPos loc = pos.offset(dir.getNormal());
                if (!configurable.contains(loc)) {
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
        vec4.transform(transform);
        // Move transformed vector back to the actual origin
        return new Vec3(vec4.x() + RAY_ORIGIN.x, vec4.y() + RAY_ORIGIN.y, vec4.z() + RAY_ORIGIN.z);
    }

    private static BlockHitResult raycast(BlockState state, float diffX, float diffY, Matrix4f transform) {
        // Add mouse offset to start and end vectors
        Vec3 start = RAY_START.add(diffX, diffY, 0);
        Vec3 end = RAY_END.add(diffX, diffY, 0);

        // Rotate start and end vectors around the block
        start = transform(start, transform);
        end = transform(end, transform);

        // Get block's shape and cast a ray through it
        VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (visible) {
            // render black bg
            enableScissor(x, y, x + width, y + height);
            fill(poseStack, x, y, x + width, y + height, 0xFF000000);

            // Calculate widget center
            int centerX = x + (width / 2);
            int centerY = y + (height / 2);
            // Calculate mouse offset from center and scale to the block space
            float diffX = (mouseX - centerX) / SCALE;
            float diffY = (mouseY - centerY) / SCALE;

            Quaternion rotPitch = Vector3f.XN.rotationDegrees(pitch);
            Quaternion rotYaw = Vector3f.YP.rotationDegrees(yaw);

            // Build block transformation matrix
            // Rotate 180 around Z, otherwise the block is upside down
            Quaternion blockTransform = new Quaternion(ROT_180_Z);
            // Rotate around X (pitch) in negative direction
            blockTransform.mul(rotPitch);
            // Rotate around Y (yaw)
            blockTransform.mul(rotYaw);

            // Draw block
            renderWorld(poseStack, centerX, centerY, blockTransform, partialTick);

            // Build ray transformation matrix
            // Rotate 180 around Z, otherwise the block is upside down
            Matrix4f rayTransform = new Matrix4f(ROT_180_Z);
            // Rotate around Y (yaw)
            rayTransform.multiply(rotYaw);
            // Rotate around X (pitch) in negative direction
            rayTransform.multiply(rotPitch);

            // Ray-cast hit on block shape
            Map<BlockHitResult, BlockPos> hits = new HashMap<>();
            //            List<BlockHitResult> hits = new ArrayList<>();
            configurable.forEach(blockPos -> {
                BlockState state = minecraft.level.getBlockState(blockPos);
                BlockHitResult hit = raycast(state, diffX, diffY, rayTransform);
                if (hit != null && hit.getType() != HitResult.Type.MISS) {
                    hits.put(hit, blockPos);
                }

            });

            Vec3 _origin = new Vec3(worldOrigin);
            Optional<Map.Entry<BlockHitResult, BlockPos>> opt = hits
                .entrySet()
                .stream()
                .min((a, b) -> (int) (a.getValue().distToCenterSqr(_origin) - b.getValue().distToCenterSqr(_origin))); // minimum

            if (opt.isPresent()) {
                Map.Entry<BlockHitResult, BlockPos> closest = opt.get();
                selection = Optional.of(new SelectedFace(closest.getValue(), closest.getKey().getDirection()));
            } else {
                selection = Optional.empty();
            }

            renderSelection(poseStack, centerX, centerY, blockTransform);
            renderOverlay(poseStack);

            disableScissor();
        }
    }

    private void renderWorld(PoseStack poseStack, int centerX, int centerY, Quaternion transform, float partialTick) {
        Lighting.setupForEntityInInventory();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, Z_OFFSET);
        poseStack.scale(SCALE, SCALE, -SCALE);
        poseStack.mulPose(transform);

        // RenderNeighbours
        if (neighbourVisible) {
            for (var neighbour : neighbours) {
                Vector3f pos = new Vector3f(neighbour.getX() - worldOrigin.x(), neighbour.getY() - worldOrigin.y(), neighbour.getZ() - worldOrigin.z());
                renderBlock(poseStack, neighbour, pos, ghostBuffers);
            }

        }
        ghostBuffers.endBatch();

        // Render configurable
        MultiBufferSource.BufferSource normalBuffers = minecraft.renderBuffers().bufferSource();
        //TODO: recheck when capacitor banks
        for (var configurable : configurable) {
            Vector3f pos = new Vector3f(configurable.getX() - worldOrigin.x(), configurable.getY() - worldOrigin.y(), configurable.getZ() - worldOrigin.z());
            renderBlock(poseStack, configurable, pos, normalBuffers);
        }
        normalBuffers.endBatch();

        poseStack.popPose();
    }

    private void renderBlock(PoseStack poseStack, BlockPos blockPos, Vector3f renderPos, MultiBufferSource.BufferSource buffers) {
        poseStack.pushPose();
        poseStack.translate(renderPos.x(), renderPos.y(), renderPos.z());
        BlockEntity blockEntity = minecraft.level.getBlockEntity(blockPos);
        BlockState blockState = blockEntity != null ? blockEntity.getBlockState() : minecraft.level.getBlockState(blockPos);

        ModelData modelData = minecraft.level.getModelDataManager().getAt(blockPos);
        if (blockEntity instanceof MachineBlockEntity machine) {
            modelData = machine.getModelData();
        }
        modelData = modelData == null ? ModelData.EMPTY : modelData;
        var renderer = minecraft.getBlockRenderer();
        renderer.renderSingleBlock(blockState, poseStack, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, modelData, null);
        poseStack.popPose();

    }

    private void renderSelection(PoseStack poseStack, int centerX, int centerY, Quaternion transform) {
        if (selection.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, Z_OFFSET);
        poseStack.scale(SCALE, SCALE, -SCALE);
        poseStack.mulPose(transform);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        TextureAtlasSprite tex = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SELECTED_ICON);
        RenderSystem.setShaderTexture(0, tex.atlas().location());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        var selectedFace = selection.get();
        BlockPos blockPos = selectedFace.blockPos;
        poseStack.translate(blockPos.getX() - worldOrigin.x(), blockPos.getY() - worldOrigin.y(), blockPos.getZ() - worldOrigin.z());
        Vector3f[] vec = Arrays.stream(ModelRenderUtil.createQuadVerts(selectedFace.side, 0, 1, 1)).map(Vector3f::new).toArray(Vector3f[]::new);
        Matrix4f matrix4f = poseStack.last().pose();
        bufferbuilder.vertex(matrix4f, vec[0].x(), vec[0].y(), vec[0].z()).color(1F, 1F, 1F, 1F).uv(tex.getU0(), tex.getV0()).endVertex();
        bufferbuilder.vertex(matrix4f, vec[1].x(), vec[1].y(), vec[1].z()).color(1F, 1F, 1F, 1F).uv(tex.getU0(), tex.getV1()).endVertex();
        bufferbuilder.vertex(matrix4f, vec[2].x(), vec[2].y(), vec[2].z()).color(1F, 1F, 1F, 1F).uv(tex.getU1(), tex.getV1()).endVertex();
        bufferbuilder.vertex(matrix4f, vec[3].x(), vec[3].y(), vec[3].z()).color(1F, 1F, 1F, 1F).uv(tex.getU1(), tex.getV0()).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());

        poseStack.popPose();
    }

    private void renderOverlay(PoseStack poseStack) {
        if (selection.isPresent()) {
            var selectedFace = selection.get();
            BlockEntity entity = minecraft.level.getBlockEntity(selectedFace.blockPos);
            if (entity instanceof MachineBlockEntity machine) {
                var ioMode = machine.getIOConfig().getMode(selectedFace.side);
                IOModeMap map = IOModeMap.getMapFromMode(ioMode);
                Rect2i iconBounds = map.getRect();
                RenderSystem.setShaderTexture(0, IOConfigButton.IOCONFIG);
                blit(poseStack, x + 4, y + height - 4 - screenFont.lineHeight - iconBounds.getHeight(), iconBounds.getX(), iconBounds.getY(),
                    iconBounds.getWidth(), iconBounds.getHeight());
                poseStack.pushPose();
                screenFont.draw(poseStack, map.getComponent(), x + 4, y + height - 2 - screenFont.lineHeight, 0xFFFFFFFF);
                poseStack.popPose();
            }
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    public record SelectedFace(@NotNull BlockPos blockPos, @NotNull Direction side) {}

    private static class GhostBuffers extends MultiBufferSource.BufferSource {
        protected GhostBuffers(BufferBuilder fallback, Map<RenderType, BufferBuilder> layerBuffers) {
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
                    RenderSystem.setShaderColor(1, 1, 1, NEIGHBOUR_TRANSPARENCY);
                }, () -> {
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
