package com.enderio.base.common.hangglider;

import com.enderio.api.glider.GliderMovementInfo;
import com.enderio.api.integration.IntegrationManager;
import com.enderio.base.common.advancement.UseGliderTrigger;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber
public class PlayerMovementHandler {

    /**
     * {@linkplain net.minecraft.world.entity.LivingEntity#travel} 0.91 multiplicator
     */
    private static final double AIR_FRICTION_COEFFICIENT = 1/0.91D;

    private static final double MOVEMENT_CHANGE_EFFECT = 0.05d;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent playerTickEvent) {
        Player player = playerTickEvent.player;
        if (playerTickEvent.phase == TickEvent.Phase.START) {
            Optional<GliderMovementInfo> gliderMovementInfoOpt = calculateGliderMovementInfo(player, true);
            if (gliderMovementInfoOpt.isEmpty())
                return;
            GliderMovementInfo gliderMovementInfo = gliderMovementInfoOpt.get();
            double verticalSpeed = gliderMovementInfo.fallSpeed();
            if (player.isSprinting()) {
                verticalSpeed *= 3;
            }
            double oldHorizontalSpeed = player.getDeltaMovement().horizontalDistance();
            double x = Math.cos(Math.toRadians(player.yHeadRot + 90)) * (gliderMovementInfo.acceleration() + oldHorizontalSpeed * MOVEMENT_CHANGE_EFFECT);
            double z = Math.sin(Math.toRadians(player.yHeadRot + 90)) * (gliderMovementInfo.acceleration() + oldHorizontalSpeed * MOVEMENT_CHANGE_EFFECT);


            Vec3 newDeltaMovement = new Vec3(player.getDeltaMovement().x() * (1 - MOVEMENT_CHANGE_EFFECT) + x, verticalSpeed, player.getDeltaMovement().z() * (1 - MOVEMENT_CHANGE_EFFECT) + z);
            double speed = newDeltaMovement.length();
            if (speed > gliderMovementInfo.maxSpeed()) {
                newDeltaMovement = newDeltaMovement.scale(gliderMovementInfo.maxSpeed() / newDeltaMovement.length());
            }
            newDeltaMovement = newDeltaMovement.scale(AIR_FRICTION_COEFFICIENT);
            player.setDeltaMovement(newDeltaMovement);
            player.fallDistance = 0f;
            if (!player.level.isClientSide()) {
                player.hurtMarked = true;
            }
            if (player instanceof ServerPlayer serverPlayer) {
                UseGliderTrigger.USE_GLIDER.trigger(serverPlayer);
            }
            gliderMovementInfo.cause().onHangGliderTick(player);
        }
    }
    public static Optional<GliderMovementInfo> calculateGliderMovementInfo(Player player, boolean displayDisabledMessage) {
        if (!player.isOnGround()
            && player.getDeltaMovement().y() < 0
            && !player.isShiftKeyDown()
            && !player.isInWater()
            && !player.isPassenger()) {
            Optional<Component> disabledReason = IntegrationManager.getFirst(integration -> integration.hangGliderDisabledReason(player));
            if (displayDisabledMessage && disabledReason.isPresent()) {
                player.displayClientMessage(EIOLang.GLIDER_DISABLED.copy().append(disabledReason.get()), true);
            }
            return IntegrationManager.getFirst(integration -> integration.getGliderMovementInfo(player));
        }

        return Optional.empty();
    }
}
