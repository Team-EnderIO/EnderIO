package com.enderio.core.common.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Map;

@EventBusSubscriber
public class EnderFakePlayerFactory {
    private static final Map<FakePlayerKey, EnderFakePlayer> fakePlayers = Maps.newHashMap();

    public static EnderFakePlayer get(ServerLevel level, GameProfile username) {
        FakePlayerKey key = new FakePlayerKey(level, username);
        return fakePlayers.computeIfAbsent(key, (k) -> new EnderFakePlayer(k.level(), k.username()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDimensionUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            fakePlayers.entrySet().removeIf((entry) -> (entry.getValue()).level() == level);
        }
    }

    private record FakePlayerKey(ServerLevel level, GameProfile username) {
    }
}
