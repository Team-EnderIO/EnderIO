package com.enderio.core.common.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;

public class EnderFakePlayer extends FakePlayer {
    public EnderFakePlayer(ServerLevel level, GameProfile name) {
        super(level, name);
    }

    public void setMaxAttackStrength() {
        // Set to max value so player will have full strength.
        this.attackStrengthTicker = Integer.MAX_VALUE;
    }
}
