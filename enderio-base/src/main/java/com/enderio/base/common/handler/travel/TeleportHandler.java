package com.enderio.base.common.handler.travel;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.item.darksteel.IDarkSteelItem;
import com.enderio.base.config.base.BaseConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;

/**
 * Thanks to the developers of <a href="https://github.com/castcrafter/travel_anchors">https://github.com/castcrafter/travel_anchors</a> for allowing us to use their code with our license.
 * For their agreements look at doc/license/castcrafter/travel-anchors/license.md in the repo root
 */

public class TeleportHandler {

    public static boolean canTeleport(Player player) {
        return canTeleport(player, InteractionHand.MAIN_HAND) || canTeleport(player, InteractionHand.OFF_HAND);
    }

    public static boolean canTeleport(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == EIOItems.TRAVEL_STAFF.get())
            return true;
        if (stack.getItem() instanceof IDarkSteelItem darkSteelItem) {
            //TODO: Check for upgrade;
        }
        return false;
    }

    public static boolean shortTeleport(Level level, Player player) {
        Optional<Vec3> pos = teleportPosition(level, player);
        if (pos.isPresent()){
            if (!level.isClientSide) {
                Optional<Vec3> eventPos = teleportEvent(player, pos.get());
                if (eventPos.isPresent()) {
                    player.teleportTo(eventPos.get().x(), eventPos.get().y(), eventPos.get().z());
                    player.fallDistance = 0;
                }
                player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean blockTeleport(Level level, Player player) {
        Optional<ITravelTarget> target = getAnchorTarget(player);
        if (target.isPresent()) {
            if (!player.getLevel().isClientSide) {
                Optional<Double> height = isValidGround(level, target.get().getPos());
                if (height.isEmpty()) {
                    return false;
                }
                BlockPos blockPos = target.get().getPos();
                Vec3 teleportPosition = new Vec3(blockPos.getX(), blockPos.getY()+height.get()+1, blockPos.getZ());
                teleportPosition = teleportEvent(player, teleportPosition).orElse(null);
                if (teleportPosition != null) {
                    player.fallDistance = 0;
                    player.teleportTo(teleportPosition.x(), teleportPosition.y(), teleportPosition.z());
                    player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                    return true;
                }
            }
        }
        return false;
    }

    public static Optional<Vec3> teleportPosition(Level level, Player player) {
        Vec3 targetVec = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 lookVec = player.getLookAngle().normalize();
        @Nullable BlockPos target = null;
        double floorHeight = 0;
        for (double i = BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_RANGE.get(); i >= 2; i -= 0.5) {
            Vec3 v3d = targetVec.add(lookVec.scale(i));
            target = new BlockPos(Math.round(v3d.x), Math.round(v3d.y), Math.round(v3d.z));
            Optional<Double> ground = isValidGround(level, target.below());
            if (ground.isPresent()) { //to use the same check as the anchors use the position below
                floorHeight = ground.get();
                break;
            } else {
                target = null;
            }
        }
        if (target == null) {
            return Optional.empty();
        }
        return Optional.of(Vec3.atBottomCenterOf(target).add(0, floorHeight, 0));
    }

    private static Optional<ITravelTarget> getAnchorTarget(Player player) {
        Vec3 positionVec = player.position().add(0, player.getEyeHeight(), 0);

        return TravelSavedData.getTravelData(player.getLevel()).getTravelTargetsInItemRange(new BlockPos(player.position()))
            .filter(target -> Math.abs(getAngleRadians(positionVec, target.getPos(), player.getYRot(), player.getXRot())) <= Math.toRadians(15))
            .filter(target -> isValidGround(player.getLevel(), target.getPos()).isPresent())
            .min(Comparator.comparingDouble(target ->
                Math.abs(getAngleRadians(positionVec, target.getPos(), player.getYRot(), player.getXRot()))
            ));
    }


    private static double getAngleRadians(Vec3 positionVec, BlockPos anchor, float yRot, float xRot) {
        Vec3 blockVec = new Vec3(anchor.getX() + 0.5 - positionVec.x, anchor.getY() + 1.0 - positionVec.y, anchor.getZ() + 0.5 - positionVec.z).normalize();
        Vec3 lookVec = Vec3.directionFromRotation(xRot, yRot).normalize();
        return Math.acos(lookVec.dot(blockVec));
    }

    /**
     *
     * @param level
     * @param target
     * @return Optional.empty if it can't teleport and the height where to place the player. This is so you can tp ontop of carpets up to a whole block
     */
    private static Optional<Double> isValidGround(BlockGetter level, BlockPos target) {
        if (!level.getBlockState(target.above(2)).canOcclude()) {
            BlockPos above = target.above();
            double height = level.getBlockState(above).getCollisionShape(level, above).max(Direction.Axis.Y);
            if (height > 0.2d && !level.getBlockState(target.above(3)).canOcclude() || height <=0.2d) {
                if (height == Double.NEGATIVE_INFINITY) {
                    height = 0;
                }
                return Optional.of(height);
            }
        }
        return Optional.empty();
    }

    private static Optional<Vec3> teleportEvent(Player player, Vec3 target) {
        EntityTeleportEvent event = new EntityTeleportEvent(player, target.x(), target.y(), target.z());
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return Optional.empty();
        }
        return Optional.of(new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ()));
    }
}
