package com.enderio.armory.common.item.darksteel.upgrades.explosive;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.item.darksteel.upgrades.SpoonUpgrade;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.network.EmitParticlesPacket;
import com.enderio.core.common.util.BlockUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector4f;

@EventBusSubscriber(value = Dist.CLIENT)
public class ExplosiveUpgradeHandler {

    private static final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_BREAK_POWER_USE = ArmoryConfig.COMMON.EXPLOSIVE_ENERGY_PER_EXPLODED_BLOCK;

    private static final Random RAND = new Random();

    public static boolean hasExplosiveUpgrades(ItemStack stack) {
        return DarkSteelHelper.hasUpgrade(stack, ExplosiveUpgrade.NAME)
                || DarkSteelHelper.hasUpgrade(stack, ExplosivePenetrationUpgrade.NAME);
    }

    public static void onMineBlock(ItemStack pStack, Level pLevel, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pEntityLiving instanceof Player player && !player.isCrouching() && hasExplosiveUpgrades(pStack)
                && ItemStackEnergy.getEnergyStored(pStack) > 0) {

            BlockHitResult hit = Item.getPlayerPOVHitResult(pLevel, player, ClipContext.Fluid.NONE);
            if (pPos.equals(hit.getBlockPos())) {
                EmitParticlesPacket particles = new EmitParticlesPacket();
                if (explodeArea(pStack, pLevel, player, hit, particles)) {
                    if (pLevel instanceof ServerLevel serverLevel) {
                        PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pPos), particles);
                    }
                }
            }
        }
    }

    private static boolean explodeArea(ItemStack pStack, Level pLevel, Player player, BlockHitResult hit,
            EmitParticlesPacket particles) {
        boolean didExplode = false;
        AABB bb = calculateMiningArea(pStack, hit.getDirection());
        bb = bb.move(hit.getBlockPos());
        for (BlockPos minePos : BlockPos.betweenClosed((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX - 1,
                (int) bb.maxY - 1, (int) bb.maxZ - 1)) {
            if (!hit.getBlockPos().equals(minePos)) {
                didExplode = explodeBlock(pStack, pLevel, minePos, player, particles) || didExplode;
            }
        }
        return didExplode;
    }

    private static boolean explodeBlock(ItemStack itemStack, Level level, BlockPos minePos, Player player,
            EmitParticlesPacket particles) {
        if (!level.isInWorldBounds(minePos) || ItemStackEnergy.getEnergyStored(itemStack) <= 0) {
            return false;
        }
        BlockState blockState = level.getBlockState(minePos);
        if (!canExplode(itemStack, blockState, level.getBlockEntity(minePos))) {
            return false;
        }
        if (BlockUtil.removeBlock(level, player, itemStack, minePos)) {
            ItemStackEnergy.extractEnergy(itemStack, EXPLOSIVE_BREAK_POWER_USE.get(), false);
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
        if (blockState.is(ArmoryTags.Blocks.DARK_STEEL_EXPLODABLE_ALLOW_LIST)) {
            return true;
        }
        if (blockState.is(ArmoryTags.Blocks.DARK_STEEL_EXPLODABLE_DENY_LIST) || blockEntity != null) {
            return false;
        }
        return Items.STONE_PICKAXE.isCorrectToolForDrops(new ItemStack(Items.STONE_PICKAXE), blockState)
                || ((DarkSteelHelper.hasUpgrade(itemStack, SpoonUpgrade.NAME)
                        && Items.STONE_SHOVEL.isCorrectToolForDrops(new ItemStack(Items.STONE_SHOVEL), blockState)));
    }

    public static float adjustDestroySpeed(float inputSpeed, ItemStack pStack) {
        if (hasExplosiveUpgrades(pStack) && ItemStackEnergy.getEnergyStored(pStack) > 0) {
            // ramp slowdown until half speed is reached with Explosive II and Penetration
            // II
            float maxReductionRatio = 0.5f;
            float areaAtMaxReduction = 5 * 5 * 3;
            AABB bounds = calculateMiningArea(pStack, Direction.NORTH);
            float miningArea = (float) (bounds.getXsize() * bounds.getYsize() * bounds.getZsize());
            float adjustBy = (miningArea / areaAtMaxReduction) * maxReductionRatio;
            adjustBy = Math.min(adjustBy, maxReductionRatio);
            return inputSpeed - (inputSpeed * adjustBy);
        }
        return inputSpeed;
    }

    private static AABB calculateMiningArea(ItemStack tool, Direction targetDir) {
        AABB miningBounds = new AABB(0, 0, 0, 1, 1, 1);

        int radius = DarkSteelHelper.getUpgradeAs(tool, ExplosiveUpgrade.NAME, ExplosiveUpgrade.class)
                .map(ExplosiveUpgrade::getMagnitude)
                .orElse(0);

        if (radius > 0) {
            Vector3d mask = new Vector3d(targetDir.getStepX() == 0 ? radius : 0, targetDir.getStepY() == 0 ? radius : 0,
                    targetDir.getStepZ() == 0 ? radius : 0);
            miningBounds = miningBounds.expandTowards(mask.x, mask.y, mask.z);
            miningBounds = miningBounds.expandTowards(-mask.x, -mask.y, -mask.z);
        }

        int penetration = DarkSteelHelper
                .getUpgradeAs(tool, ExplosivePenetrationUpgrade.NAME, ExplosivePenetrationUpgrade.class)
                .map(ExplosivePenetrationUpgrade::getMagnitude)
                .orElse(0);

        if (penetration > 0) {
            Vec3i shiftDir = targetDir.getNormal();
            shiftDir = shiftDir.multiply(-1);
            miningBounds = miningBounds.expandTowards(penetration * shiftDir.getX(), penetration * shiftDir.getY(),
                    penetration * shiftDir.getZ());
        }
        return miningBounds;
    }

    // region area highlight

    @SubscribeEvent
    public static void showAreaOfEffectHighlight(RenderHighlightEvent.Block event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.isCrouching()
                && hasExplosiveUpgrades(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            drawHighlight(event, player.getItemInHand(InteractionHand.MAIN_HAND));
        }
    }

    private static void drawHighlight(RenderHighlightEvent.Block event, ItemStack held) {
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
        Vector3d origin = new Vector3d(blockPos.getX() - camPos.x(), blockPos.getY() - camPos.y(),
                blockPos.getZ() - camPos.z());
        Vector4f color = new Vector4f(1, 0, 0, 0.2f);

        renderShape(poseStack, vertexConsumer, outlineShape, origin, color);

        AABB refBounds = new AABB(0, 0, 0, 1, 1, 1);
        color = new Vector4f(0, 0, 0, 0.2f);
        renderJoiningLines(poseStack, vertexConsumer, refBounds, miningBounds, origin, color);
    }

    private static void renderJoiningLines(PoseStack poseStack, VertexConsumer vertexConsumer, AABB refBounds,
            AABB miningBounds, Vector3d origin, Vector4f color) {
        List<Vector3d> fromCorners = getCorners(refBounds);
        List<Vector3d> toCorners = getCorners(miningBounds);
        for (int i = 0; i < fromCorners.size(); i++) {
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

    private static void renderShape(PoseStack pPoseStack, VertexConsumer pConsumer, VoxelShape pShape, Vector3d origin,
            Vector4f color) {
        PoseStack.Pose pose = pPoseStack.last();
        pShape.forAllEdges((pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ) -> addVertices(pose, pConsumer, origin.x,
                origin.y, origin.z, pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ, color));
    }

    private static void addVertices(PoseStack.Pose pose, VertexConsumer pConsumer, Vector3d origin, Vector3d from,
            Vector3d to, Vector4f color) {
        addVertices(pose, pConsumer, origin.x, origin.y, origin.z, from.x, from.y, from.z, to.x, to.y, to.z, color);
    }

    private static void addVertices(PoseStack.Pose pose, VertexConsumer pConsumer, double originX, double originY,
            double originZ, double fromX, double fromY, double fromZ, double toX, double toY, double toZ,
            Vector4f color) {

        // create normal
        float normalX = (float) (toX - fromX);
        float normalY = (float) (toY - fromY);
        float normalZ = (float) (toZ - fromZ);
        float length = Mth.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        // normalize
        normalX = normalX / length;
        normalY = normalY / length;
        normalZ = normalZ / length;

        pConsumer
                .addVertex(pose.pose(), (float) (fromX + originX), (float) (fromY + originY), (float) (fromZ + originZ))
                .setColor(color.x(), color.y(), color.z(), color.w())
                .setNormal(pose, normalX, normalY, normalZ);
        pConsumer.addVertex(pose.pose(), (float) (toX + originX), (float) (toY + originY), (float) (toZ + originZ))
                .setColor(color.x(), color.y(), color.z(), color.w())
                .setNormal(pose, normalX, normalY, normalZ);
    }

    // endregion

}
