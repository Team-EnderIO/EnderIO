package com.enderio.base.common.hangglider;

import com.enderio.EnderIOBase;
import com.enderio.base.api.UseOnly;
import com.enderio.base.api.glider.GliderMovementInfo;
import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.common.init.EIOCriterions;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID)
public class PlayerMovementHandler {

    /**
     * {@linkplain net.minecraft.world.entity.LivingEntity#travel} 0.91 multiplicator
     */
    private static final double AIR_FRICTION_COEFFICIENT = 1/0.91D;

    private static final double MOVEMENT_CHANGE_EFFECT = 0.05d;

    @UseOnly(LogicalSide.CLIENT)
    private static final Map<Player, Integer> TICKS_FALLING_CLIENT = new WeakHashMap<>();
    @UseOnly(LogicalSide.SERVER)
    private static final Map<Player, Integer> TICKS_FALLING_SERVER = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre playerTickEvent) {
        Player player = playerTickEvent.getEntity();
        Map<Player, Integer> ticksFallingMap = player instanceof ServerPlayer ? TICKS_FALLING_SERVER : TICKS_FALLING_CLIENT;

        int ticksFalling = ticksFallingMap.getOrDefault(player, 0);
        if (player.onGround() != player.getDeltaMovement().y() < 0) {
            ticksFallingMap.put(player, ticksFalling + 1);
        } else {
            ticksFallingMap.put(player, 0);
        }

        if (player.isSpectator()) {
            return;
        }

        Optional<GliderMovementInfo> gliderMovementInfoOpt = calculateGliderMovementInfo(player, true, ticksFallingMap);
        if (gliderMovementInfoOpt.isEmpty()) {
            return;
        }

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
        if (player instanceof ServerPlayer serverPlayer) {
            EIOCriterions.USE_GLIDER.get().trigger(serverPlayer);
            player.hurtMarked = true;
        } else if (player.level().isClientSide()) {
            ClientClassLoadingProtection.playSound(player);
        }

        gliderMovementInfo.cause().onHangGliderTick(player);
    }

    public static Optional<GliderMovementInfo> calculateGliderMovementInfo(Player player, boolean displayDisabledMessage, Map<Player, Integer> ticksFallingMap) {
        if (!player.onGround()
            && player.getDeltaMovement().y() < 0
            && !player.isShiftKeyDown()
            && !player.isInWater()
            && !player.isPassenger()
            && ticksFallingMap.getOrDefault(player, 0) > 12) {
            Optional<Component> disabledReason = IntegrationManager.getFirst(integration -> integration.hangGliderDisabledReason(player));
            Optional<GliderMovementInfo> gliderMovementInfo = IntegrationManager.getFirst(integration -> integration.getGliderMovementInfo(player));
            if (displayDisabledMessage && disabledReason.isPresent() && gliderMovementInfo.isPresent()) {
                player.displayClientMessage(EIOLang.GLIDER_DISABLED.copy().append(disabledReason.get()), true);
            }
            return gliderMovementInfo;
        }

        return Optional.empty();
    }

    private static class ClientClassLoadingProtection {
        private static void playSound(Player player) {
            if (player instanceof LocalPlayer localPlayer && !isGliderPlaying()) {
                Minecraft.getInstance().getSoundManager().play(new WindSoundInstance(localPlayer));
            }
        }

        private static boolean isGliderPlaying() {
            for (SoundInstance soundInstance : Minecraft.getInstance().getSoundManager().soundEngine.instanceBySource.get(SoundSource.PLAYERS)) {
                if (soundInstance instanceof WindSoundInstance) {
                    return true;
                }
            }

            return false;
        }
    }

    private static class WindSoundInstance extends AbstractTickableSoundInstance {
        private final LocalPlayer player;
        private int time;
        WindSoundInstance(LocalPlayer player) {
            super(SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            this.player = player;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.1F;
        }

        @Override
        public void tick() {

            ++this.time;
            if (!this.player.isRemoved() && (this.time <= 20 || PlayerMovementHandler.calculateGliderMovementInfo(player, false, TICKS_FALLING_CLIENT).isPresent())) {
                this.x = this.player.getX();
                this.y = this.player.getY();
                this.z = this.player.getZ();
                float f = (float)this.player.getDeltaMovement().lengthSqr();
                if (f >= 1.0E-7D) {
                    this.volume = Mth.clamp(f / 4.0F, 0.0F, 1.0F);
                } else {
                    this.volume = 0.0F;
                }

                if (this.time < 20) {
                    this.volume = 0.0F;
                } else if (this.time < 40) {
                    this.volume *= (this.time - 20) / 20.0F;
                }

                if (this.volume > 0.8F) {
                    this.pitch = 1.0F + (this.volume - 0.8F);
                } else {
                    this.pitch = 1.0F;
                }
            } else {
                this.stop();
            }
        }
    }
}
