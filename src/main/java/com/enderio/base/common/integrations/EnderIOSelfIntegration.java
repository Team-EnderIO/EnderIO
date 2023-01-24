package com.enderio.base.common.integrations;

import com.enderio.api.glider.GliderMovementInfo;
import com.enderio.api.integration.ClientIntegration;
import com.enderio.api.integration.Integration;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class EnderIOSelfIntegration implements Integration {

    private GliderMovementInfo info = new GliderMovementInfo(0.01d, 1d, -0.05d, this);

    @Override
    public boolean isHangGliderDisabled(Player player) {
        return player.isFallFlying();
    }

    @Override
    public Optional<GliderMovementInfo> getGliderMovementInfo(Player player) {
        if (player.getMainHandItem().is(EIOTags.Items.GLIDER)) {
            return Optional.of(info);
        }
        return Optional.empty();
    }
    @Override
    public ClientIntegration getClientIntegration() {
        return EnderIOSelfClientIntegration.INSTANCE;
    }
}
