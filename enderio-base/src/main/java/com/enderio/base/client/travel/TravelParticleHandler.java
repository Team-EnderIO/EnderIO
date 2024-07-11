package com.enderio.base.client.travel;

import com.enderio.EnderIOBase;
import com.enderio.base.common.handler.TravelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID, value = Dist.CLIENT)
public class TravelParticleHandler {
    private static int tick = 0;

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post e) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            tick++;
            if (tick % 3 == 0 && player.onGround() && player.isShiftKeyDown() && TravelHandler.canItemTeleport(player)) {
                TravelHandler.teleportPosition(player.level(), player).ifPresent(TravelParticleHandler::addTravelParticle);
            }
        }
    }

    private static void addTravelParticle(Vec3 pos) {
        int time = tick/3%20;
        float x = Mth.sin((float)(time*Math.PI)/10f);
        float z = Mth.cos((float)(time*Math.PI)/10f);
        @Nullable Particle particle = Minecraft.getInstance().levelRenderer.addParticleInternal(
            ParticleTypes.TOTEM_OF_UNDYING, false, true, pos.x() + x, pos.y() + 0.4, pos.z() + z, 0, 0, 0);
        if (particle != null) {
            particle.setLifetime(30);
            var color = getTravelParticleColor();
            particle.gravity = 0;
            particle.setColor(color.getLeft(), color.getMiddle(), color.getRight());
        }
    }

    private static Triple<Float, Float, Float> getTravelParticleColor() {
        if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JUNE) {
            int halfTick = tick/3%10;
            return switch (halfTick) {
                case 0: yield Triple.of(228/255f, 3/255f, 3/255f);
                case 1: yield Triple.of(255/255f, 140/255f, 0/255f);
                case 2: yield Triple.of(255/255f, 237/255f, 0/255f);
                case 3: yield Triple.of(0/255f, 128/255f, 38/255f);
                case 4: yield Triple.of(0/255f, 77/255f, 255/255f);
                case 5: yield Triple.of(117/255f, 7/255f, 135/255f);
                default: yield Triple.of(255/255f, 255/255f, 255/255f);
            };
        }
        return Triple.of(117/255f, 7/255f, 135/255f);
    }
}
