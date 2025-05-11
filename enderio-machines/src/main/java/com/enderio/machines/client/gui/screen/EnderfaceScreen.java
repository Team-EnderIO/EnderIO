package com.enderio.machines.client.gui.screen;

import com.enderio.machines.common.blocks.enderface.EnderfaceBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.network.EnderfaceInteractPacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class EnderfaceScreen extends Screen {
    private static final Quaternionf ROT_180_Z = Axis.ZP.rotation((float) Math.PI);
    private static final Object2ObjectOpenHashMap<RenderType, ByteBufferBuilder> ENDERFACE_BUFFERS = new Object2ObjectOpenHashMap<>();
    private static final List<RenderType> LAYERS_BEFORE_BLOCK_ENTITIES = RenderType.chunkBufferLayers()
            .stream()
            .filter(l -> l != RenderType.translucent())
            .toList();
    private static final List<RenderType> LAYERS_AFTER_BLOCK_ENTITIES = List.of(RenderType.translucent());
    private static final boolean DEBUG_SELECTION_POSITION = false;
    private static final Vec3 RAY_ORIGIN = new Vec3(1.5, 1.5, 1.5);
    private static final Vec3 RAY_START = new Vec3(1.5, 1.5, -1);
    private static final Vec3 RAY_END = new Vec3(1.5, 1.5, 3);
    private static final BlockPos POS = new BlockPos(1, 1, 1);

    private final BlockPos enderfacePos;
    private final ClientLevel world;

    private float pitch = -45;
    private float yaw = 45;

    private boolean chunkLoaded;

    private int gw;
    private int gh;
    private int guiLeft;
    private int guiTop;
    private int finalGw;
    private int finalGh;
    // private boolean animateInX = true;
    private boolean animateInX = false;
    private boolean animateInY = false;

    private final Vector3f origin = new Vector3f();

    private final int range;

    private float distance;

    private long initTime;

    private BlockPos selectedPos;
    private Direction selectedSide;
    private Vec3 selectedLocation;

    private Vec3 eyePosition;

    private final Object2ObjectOpenHashMap<RenderType, BufferBuilder> worldBufferBuilders = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<RenderType, VertexBuffer> worldGeometry = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<RenderType, MeshData.SortState> worldSortStates = new Object2ObjectOpenHashMap<>();

    private boolean worldNeedsRebuild = true;
    private final List<BlockEntity> worldBlockEntities = new ArrayList<>();

    public EnderfaceScreen(BlockPos pos, ClientLevel world) {
        super(Component.literal("Ender IO"));
        this.enderfacePos = pos;
        this.world = world;

        range = MachinesConfig.COMMON.ENDERFACE_RANGE.getAsInt();
        distance = 20;

        BlockEntity blockEntity = world.getBlockEntity(enderfacePos);

        if (blockEntity instanceof EnderfaceBlockEntity enderface) {
            pitch = enderface.getLastUiPitch();
            yaw = enderface.getLastUiYaw();
            distance = enderface.getLastUiDistance();
        }

        origin.set(enderfacePos.getX() + 0.5, enderfacePos.getY() + 0.5, enderfacePos.getZ() + 0.5);

        chunkLoaded = world.isLoaded(enderfacePos);
    }

    @Override
    public void onClose() {
        super.onClose();
        BlockEntity te = world.getBlockEntity(enderfacePos);
        if (te instanceof EnderfaceBlockEntity enderface) {
            enderface.setLastUiPitch(pitch);
            enderface.setLastUiYaw(yaw);
            enderface.setLastUiDistance(distance);
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void init() {
        finalGw = width * 1;
        finalGh = height * 1;
        // gw = finalGw - 1;
        // gh = finalGh - 1;
        gw = finalGw;
        gh = finalGh;
        // gw = 0;
        // gh = 0;
        guiLeft = (width - gw) / 2;
        guiTop = (height - gh) / 2;

        initTime = world.getGameTime();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double pDragX, double pDragY) {
        long window = Minecraft.getInstance().getWindow().getWindow();

        double dx = pDragX / (double) Minecraft.getInstance().getWindow().getGuiScaledWidth();
        double dy = pDragY / (double) Minecraft.getInstance().getWindow().getGuiScaledHeight();

        if (InputConstants.isKeyDown(window, InputConstants.KEY_LCONTROL)
                || InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT)) {
            distance -= dy * 15;
        } else {
            yaw -= dx * 180;
            pitch += dy * 180;
            pitch = (float) Mth.clamp(pitch, -80, 80);
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        distance += deltaY;
        distance = Math.min(40, Math.max(10, distance)); // clamp
        return true;
    }

    private Iterable<BlockPos> getShownPositions() {
        return BlockPos.betweenClosed(enderfacePos.offset(-range, -range, -range),
                enderfacePos.offset(range, range, range));
    }

    private BufferBuilder getBuilderForLayer(RenderType renderType) {
        var builder = worldBufferBuilders.get(renderType);

        if (builder != null) {
            return builder;
        }

        var byteBuilder = ENDERFACE_BUFFERS.computeIfAbsent(renderType, k -> new ByteBufferBuilder(2097152));
        builder = new BufferBuilder(byteBuilder, renderType.mode(), renderType.format());
        worldBufferBuilders.put(renderType, builder);
        return builder;
    }

    private void uploadCompiledLayer(RenderType type) {
        var builder = worldBufferBuilders.get(type);
        MeshData meshdata = builder != null ? builder.build() : null;
        if (meshdata != null) {
            if (type.sortOnUpload()) {
                var sortState = meshdata.sortQuads(ENDERFACE_BUFFERS.get(type), RenderSystem.getVertexSorting());

                worldSortStates.put(type, sortState);
            }

            VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            buffer.bind();
            buffer.upload(meshdata);

            worldGeometry.put(type, buffer);
        }
    }

    private void renderCompiledLayer(PoseStack.Pose pose, RenderType type) {
        var vertexBuffer = worldGeometry.get(type);

        if (vertexBuffer != null) {
            var sortState = worldSortStates.get(type);
            vertexBuffer.bind();

            if (sortState != null) {
                // Resort
                // TODO not working properly, eye position is probably wrong
                var byteBuilder = ENDERFACE_BUFFERS.get(type);
                var indexBuffer = sortState.buildSortedIndexBuffer(byteBuilder,
                        VertexSorting.byDistance((float) eyePosition.x, (float) eyePosition.y, (float) eyePosition.z));
                vertexBuffer.uploadIndexBuffer(indexBuffer);
                byteBuilder.clear();
            }

            type.setupRenderState();
            var chunkOffset = RenderSystem.getShader().CHUNK_OFFSET;
            chunkOffset.set((float) 0, (float) 0, (float) 0);
            var modelView = new Matrix4f(RenderSystem.getModelViewMatrix());
            modelView.mul(pose.pose());
            vertexBuffer.drawWithShader(modelView, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
            type.clearRenderState();
        }
    }

    private Quaternionf getGuiWorldTransform() {
        Quaternionf rotPitch = Axis.XN.rotationDegrees(pitch);
        Quaternionf rotYaw = Axis.YP.rotationDegrees(yaw);

        // Build block transformation matrix
        // Rotate 180 around Z, otherwise the block is upside down
        Quaternionf blockTransform = new Quaternionf(ROT_180_Z);
        // Rotate around X (pitch) in negative direction
        blockTransform.mul(rotPitch);
        // Rotate around Y (yaw)
        blockTransform.mul(rotYaw);

        return blockTransform;
    }

    public void setWorldNeedsRebuild() {
        this.worldNeedsRebuild = true;
    }

    private void renderWorld(GuiGraphics graphics, float partialTick) {
        Quaternionf blockTransform = getGuiWorldTransform();

        // Flush out all previously rendered GUI content
        graphics.flush();

        graphics.pose().pushPose();
        graphics.pose().translate(width / 2f, height / 2f, 1000);
        graphics.pose().scale(distance, distance, -distance);
        graphics.pose().mulPose(blockTransform);

        var dispatcher = Minecraft.getInstance().getBlockRenderer();
        var randomSource = new SingleThreadedRandomSource(42L);

        var blockEntityDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();

        if (worldNeedsRebuild) {
            worldGeometry.values().forEach(VertexBuffer::close);
            worldGeometry.clear();
            worldBlockEntities.clear();

            var pose = new PoseStack();

            getShownPositions().forEach(pos -> {
                var state = world.getBlockState(pos);

                if (state.getRenderShape() != RenderShape.INVISIBLE) {
                    randomSource.setSeed(42L);

                    var model = dispatcher.getBlockModel(state);
                    var data = model.getModelData(world, pos, state, world.getModelData(pos));
                    for (RenderType type : model.getRenderTypes(state, randomSource, data)) {
                        randomSource.setSeed(42L);
                        var buffer = getBuilderForLayer(type);
                        pose.pushPose();
                        pose.translate(pos.getX() - origin.x, pos.getY() - origin.y, pos.getZ() - origin.z);
                        dispatcher.renderBatched(state, pos, world, pose, buffer, true, randomSource, data, type);
                        pose.popPose();
                    }
                }

                var fluidState = state.getFluidState();

                if (!fluidState.isEmpty()) {
                    float xTransform = (pos.getX() & 15);
                    float yTransform = (pos.getY() & 15);
                    float zTransform = (pos.getZ() & 15);
                    pose.pushPose();
                    pose.translate(pos.getX() - origin.x, pos.getY() - origin.y, pos.getZ() - origin.z);
                    var offset = pose.last();
                    // Use wrapper to make fluid rendering conform to PoseStack matrices
                    var buffer = new VertexConsumerWrapper(
                            getBuilderForLayer(ItemBlockRenderTypes.getRenderLayer(fluidState))) {
                        @Override
                        public VertexConsumer addVertex(float x, float y, float z) {
                            parent.addVertex(offset, x - xTransform, y - yTransform, z - zTransform);
                            return this;
                        }
                    };
                    dispatcher.renderLiquid(pos, world, buffer, state, fluidState);
                    pose.popPose();
                }

                var blockEntity = world.getBlockEntity(pos);

                if (blockEntity != null && blockEntityDispatcher.getRenderer(blockEntity) != null) {
                    worldBlockEntities.add(blockEntity);
                }
            });

            worldBufferBuilders.keySet().forEach(this::uploadCompiledLayer);
            worldBufferBuilders.clear();

            ENDERFACE_BUFFERS.values().forEach(ByteBufferBuilder::clear);

            worldNeedsRebuild = false;
        }

        for (var layer : LAYERS_BEFORE_BLOCK_ENTITIES) {
            this.renderCompiledLayer(graphics.pose().last(), layer);
        }

        VertexBuffer.unbind();

        for (var blockEntity : worldBlockEntities) {
            var renderer = blockEntityDispatcher.getRenderer(blockEntity);
            if (renderer != null) {
                var pos = blockEntity.getBlockPos();
                graphics.pose().pushPose();
                graphics.pose().translate(pos.getX() - origin.x, pos.getY() - origin.y, pos.getZ() - origin.z);
                blockEntityDispatcher.render(blockEntity, partialTick, graphics.pose(), graphics.bufferSource());
                graphics.pose().popPose();
            }
        }

        // Force block entities to be flushed
        graphics.bufferSource().endBatch();

        for (var layer : LAYERS_AFTER_BLOCK_ENTITIES) {
            this.renderCompiledLayer(graphics.pose().last(), layer);
        }

        if (DEBUG_SELECTION_POSITION && selectedLocation != null) {
            graphics.pose().pushPose();
            graphics.pose()
                    .translate(selectedLocation.x - origin.x, selectedLocation.y - origin.y,
                            selectedLocation.z - origin.z);
            graphics.pose().scale(0.3f, 0.3f, 0.3f);
            graphics.pose().translate(-0.5, -0.5, -0.5);
            dispatcher.renderSingleBlock(Blocks.OAK_PLANKS.defaultBlockState(), graphics.pose(),
                    graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            graphics.bufferSource().endBatch();
            graphics.pose().popPose();
        }

        graphics.pose().popPose();
    }

    private static Vec3 transform(Vec3 vec, Matrix4f transform) {
        // Move vector to a (0,0,0) origin as the transformation matrix expects
        Vector4f vec4 = new Vector4f((float) (vec.x - RAY_ORIGIN.x), (float) (vec.y - RAY_ORIGIN.y),
                (float) (vec.z - RAY_ORIGIN.z), 1F);
        // Apply the transformation matrix
        vec4.mul(transform);
        // Move transformed vector back to the actual origin
        return new Vec3(vec4.x() + RAY_ORIGIN.x, vec4.y() + RAY_ORIGIN.y, vec4.z() + RAY_ORIGIN.z);
    }

    @Nullable
    private BlockHitResult raycast(BlockPos pos, float diffX, float diffY, Matrix4f transform) {
        var state = world.getBlockState(pos);
        // Get block's shape and cast a ray through it
        VoxelShape shape = state.getShape(world, pos);
        if (shape == Shapes.empty()) {
            return null;
        }

        // Add mouse offset to start and end vectors
        Vec3 start = RAY_START.add(diffX, diffY, 0);
        Vec3 end = RAY_END.add(diffX, diffY, 0);

        // Rotate start and end vectors around the block
        start = transform(start, transform);
        end = transform(end, transform);

        Vector3f centerPos = new Vector3f(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).sub(origin);
        shape = shape.move(centerPos.x(), centerPos.y(), centerPos.z());
        return shape.clip(start, end, POS);
    }

    private record HitCandidate(BlockHitResult hitResult, BlockPos realHitPos, Vec3 locationOffset) {
    }

    private void updateCamera(int mouseX, int mouseY) {
        Quaternionf rotPitch = Axis.XN.rotationDegrees(pitch);
        Quaternionf rotYaw = Axis.YP.rotationDegrees(yaw);

        // Build ray transformation matrix
        // Rotate 180 around Z, otherwise the block is upside down
        Matrix4f rayTransform = new Matrix4f();
        rayTransform.set(ROT_180_Z);
        // Rotate around Y (yaw)
        rayTransform.rotate(rotYaw);
        // Rotate around X (pitch) in negative direction
        rayTransform.rotate(rotPitch);

        // Calculate mouse offset from center and scale to the block space
        int centerX = width / 2, centerY = height / 2;

        float diffX = (mouseX - centerX) / distance;
        float diffY = (mouseY - centerY) / distance;

        List<HitCandidate> candidates = new ArrayList<>();

        getShownPositions().forEach(pos -> {
            var hitResult = raycast(pos, diffX, diffY, rayTransform);

            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                candidates.add(new HitCandidate(hitResult, pos.immutable(),
                        hitResult.getLocation().subtract(POS.getX(), POS.getY(), POS.getZ())));
            }
        });

        eyePosition = transform(RAY_START, rayTransform).add(origin.x, origin.y, origin.z);

        var candidate = candidates.stream()
                .min(Comparator.comparingDouble(entry -> entry.realHitPos().distToCenterSqr(eyePosition)))
                .orElse(null);

        if (candidate != null) {
            selectedPos = candidate.realHitPos();
            selectedSide = candidate.hitResult.getDirection();
            selectedLocation = candidate.locationOffset();
        } else {
            selectedLocation = null;
            selectedPos = null;
            selectedSide = null;
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(GuiGraphics graphics, int par1, int par2, float partialTick) {
        renderBackground(graphics, par1, par2, partialTick);

        drawEnderfaceBackground(graphics);

        if (!animateInX && !animateInY) {

            if (chunkLoaded) {
                updateCamera(par1, par2);
                renderWorld(graphics, partialTick);
            } else {
                graphics.drawCenteredString(Minecraft.getInstance().font, "EnderIO chunk not loaded.", width / 2,
                        height / 2 - 32, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == InputConstants.MOUSE_BUTTON_RIGHT && selectedPos != null) {
            if (DEBUG_SELECTION_POSITION) {
                System.out.println(selectedPos);
                System.out.println(selectedSide);
                System.out.println(selectedLocation);
                System.out.println(world.getBlockState(selectedPos));
            }
            // Open menu here
            PacketDistributor.sendToServer(new EnderfaceInteractPacket(
                    new BlockHitResult(selectedLocation, selectedSide, selectedPos, false)));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private float portalFade = 1;

    private void drawEnderfaceBackground(GuiGraphics graphics) {

        int w = gw;
        int h = gh;
        int left = guiLeft;
        int top = guiTop;
        int cx = left + w / 2;
        int cy = top + h / 2;

        // black outline
        graphics.fill(left, top, left + w, top + h, 0xFF000000);
        left += 1;
        top += 1;
        w -= 2;
        h -= 2;

        // border
        int topH = 0xFFFFFFFF;
        int botH = 0xFF555555;
        int rightH = 0xFF555555;
        int leftH = 0xFFFFFFFF;
        if (animateInX) {
            leftH = 0xFF555555;
            rightH = 0xFFFFFFFF;
        }
        if (animateInY) {
            topH = 0xFF555555;
            botH = 0xFFFFFFFF;
        }

        left += 1;
        top += 1;
        w -= 2;
        h -= 2;
        graphics.fill(left, top, left + w, top + h, 0xFF00331C);
    }
}
