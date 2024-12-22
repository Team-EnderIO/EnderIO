package com.enderio.machines.common.integrations;

import com.enderio.base.api.integration.Integration;
import com.enderio.machines.common.blocks.travel_anchor.TravelAnchorBlock;
import net.minecraft.world.entity.player.Player;

public class EnderIOMachinesSelfIntegration implements Integration {

    public static final EnderIOMachinesSelfIntegration INSTANCE = new EnderIOMachinesSelfIntegration();

    @Override
    public boolean canBlockTeleport(Player player) {
        return player.level().getBlockState(player.blockPosition().below()).getBlock() instanceof TravelAnchorBlock;
    }
}
