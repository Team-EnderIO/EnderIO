package com.enderio.enderio.content.armory;

import com.enderio.enderio.foundation.network.packets.ServerboundDoubleJumpPacket;
import com.enderio.enderio.init.EIOAttachments;
import com.enderio.enderio.init.EIOAttributes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Handles mid-air jumps granted by {@link EIOAttributes#EXTRA_JUMPS}.
 * <p>
 * Player movement is client authoritative, so the jump is started by the client (see {@code LocalPlayerMixin}),
 * which then tells the server so it can track the jump count and reset the fall distance.
 * Each side tracks its own count of used jumps, which is reset whenever the player lands.
 */
@EventBusSubscriber
public class DoubleJumpHandler {

    /**
     * Called on the client when the jump key is first pressed, at the point vanilla would try to start elytra flight.
     * Extra jumps are used up before the elytra is deployed.
     * @return whether a double jump was performed.
     */
    public static boolean tryClientDoubleJump(Player player) {
        if (!canDoubleJump(player)) {
            return false;
        }

        player.jumpFromGround();
        onDoubleJump(player);
        PacketDistributor.sendToServer(new ServerboundDoubleJumpPacket());
        return true;
    }

    public static void handleServerDoubleJump(ServerPlayer player) {
        // Movement is client authoritative so we can't undo the jump, but we can refuse to reset fall distance.
        if (!canDoubleJump(player)) {
            return;
        }

        onDoubleJump(player);

        // Normal jumps are detected by the server when the player leaves the ground, but this one is not, so mirror Player#jumpFromGround.
        player.awardStat(Stats.JUMP);
        player.causeFoodExhaustion(player.isSprinting() ? 0.2F : 0.05F);

        player.serverLevel().sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(),
            6, 0.25, 0.0, 0.25, 0.02);
    }

    private static boolean canDoubleJump(Player player) {
        if (player.onGround() || player.isSpectator() || player.isFallFlying() || player.isPassenger() || player.onClimbable()
            || player.isInLiquid() || player.hasEffect(MobEffects.LEVITATION)) {
            return false;
        }

        // Players who can fly use jump presses in the air to toggle flight.
        if (player.getAbilities().mayfly || player.getAbilities().flying) {
            return false;
        }

        int extraJumps = (int) player.getAttributeValue(EIOAttributes.EXTRA_JUMPS);
        return player.getData(EIOAttachments.EXTRA_JUMPS_USED) < extraJumps;
    }

    private static void onDoubleJump(Player player) {
        player.setData(EIOAttachments.EXTRA_JUMPS_USED, player.getData(EIOAttachments.EXTRA_JUMPS_USED) + 1);
        player.resetFallDistance();
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.hasData(EIOAttachments.EXTRA_JUMPS_USED)) {
            return;
        }

        if (player.onGround() || player.onClimbable() || player.isInLiquid() || player.getAbilities().flying) {
            player.removeData(EIOAttachments.EXTRA_JUMPS_USED);
        }
    }
}
