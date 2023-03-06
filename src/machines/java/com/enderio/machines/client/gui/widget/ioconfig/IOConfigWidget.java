package com.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.EnderIO;
import com.enderio.core.client.RenderUtil;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Thanks XFactHD for help and providing a demo a preview widget and raycast
 * <a href="https://gist.github.com/XFactHD/4b214f98a1b30a590c6e0de6bd84602a">Preview Widget Gist</a>
 */
public class IOConfigWidget<U extends EIOScreen<?>> extends AbstractWidget {

    private static final float SCALE = 30;
    private static final Quaternion ROT_180_Z = Vector3f.ZP.rotation((float) Math.PI);
    private static final Vec3 RAY_ORIGIN = new Vec3(1.5, 1.5, 1.5);
    private static final Vec3 START = new Vec3(1.5, 1.5, -1);
    private static final Vec3 END = new Vec3(1.5, 1.5, 3);
    private static final BlockPos POS = new BlockPos(1, 1, 1);
    private static final int Z_OFFSET = 100;
    private static final ResourceLocation SELECTED_ICON = EnderIO.loc("block/overlay/selected_face");
    private static final int NEIGHBOUR_TRANSPARENCY = 100; // range 0-255
    private static final Minecraft minecraft = Minecraft.getInstance();
    private final U addedOn;
    private final @NotNull Vector3f world_origin;
    private final Vector3f multiblock_size;
    private final List<BlockPos> configurable = new ArrayList<>();
    private final List<BlockPos> neighbours = new ArrayList<>();
    // Camera Variables
    private float pitch;
    private float yaw;
    private @Nullable SelectedFace selection;

    public IOConfigWidget(U addedOn, int x, int y, int width, int height, BlockPos configurable) {
        this(addedOn, x, y, width, height, List.of(configurable));
    }

    public IOConfigWidget(U addedOn, int x, int y, int width, int height, List<BlockPos> _configurable) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.addedOn = addedOn;
        this.configurable.addAll(_configurable);

        Vector3f c;
        if (configurable.size() == 1) {
            BlockPos bc = configurable.get(0);
            c = new Vector3f(bc.getX() + 0.5f, bc.getY() + 0.5f, bc.getZ() + 0.5f);
            multiblock_size = new Vector3f(1, 1, 1);
        } else {
            Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
            Vector3f max = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
            for (BlockPos bc : configurable) {
                min.set(Math.min(bc.getX(), min.x()), Math.min(bc.getY(), min.y()), Math.min(bc.getZ(), min.z()));
                max.set(Math.max(bc.getX(), max.x()), Math.max(bc.getY(), max.y()), Math.max(bc.getZ(), max.z()));
            }
            multiblock_size = max;
            multiblock_size.sub(min);
            multiblock_size.mul(0.5f);
            c = new Vector3f(min.x() + multiblock_size.x(), min.y() + multiblock_size.y(), min.z() + multiblock_size.z());
            multiblock_size.mul(2);
        }

        world_origin = new Vector3f(c.x(), c.y(), c.z());

        var distance = Math.max(Math.max(multiblock_size.x(), multiblock_size.y()), multiblock_size.z()) + 4;

