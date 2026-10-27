package com.enderio.core.common.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;

public class EnderFakePlayer extends FakePlayer {
    public EnderFakePlayer(ServerLevel level, GameProfile name) {
        super(level, name);
    }

    /**
     * NeoForge fake players are permanently invulnerable, which also stops mobs from targeting them.
     * We still want mobs to retaliate against (and path towards) whatever is using this player, while it stays immune to damage.
     */
    @Override
    public boolean canBeSeenAsEnemy() {
        return canBeSeenByAnyone();
    }

    /**
     * Turn the player to look horizontally in the given direction.
     */
    public void faceDirection(Direction direction) {
        float yRot = direction.toYRot();
        setYRot(yRot);
        setYHeadRot(yRot);
        setXRot(0);
        this.yRotO = yRot;
        this.xRotO = 0;
    }

    public void setMaxAttackStrength() {
        // Set to max value so player will have full strength.
        this.attackStrengthTicker = Integer.MAX_VALUE;
    }
}
