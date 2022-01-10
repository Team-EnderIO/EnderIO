package com.enderio.base.client;

import com.enderio.base.common.handler.travel.TeleportHandler;
import com.enderio.base.common.menu.SyncedMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {

    private static int tick = 0;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent e) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (e.phase == TickEvent.Phase.END && player != null) {
            tick++;
            //Sync Menu
            if (player.containerMenu instanceof SyncedMenu syncedMenu) {
                syncedMenu.clientTick();
            }

            //TravellingStaffParticle
            if (player.isShiftKeyDown() && TeleportHandler.canTeleport(player) && tick%3==0) {
                Optional<Vec3> pos = TeleportHandler.teleportPosition(player.getLevel(), player);
                if (pos.isPresent()) {
                    addParticle(pos.get());
                }
            }
        }
    }


    private static void addParticle(Vec3 pos) {
        int time = tick/3%20;
        float x = Mth.sin((float)(time*Math.PI)/10f);
        float z = Mth.cos((float)(time*Math.PI)/10f);
        @Nullable
        Particle particle = Minecraft.getInstance().levelRenderer.addParticleInternal(ParticleTypes.TOTEM_OF_UNDYING, false, true, pos.x() + x, pos.y() + 0.4, pos.z() + z, 0, 0, 0);
        if (particle != null) {
            particle.setLifetime(30);
            var color = getColor();
            particle.gravity = 0;
            particle.setColor(color.getLeft(), color.getMiddle(), color.getRight());
        }
    }

    private static Triple<Float, Float, Float> getColor() {
        Date date = new Date();
        if (date.getMonth() == Calendar.JUNE) {
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