        configurable.forEach(pos -> {
            for (Direction dir : Direction.values()) {
                BlockPos loc = pos.offset(dir.getNormal());
                if (!configurable.contains(loc)) {
                    neighbours.add(loc);
                }
            }

        });
        pitch = 30;
        yaw = 30;
    }

    private static BlockHitResult raycast(BlockState state, float diffX, float diffY, Matrix4f transform) {
        // Add mouse offset to start and end vectors
        Vec3 start = START.add(diffX, diffY, 0);
        Vec3 end = END.add(diffX, diffY, 0);

        // Rotate start and end vectors around the block
        start = transform(start, transform);
        end = transform(end, transform);

        // Get block's shape and cast a ray through it
        VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        return shape.clip(start, end, POS);
    }

    private static Vec3 transform(Vec3 vec, Matrix4f transform) {
        // Move vector to a (0,0,0) origin as the transformation matrix expects
        Vector4f vec4 = new Vector4f((float) (vec.x - RAY_ORIGIN.x), (float) (vec.y - RAY_ORIGIN.y), (float) (vec.z - RAY_ORIGIN.z), 1F);
        // Apply the transformation matrix
        vec4.transform(transform);
        // Move transformed vector back to the actual origin
        return new Vec3(vec4.x() + RAY_ORIGIN.x, vec4.y() + RAY_ORIGIN.y, vec4.z() + RAY_ORIGIN.z);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (visible) {
            // render black bg
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
            renderWorld(poseStack, centerX, centerY, blockTransform);

            // Build ray transformation matrix
            // Rotate 180 around Z, otherwise the block is upside down
            Matrix4f rayTransform = new Matrix4f(ROT_180_Z);
            // Rotate around Y (yaw)
            rayTransform.multiply(rotYaw);
            // Rotate around X (pitch) in negative direction
            rayTransform.multiply(rotPitch);

            // Ray-cast hit on block shape
            List<BlockHitResult> hits = new ArrayList<>();
            configurable.forEach(blockPos -> {
                BlockState state = minecraft.level.getBlockState(blockPos);
                BlockHitResult hit = raycast(state, diffX, diffY, rayTransform);
                if (hit != null && hit.getType() != HitResult.Type.MISS) {
                    hits.add(hit);
                }
            });

            Vec3 _origin = new Vec3(world_origin);
            Optional<BlockHitResult> opt = hits
                .stream()
                .min((a, b) -> (int) (a.getBlockPos().distToCenterSqr(_origin) - b.getBlockPos().distToCenterSqr(_origin))); // minimum

            if (opt.isPresent()) {
                BlockHitResult closest = opt.get();
                Direction face = closest.getDirection();
                selection = new SelectedFace(closest.getBlockPos(), face);
            }

            //                renderSelection(pPoseStack);
            //        renderOverlay();

        }
    }

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        double dx = pDragX / (double) addedOn.width;
        double dy = pDragY / (double) addedOn.height;
        yaw += 4 * dx * 180;
        pitch += 2 * dy * 180;

        pitch = Math.min(80, Math.max(-80, pitch)); //clamp
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (visible) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return false;
    }

    private void renderSelection(PoseStack poseStack) {
        if (selection == null) {
            return;
        }
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();

        // do I need a texture atlas ?
        TextureAtlasSprite tex = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SELECTED_ICON);
        RenderSystem.setShaderTexture(0, tex.atlas().location());

        poseStack.pushPose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        var blockPos = selection.block;
        poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        RenderUtil.getVerticesForFace(bufferbuilder, selection.face, new AABB(selection.block), tex.getU0(), tex.getU1(), tex.getV0(), tex.getV1());
        BufferUploader.drawWithShader(bufferbuilder.end());
        poseStack.popPose();

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }

    private void renderWorld(PoseStack poseStack, int centerX, int centerY, Quaternion transform) {
        Lighting.setupForEntityInInventory();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, Z_OFFSET);
        poseStack.scale(SCALE, SCALE, -SCALE);
        poseStack.mulPose(transform);

        // Render configurable
        //TODO: recheck when capacitor banks
        for (var configurable : configurable) {
            Vector3f pos = new Vector3f(configurable.getX() - world_origin.x(), configurable.getY() - world_origin.y(), configurable.getZ() - world_origin.z());
            renderBlock(poseStack, configurable, pos, buffers);
        }

        //        // RenderNeighbours
        for (var neighbour : neighbours) {
            Vector3f pos = new Vector3f(neighbour.getX() - world_origin.x(), neighbour.getY() - world_origin.y(), neighbour.getZ() - world_origin.z());
            renderBlockWithAlpha(poseStack, neighbour, pos, buffers);
        }
        buffers.endBatch();

        poseStack.popPose();
    }

    private void renderBlock(PoseStack poseStack, BlockPos blockPos, Vector3f renderPos, MultiBufferSource.BufferSource buffers) {
        poseStack.pushPose();
        poseStack.translate(renderPos.x(), renderPos.y(), renderPos.z());
        BlockState blockState = minecraft.level.getBlockState(blockPos);
        minecraft
            .getBlockRenderer()
            .renderSingleBlock(blockState, poseStack, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        poseStack.popPose();

    }

    private void renderBlockWithAlpha(PoseStack poseStack, BlockPos blockPos, Vector3f renderPos, MultiBufferSource.BufferSource buffers) {
        poseStack.pushPose();
        poseStack.translate(renderPos.x(), renderPos.y(), renderPos.z());
        BlockState blockState = minecraft.level.getBlockState(blockPos);
        BlockRenderDispatcher renderer = minecraft.getBlockRenderer();

        BakedModel bakedModel = renderer.getBlockModel(blockState);
        RenderType renderType = Minecraft.useShaderTransparency() ? Sheets.translucentItemSheet() : Sheets.translucentCullBlockSheet();
        TransparentVertexConsumer vertexConsumer = new TransparentVertexConsumer(buffers.getBuffer(renderType), NEIGHBOUR_TRANSPARENCY);
        int blockColor = minecraft.getBlockColors().getColor(blockState, minecraft.level, blockPos, 0);
        float r = FastColor.ARGB32.red(blockColor) / 255F;
        float g = FastColor.ARGB32.green(blockColor) / 255F;
        float b = FastColor.ARGB32.blue(blockColor) / 255F;

        renderer
            .getModelRenderer()
            .renderModel(poseStack.last(), vertexConsumer, blockState, bakedModel, r, g, b, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY, renderType);
        poseStack.popPose();
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    public record SelectedFace(@NotNull BlockPos block, @NotNull Direction face) {}

}
