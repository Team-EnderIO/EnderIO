package com.enderio.base.common.item.darksteel.upgrades.explosive;

import com.enderio.base.common.capability.DarkSteelUpgradeable;
import com.enderio.base.common.init.EIOPackets;
import com.enderio.base.common.item.darksteel.upgrades.SpoonUpgrade;
import com.enderio.base.common.network.packet.EmitParticlesPacket;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.BlockUtil;
import com.enderio.base.common.util.EnergyUtil;
import com.enderio.base.config.base.BaseConfig;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExplosiveUpgradeHandler {

    private static final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_BREAK_POWER_USE = BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_ENERGY_PER_EXPLODED_BLOCK;

    private static final Random RAND = new Random();

    public static boolean hasExplosiveUpgrades(ItemStack stack) {
        return DarkSteelUpgradeable.hasUpgrade(stack, ExplosiveUpgrade.NAME) || DarkSteelUpgradeable.hasUpgrade(stack, ExplosivePenetrationUpgrade.NAME);
    }

    public static void onMineBlock(ItemStack pStack, Level pLevel, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pEntityLiving instanceof Player player && !player.isCrouching() && hasExplosiveUpgrades(pStack) && EnergyUtil.getEnergyStored(pStack) > 0) {
            BlockHitResult hit = Item.getPlayerPOVHitResult(pLevel, player, ClipContext.Fluid.NONE);
            if (pPos.equals(hit.getBlockPos())) {
                EmitParticlesPacket particles = new EmitParticlesPacket();
                if (explodeArea(pStack, pLevel, player, hit, particles)) {
                    EIOPackets.getNetwork().getNetworkChannel().send(PacketDistributor.TRACKING_CHUNK.with(() -> pLevel.getChunkAt(pPos)), particles);
                }
            }
        }
    }

    private static boolean explodeArea(ItemStack pStack, Level pLevel, Player player, BlockHitResult hit, EmitParticlesPacket particles) {
        boolean didExplode = false;
        AABB bb = calculateMiningArea(pStack, hit.getDirection());
        bb = bb.move(hit.getBlockPos());
        for (BlockPos minePos : BlockPos.betweenClosed((int) bb.minX, (int) bb.minY, (int) bb.minZ,
            (int) bb.maxX - 1, (int) bb.maxY - 1, (int) bb.maxZ - 1)) {
            if (!hit.getBlockPos().equals(minePos)) {
                didExplode = explodeBlock(pStack, pLevel, minePos, player, particles) || didExplode;
            }
        }
        return didExplode;
    }

    private static boolean explodeBlock(ItemStack itemStack, Level level, BlockPos minePos, Player player, EmitParticlesPacket particles) {
        if (!level.isInWorldBounds(minePos) || EnergyUtil.getEnergyStored(itemStack) <= 0) {
            return false;
        }
        BlockState blockState = level.getBlockState(minePos);
        if (!canExplode(itemStack, blockState, level.getBlockEntity(minePos))) {
            return false;
        }
        if (BlockUtil.removeBlock(level, player, itemStack, minePos)) {
            EnergyUtil.extractEnergy(itemStack, EXPLOSIVE_BREAK_POWER_USE.get(),false);
            if (RAND.nextFloat() < .3f) {
                particles.add(minePos, ParticleTypes.LARGE_SMOKE);
            } else if (RAND.nextFloat() < .7f) {
                particles.add(minePos, ParticleTypes.SMOKE);
            }
            return true;
        }
        return false;
    }

    private static boolean canExplode(ItemStack itemStack, BlockState blockState, @Nullable BlockEntity blockEntity) {
        if (blockState.is(EIOTags.Blocks.DARK_STEEL_EXPLODABLE_ALLOW_LIST)) {
            return true;
        }
        if (blockState.is(EIOTags.Blocks.DARK_STEEL_EXPLODABLE_DENY_LIST) || blockEntity != null) {
            return false;
        }
        return Items.STONE_PICKAXE.isCorrectToolForDrops(blockState) ||
            (DarkSteelUpgradeable.hasUpgrade(itemStack, SpoonUpgrade.NAME) && Items.STONE_SHOVEL.isCorrectToolForDrops(blockState));
    }

    public static float adjustDestroySpeed(float inputSpeed, ItemStack pStack) {
        if (hasExplosiveUpgrades(pStack) && EnergyUtil.getEnergyStored(pStack) > 0) {
            //ramp slowdown until half speed is reached with Explosive II and Penetration II
            float maxReductionRatio = 0.5f;
            float areaAtMaxReduction = 5 * 5 * 3;
            AABB bounds = calculateMiningArea(pStack, Direction.NORTH);
            float miningArea = (float)(bounds.getXsize() * bounds.getYsize() * bounds.getZsize());
            float adjustBy = (miningArea / areaAtMaxReduction) * maxReductionRatio;
            adjustBy = Math.min(adjustBy, maxReductionRatio);
            return inputSpeed - (inputSpeed * adjustBy);
        }
        return inputSpeed;
    }

    private static AABB calculateMiningArea(ItemStack tool, Direction targetDir) {
        AABB miningBounds = new AABB(0,0,0,1,1,1);

        int radius = DarkSteelUpgradeable
            .getUpgradeAs(tool, ExplosiveUpgrade.NAME, ExplosiveUpgrade.class)
            .map(ExplosiveUpgrade::getMagnitude)
            .orElse(0);

        if (radius > 0) {
            Vector3d mask = new Vector3d(
                targetDir.getStepX() == 0 ? radius : 0,
                targetDir.getStepY() == 0 ? radius : 0,
                targetDir.getStepZ() == 0 ? radius : 0);
            miningBounds = miningBounds.expandTowards(mask.x, mask.y, mask.z);
            miningBounds = miningBounds.expandTowards(-mask.x, -mask.y, -mask.z);
        }

        int penetration = DarkSteelUpgradeable
            .getUpgradeAs(tool, ExplosivePenetrationUpgrade.NAME, ExplosivePenetrationUpgrade.class)
            .map(ExplosivePenetrationUpgrade::getMagnitude)
            .orElse(0);

        if (penetration > 0) {
            Vec3i shiftDir = targetDir.getNormal();
            shiftDir = shiftDir.multiply(-1);
            miningBounds = miningBounds.expandTowards(penetration * shiftDir.getX(), penetration * shiftDir.getY(), penetration * shiftDir.getZ());
        }
        return miningBounds;
    }


    // region area highlight

    @SubscribeEvent
    public static void showAreaOfEffectHighlight(DrawSelectionEvent.HighlightBlock event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.isCrouching() && hasExplosiveUpgrades(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            drawHighlight(event, player.getItemInHand(InteractionHand.MAIN_HAND));
        }
    }

    private static void drawHighlight(DrawSelectionEvent.HighlightBlock event, ItemStack held) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        BlockPos blockPos = event.getTarget().getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.isAir() || !level.getWorldBorder().isWithinBounds(blockPos)) {
            return;
        }

        AABB miningBounds = calculateMiningArea(held, event.getTarget().getDirection());
        VoxelShape outlineShape = Shapes.create(miningBounds);

        VertexConsumer vertexConsumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
        PoseStack poseStack = event.getPoseStack();

        Vec3 camPos = event.getCamera().getPosition();
        Vector3d origin = new Vector3d(blockPos.getX() - camPos.x(), blockPos.getY() - camPos.y(), blockPos.getZ() - camPos.z());
        Vector4f color = new Vector4f(1,0,0,0.2f);

        renderShape(poseStack, vertexConsumer, outlineShape, origin, color);

        AABB refBounds = new AABB(0,0,0,1,1,1);
        color = new Vector4f(0,0,0,0.2f);
        renderJoiningLines(poseStack, vertexConsumer, refBounds, miningBounds, origin, color);
    }

    private static void renderJoiningLines(PoseStack poseStack, VertexConsumer vertexConsumer, AABB refBounds, AABB miningBounds, Vector3d origin, Vector4f color) {
        var fromCorners = getCorners(refBounds);
        var toCorners = getCorners(miningBounds);
        for (int i=0;i<fromCorners.size();i++) {
            Vector3d from = fromCorners.get(i);
            Vector3d to = toCorners.get(i);
            addVertices(poseStack.last(), vertexConsumer, origin, from, to, color);
        }
    }

    private static List<Vector3d> getCorners(AABB aabb) {
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

    // endregion

}
