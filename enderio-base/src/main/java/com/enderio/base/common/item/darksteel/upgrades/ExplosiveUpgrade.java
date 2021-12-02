package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.base.common.capability.darksteel.DarkSteelUpgradeable;
import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExplosiveUpgrade implements IDarkSteelUpgrade {

    @SubscribeEvent
    public static void showAreaOfEffectHighlight(DrawSelectionEvent.HighlightBlock event) {

        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null || player.isCrouching()) {
            return;
        }
        DarkSteelUpgradeable
            .getUpgradeAs(player.getItemInHand(InteractionHand.MAIN_HAND), NAME, ExplosiveUpgrade.class)
            .ifPresent(explosiveUpgrade -> explosiveUpgrade.drawHighlight(event));
    }

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "explosive";

    private int aoeSize = 1;

    public ExplosiveUpgrade() {}

    private void drawHighlight(DrawSelectionEvent.HighlightBlock event) {
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null) {
            return;
        }
        BlockPos blockPos = event.getTarget().getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (blockState.isAir() || !level.getWorldBorder().isWithinBounds(blockPos)) {
            return;
        }

        AABB miningBounds = new AABB(-aoeSize,-aoeSize,-aoeSize,1 + aoeSize,1 + aoeSize,1 + aoeSize);

        //TODO: Do we want to use the 'Depth' upgrade for this?
        Direction targetDir = event.getTarget().getDirection();
        Vec3i shiftDir = targetDir.getNormal();
        shiftDir = shiftDir.multiply(-1);
        miningBounds = miningBounds.move(aoeSize * shiftDir.getX(), aoeSize * shiftDir.getY(),aoeSize * shiftDir.getZ());

        VoxelShape outlineShape = Shapes.create(miningBounds);

        VertexConsumer vertexConsumer = event.getBuffers().getBuffer(RenderType.lines());
        PoseStack poseStack = event.getMatrix();

        Vec3 camPos = event.getInfo().getPosition();
        Vector3d origin = new Vector3d(blockPos.getX() - camPos.x(), blockPos.getY() - camPos.y(), blockPos.getZ() - camPos.z());
        Vector4f color = new Vector4f(1,0,0,0.2f);

        renderShape(poseStack, vertexConsumer, outlineShape, origin, color);

        AABB refBounds = new AABB(0,0,0,1,1,1);
        color = new Vector4f(0,0,0,0.2f);
        renderJoiningLines(poseStack, vertexConsumer, refBounds, miningBounds, origin, color);
    }

    private void renderJoiningLines(PoseStack poseStack, VertexConsumer vertexConsumer, AABB refBounds, AABB miningBounds, Vector3d origin, Vector4f color) {
        var fromCorners = getCorners(refBounds);
        var toCorners = getCorners(miningBounds);
        for(int i=0;i<fromCorners.size();i++) {
            Vector3d from = fromCorners.get(i);
            Vector3d to = toCorners.get(i);
            addVertices(poseStack.last(), vertexConsumer, origin, from, to, color);
        }
    }

    private List<Vector3d> getCorners(AABB aabb) {
        List<Vector3d> res = new ArrayList<>(8);
        res.add(new Vector3d(aabb.minX, aabb.minY, aabb.minZ));
        res.add(new Vector3d(aabb.minX, aabb.maxY, aabb.minZ));
        res.add(new Vector3d(aabb.maxX, aabb.minY, aabb.minZ));
        res.add(new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ));
        res.add(new Vector3d(aabb.minX, aabb.minY, aabb.maxZ));
        res.add(new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ));
        res.add(new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ));
        res.add(new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
        return res;
    }

    private static void renderShape(PoseStack pPoseStack, VertexConsumer pConsumer, VoxelShape pShape, Vector3d origin, Vector4f color) {
        PoseStack.Pose pose = pPoseStack.last();
        pShape.forAllEdges(
            (pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ) -> addVertices(pose, pConsumer, origin.x, origin.y, origin.z, pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ,
                color));
    }

    private static void addVertices(PoseStack.Pose pose, VertexConsumer pConsumer, Vector3d origin, Vector3d from, Vector3d to, Vector4f color) {
        addVertices(pose,pConsumer, origin.x, origin.y, origin.z, from.x, from.y, from.z, to.x, to.y, to.z, color);
    }

    private static void addVertices(PoseStack.Pose pose, VertexConsumer pConsumer, double originX, double originY, double originZ,
        double fromX, double fromY, double fromZ, double toX, double toY, double toZ, Vector4f color) {

        //create normal
        float normalX = (float)(toX - fromX);
        float normalY = (float)(toY - fromY);
        float normalZ = (float)(toZ - fromZ);
        float length = Mth.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        //normalize
        normalX = normalX / length;
        normalY = normalY / length;
        normalZ = normalZ / length;

        pConsumer
            .vertex(pose.pose(), (float) (fromX + originX), (float) (fromY + originY), (float) (fromZ + originZ))
            .color(color.x(), color.y(), color.z(), color.w())
            .normal(pose.normal(), normalX, normalY, normalZ)
            .endVertex();
        pConsumer
            .vertex(pose.pose(), (float) (toX + originX), (float) (toY + originY), (float) (toZ + originZ))
            .color(color.x(), color.y(), color.z(), color.w())
            .normal(pose.normal(), normalX, normalY, normalZ)
            .endVertex();
    }

    @Override
    public String getSerializedName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        //TODO:
        return new TextComponent(NAME);
    }
}
