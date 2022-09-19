package com.enderio.base.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IIOConfigProvider;
import com.enderio.api.io.IOMode;
import com.enderio.core.client.RenderUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector4f;
import mcjty.theoneprobe.network.PacketHandler;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.common.Mod;
import org.codehaus.plexus.util.dag.Vertex;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class IOConfigRenderer<E extends BlockEntity & IIOConfigProvider> {

    public static final TextureSupplier selectedFaceIcon = TextureRegistry.registerTexture("blocks/overlays/selected_face");

    private boolean dragging = false;
    private float pitch = 0;
    private float yaw = 0;
    private double distance;
    private long initTime;

    private @Nonnull Minecraft mc = Minecraft.getInstance();
    private @Nonnull Level level = mc.player.level;

    private final @Nonnull Vector3d origin = new Vector3d(0, 0, 0);
    private final @Nonnull Vector3d eye = new Vector3d(0, 0, 0);
    private final @Nonnull Camera camera = new Camera();
    private final @Nonnull Matrix4f pitchRot = new Matrix4f();
    private final @Nonnull Matrix4f yawRot = new Matrix4f();

    private @Nonnull NonNullList<BlockPos> configurables = NonNullList.create();
    private @Nonnull NonNullList<BlockPos> neighbours = NonNullList.create();

    private SelectedFace<E> selection;

    private boolean renderNeighbours = true;
    private boolean inNeigButBounds = false;

    public IOConfigRenderer(@Nonnull IIOConfig configurable) {
        this(NonNullList(configurable.getLocation()));
    }

    public IOConfigRenderer(@Nonnull final NonNullList<BlockPos> configurables) {
        this.configurables.addAll(configurables);

        Vector3d c;
        Vector3d size;
        if (configurables.size() == 1) {
            BlockPos bc = configurables.get(0);
            c = new Vector3d(bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5);
            size = new Vector3d(1, 1, 1);
        } else {
            Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
            for (BlockPos bc : configurables) {
                min.set(Math.min(bc.getX(), min.x), Math.min(bc.getY(), min.y), Math.min(bc.getZ(), min.z));
                max.set(Math.max(bc.getX(), max.x), Math.max(bc.getY(), max.y), Math.max(bc.getZ(), max.z));
            }
            size = new Vector3d(0,0,0);
            size.set(max);
            min.scale(-1);
            size.add(min);
            size.scale(0.5);
            c = new Vector3d(min.x + size.x, min.y + size.y, min.z + size.z);
            size.scale(2);
        }

        origin.set(c);
        pitchRot.setIdentity();
        yawRot.setIdentity();

        pitch = -mc.player.getRotationVector().x;
        yaw = 180 - mc.player.getRotationVector().y;

        distance = Math.max(Math.max(size.x, size.y), size.z) + 4;

        configurables.forEach(pos -> Direction.stream().forEach(dir -> {
            BlockPos loc = pos.relative(dir);
            if (!configurables.contains(loc)) {
                neighbours.add(loc);
            }
        }));

        level = mc.player.level;
    }

    public void init() {
        initTime = System.currentTimeMillis();
    }

    public SelectedFace<E> getSelection() {
        return selection;
    }

    public void handleMouseInput() {

        if (Mouse.getEventButton() == 0) {
            dragging = Mouse.getEventButtonState();
        }

        if (dragging) {

            double dx = (Mouse.getEventDX() / (double) mc.displayWidth);
            double dy = (Mouse.getEventDY() / (double) mc.displayHeight);
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                distance -= dy * 15;
            } else {
                yaw -= 4 * dx * 180;
                pitch += 2 * dy * 180;
                pitch = (float) VecmathUtil.clamp(pitch, -80, 80);
            }
        }

        distance -= Mouse.getEventDWheel() * 0.01;
        distance = VecmathUtil.clamp(distance, 0.01, 200);

        long elapsed = System.currentTimeMillis() - initTime;

        // Mouse Over
        int x = Mouse.getEventX();
        int y = Mouse.getEventY();
        Vector3d start = new Vector3d();
        Vector3d end = new Vector3d();
        if (camera.getRayForPixel(x, y, start, end)) {
            end.scale(distance * 2);
            end.add(start);
            updateSelection(start, end);
        }

        // Mouse pressed on configurable side
        if (!Mouse.getEventButtonState() && camera.isValid() && elapsed > 500) {
            if (Mouse.getEventButton() == 1) {
                if (selection != null) {
                    selection.config.toggleIoModeForFace(selection.direction);
                    PacketHandler.INSTANCE.sendToServer(new PacketIoMode(selection.config, selection.direction));
                }
            } else if (Mouse.getEventButton() == 0 && inNeigButBounds) {
                renderNeighbours = !renderNeighbours;
            }
        }
    }


    private void updateSelection(@Nonnull final Vector3d start, @Nonnull final Vector3d end) {
        start.add(origin);
        end.add(origin);
        final List<RayTraceResult> hits = new ArrayList<RayTraceResult>();

        configurables.apply(new Callback<BlockPos>() {
            @Override
            public void apply(@Nonnull BlockPos pos) {
                if (!level.isAirBlock(pos)) {
                    IBlockState bs = level.getBlockState(pos);
                    RayTraceResult hit = bs.collisionRayTrace(level, pos, new Vec3d(start.x, start.y, start.z), new Vec3d(end.x, end.y, end.z));
                    if (NullHelper.untrust(hit) != null && hit.typeOfHit != RayTraceResult.Type.MISS) {
                        hits.add(hit);
                    }
                }
            }
        });

        selection = null;
        RayTraceResult hit = getClosestHit(new Vec3d(start.x, start.y, start.z), hits);
        if (hit != null) {
            BlockEntity te = level.getBlockEntity(hit.getBlockPos());
            if (te instanceof IIoConfigurable) {
                EnumFacing face = hit.sideHit;
                selection = new SelectedFace<E>((E) te, face);
            }
        }
    }

    public static HitResult getClosestHit(@Nonnull Vec3 origin, @Nonnull Collection<HitResult> candidates) {
        double minLengthSquared = Double.POSITIVE_INFINITY;
        HitResult closest = null;

        for (HitResult hit : candidates) {
            if (hit != null) {
                double lengthSquared = hit.getLocation().distanceToSqr(origin);
                if (lengthSquared < minLengthSquared) {
                    minLengthSquared = lengthSquared;
                    closest = hit;
                }
            }
        }
        return closest;
    }

    public void drawScreen(int par1, int par2, float partialTick, @Nonnull Rectangle vp, @Nonnull Rectangle parentBounds) {

        if (!updateCamera(partialTick, vp.x, vp.y, vp.width, vp.height)) {
            return;
        }

        applyCamera(partialTick);
        TravelController.setSelectionEnabled(false);
        renderScene();
        TravelController.setSelectionEnabled(true);
        renderSelection();
        renderOverlay(par1, par2);
    }

    private void renderSelection() {
        if (selection == null) {
            return;
        }

        BoundingBox bb = new BoundingBox(selection.config.getLocation());

        TextureAtlasSprite icon = selectedFaceIcon.get(TextureAtlasSprite.class);
        List<Vertex> corners = bb.getCornersWithUvForFace(selection.direction, icon.getMinU(), icon.getMaxU(), icon.getMinV(), icon.getMaxV());

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();

        RenderUtil.bindBlockTexture();
        BufferBuilder tes = Tessellator.getInstance().getBuffer();

        GlStateManager.color(1, 1, 1);
        Vector3d trans = new Vector3d((-origin.x) + eye.x, (-origin.y) + eye.y, (-origin.z) + eye.z);
        tes.setTranslation(trans.x, trans.y, trans.z);
        RenderUtil.addVerticesToTessellator(corners, DefaultVertexFormats.POSITION_TEX, true);
        Tessellator.getInstance().draw();
        tes.setTranslation(0, 0, 0);

    }

    private void renderOverlay(int mx, int my) {
        Rectangle vp = camera.getViewport();
        ScaledResolution scaledresolution = new ScaledResolution(mc);

        int vpx = vp.x / scaledresolution.getScaleFactor();
        int vph = vp.height / scaledresolution.getScaleFactor();
        int vpw = vp.width / scaledresolution.getScaleFactor();
        int vpy = (int) ((float) (vp.y + vp.height - 4) / (float) scaledresolution.getScaleFactor());

        GL11.glViewport(0, 0, mc.screen.width, mc.screen.height);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(vpx, vpy, -2000.0F);

        GlStateManager.disableLighting();

        int x = vpw - 17;
        int y = vph - 18;

        mx -= vpx;
        my -= vpy;

        if (mx >= x && mx <= x + IconEIO.IO_WHATSIT.width && my >= y && my <= y + IconEIO.IO_WHATSIT.height) {
            GlStateManager.enableBlend();
            RenderUtil.renderQuad2D(x, y, 0, IconEIO.IO_WHATSIT.width, IconEIO.IO_WHATSIT.height, new Vector4f(0.4f, 0.4f, 0.4f, 0.6f));
            GlStateManager.disableBlend();
            inNeigButBounds = true;
        } else {
            inNeigButBounds = false;
        }

        GL11.glColor3f(1, 1, 1);
        IconEIO.map.render(IconEIO.IO_WHATSIT, x, y, true);

        if (selection != null) {
            IconEIO ioIcon = null;
            int yOffset = 0;
            // INPUT
            IOMode mode = selection.config.getIoMode(selection.direction);
            if (mode == IoMode.PULL) {
                ioIcon = IconEIO.INPUT;
            } else if (mode == IoMode.PUSH) {
                ioIcon = IconEIO.OUTPUT;
            } else if (mode == IoMode.PUSH_PULL) {
                ioIcon = IconEIO.INPUT_OUTPUT;
            } else if (mode == IoMode.DISABLED) {
                ioIcon = IconEIO.DISABLED;
                yOffset = 5;
            }

            y = vph - mc.font.lineHeight - 2;
            mc.font.drawString(getLabelForMode(mode), 4, y, ColorUtil.getRGB(Color.white));
            if (ioIcon != null) {
                int w = mc.fontRenderer.getStringWidth(mode.getLocalisedName());
                double xd = (w - ioIcon.width) / 2;
                xd = Math.max(0, w);
                xd /= 2;
                xd += 4;
                xd /= scaledresolution.getScaleFactor();
                ioIcon.getMap().render(ioIcon, xd, y - mc.fontRenderer.FONT_HEIGHT - 2 - yOffset, true);
            }
        }
    }

    protected @Nonnull String getLabelForMode(@Nonnull IOMode mode) {
        return mode.getLocalisedName();
    }

    private void renderScene() {

        GlStateManager.enableCull();
        GlStateManager.enableRescaleNormal();

        RenderHelper.disableStandardItemLighting();
        mc.entityRenderer.disableLightmap();
        RenderUtil.bindBlockTexture();

        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();

        final Vector3d trans = new Vector3d((-origin.x) + eye.x, (-origin.y) + eye.y, (-origin.z) + eye.z);

        BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
        try {
            NNList.of(BlockRenderLayer.class).apply(new Callback<BlockRenderLayer>() {
                @Override
                public void apply(@Nonnull BlockRenderLayer layer) {
                    ForgeHooksClient.setRenderLayer(layer);
                    setGlStateForPass(layer, false);
                    doWorldRenderPass(trans, configurables, layer);
                }
            });

            if (renderNeighbours) {
                NNList.of(BlockRenderLayer.class).apply(new Callback<BlockRenderLayer>() {
                    @Override
                    public void apply(@Nonnull BlockRenderLayer layer) {
                        ForgeHooksClient.setRenderLayer(layer);
                        setGlStateForPass(layer, true);
                        doWorldRenderPass(trans, neighbours, layer);
                    }
                });
            }
        } finally {
            ForgeHooksClient.setRenderLayer(oldRenderLayer);
        }

        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();
        BlockEntityRenderDispatcher.instance.entityX = origin.x - eye.x;
        BlockEntityRenderDispatcher.instance.entityY = origin.y - eye.y;
        BlockEntityRenderDispatcher.instance.entityZ = origin.z - eye.z;
        BlockEntityRenderDispatcher.staticPlayerX = origin.x - eye.x;
        BlockEntityRenderDispatcher.staticPlayerY = origin.y - eye.y;
        BlockEntityRenderDispatcher.staticPlayerZ = origin.z - eye.z;

        for (int pass = 0; pass < 2; pass++) {
            ForgeHooksClient.setRenderPass(pass);
            setGlStateForPass(pass, false);
            doTileEntityRenderPass(configurables, pass);
            if (renderNeighbours) {
                setGlStateForPass(pass, true);
                doTileEntityRenderPass(neighbours, pass);
            }
        }
        ForgeHooksClient.setRenderPass(-1);
        setGlStateForPass(0, false);
    }

    private void doTileEntityRenderPass(@Nonnull NonNullList<BlockPos> blocks, final int pass) {
        blocks.apply(new Callback<BlockPos>() {
            @Override
            public void apply(@Nonnull BlockPos pos) {
                TileEntity tile = level.getTileEntity(pos);
                if (tile != null) {
                    if (tile.shouldRenderInPass(pass)) {
                        Vector3d at = new Vector3d(eye.x, eye.y, eye.z);
                        at.x += pos.getX() - origin.x;
                        at.y += pos.getY() - origin.y;
                        at.z += pos.getZ() - origin.z;
                        if (tile.getClass() == TileEntityChest.class) {
                            TileEntityChest chest = (TileEntityChest) tile;
                            if (NullHelper.untrust(chest.adjacentChestXNeg) != null) {
                                tile = chest.adjacentChestXNeg;
                                at.x--;
                            } else if (NullHelper.untrust(chest.adjacentChestZNeg) != null) {
                                tile = chest.adjacentChestZNeg;
                                at.z--;
                            }
                        }
                        TileEntityRendererDispatcher.instance.render(tile, at.x, at.y, at.z, 0);
                    }
                }
            }
        });
    }

    private void doWorldRenderPass(@Nonnull Vector3d trans, @Nonnull NonNullList<BlockPos> blocks, final @Nonnull BlockRenderLayer layer) {

        BufferBuilder wr = Tessellator.getInstance().getBuffer();
        wr.begin(7, DefaultVertexFormats.BLOCK);

        Tessellator.getInstance().getBuffer().setTranslation(trans.x, trans.y, trans.z);

        blocks.apply(new Callback<BlockPos>() {
            @Override
            public void apply(@Nonnull BlockPos pos) {

                BlockState bs = level.getBlockState(pos);
                Block block = bs.getBlock();
                bs = bs.getActualState(level, pos);
                if (block.canRenderInLayer(bs, layer)) {
                    renderBlock(bs, pos, level, Tessellator.getInstance().getBuffer());
                }
            }
        });

        Tessellator.getInstance().draw();
        Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
    }

    public void renderBlock(@Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull BlockDataAccessor blockAccess, @Nonnull BufferBuilder worldRendererIn) {

        try {
            BlockRenderDispatcher blockrendererdispatcher = mc.getBlockRenderer();
            EnumBlockRenderType type = state.getRenderType();
            if (type != EnumBlockRenderType.MODEL) {
                blockrendererdispatcher.renderBlock(state, pos, blockAccess, worldRendererIn);
                return;
            }

            // We only want to change one param here, the check sides
            BakedModel ibakedmodel = blockrendererdispatcher.getModelForState(state);
            state = state.getBlock().getExtendedState(state, level, pos);
            blockrendererdispatcher.getBlockModelRenderer().renderModel(blockAccess, ibakedmodel, state, pos, worldRendererIn, false);

        } catch (Throwable throwable) {
            // Just bury a render issue here, it is only the IO screen
        }
    }

    private void setGlStateForPass(@Nonnull BlockRenderLayer layer, boolean isNeighbour) {
        int pass = layer == BlockRenderLayer.TRANSLUCENT ? 1 : 0;
        setGlStateForPass(pass, isNeighbour);
    }

    private void setGlStateForPass(int layer, boolean isNeighbour) {

        GlStateManager.color(1, 1, 1);
        if (isNeighbour) {

            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            float alpha = 1f;
            float col = 1f;

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR);
            GL14.glBlendColor(col, col, col, alpha);
            return;
        }

        if (layer == 0) {
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
        } else {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(false);

        }

    }

    private boolean updateCamera(float partialTick, int vpx, int vpy, int vpw, int vph) {
        if (vpw <= 0 || vph <= 0) {
            return false;
        }
        camera.setViewport(vpx, vpy, vpw, vph);
        camera.setProjectionMatrixAsPerspective(30, 0.05, 50, vpw, vph);
        eye.set(0, 0, distance);
        pitchRot.makeRotationX(Math.toRadians(pitch));
        yawRot.makeRotationY(Math.toRadians(yaw));
        pitchRot.transform(eye);
        yawRot.transform(eye);
        camera.setViewMatrixAsLookAt(eye, RenderUtil.ZERO_V, RenderUtil.UP_V);
        return camera.isValid();
    }

    private void applyCamera(float partialTick) {
        Rectangle vp = camera.getViewport();
        GL11.glViewport(vp.x, vp.y, vp.width, vp.height);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        final Matrix4f camaraViewMatrix = camera.getTransposeProjectionMatrix();
        if (camaraViewMatrix != null) {
            RenderUtil.loadMatrix(camaraViewMatrix);
        }
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        final Matrix4f cameraViewMatrix = camera.getTransposeViewMatrix();
        if (cameraViewMatrix != null) {
            RenderUtil.loadMatrix(cameraViewMatrix);
        }
        GL11.glTranslatef(-(float) eye.x, -(float) eye.y, -(float) eye.z);
    }

    public static class SelectedFace<E extends BlockEntity & IIOConfigProvider> {

        public final @Nonnull E config;
        public final @Nonnull Direction direction;

        public SelectedFace(@Nonnull E config, @Nonnull Direction direction) {
            this.config = config;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "SelectedFace [config=" + config + ", face=" + direction + "]";
        }

    }

}